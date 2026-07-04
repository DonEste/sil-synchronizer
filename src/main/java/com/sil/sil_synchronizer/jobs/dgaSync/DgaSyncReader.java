package com.sil.sil_synchronizer.jobs.dgaSync;

import com.sil.sil_synchronizer.dtos.DgaRequiredInformationDto;
import com.sil.sil_synchronizer.dtos.StationConfigurationDto;
import com.sil.sil_synchronizer.entities.DgaRegistryLogEntity;
import com.sil.sil_synchronizer.entities.ViewArchivedInformationEntity;
import com.sil.sil_synchronizer.repositories.IDgaRegistryLogDao;
import com.sil.sil_synchronizer.repositories.IViewArchivedInformationDao;
import com.sil.sil_synchronizer.services.DgaClientService;
import com.sil.sil_synchronizer.services.NotificationService;
import com.sil.sil_synchronizer.Variables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DgaSyncReader implements ItemReader<Map<String, Object>>, ItemStream {

    private final Variables variables;

    private final IViewArchivedInformationDao viewArchivedInformationDao;

    private final IDgaRegistryLogDao dgaRegistryLogDao;

    private List<StationConfigurationDto> stationConfigurationDtos;

    private List<ViewArchivedInformationEntity> archivedInformationToSave;

    private List<DgaRegistryLogEntity> dgaRegistryLogs;

    private int resultCount;

    private int stationCount;

    private final DgaClientService dgaClientService;

    private final NotificationService notificationService;

    public DgaSyncReader(Variables variables, IViewArchivedInformationDao viewArchivedInformationDao, IDgaRegistryLogDao dgaRegistryLogDao, DgaClientService dgaClientService, NotificationService notificationService) {
        this.variables = variables;
        this.viewArchivedInformationDao = viewArchivedInformationDao;
        this.dgaRegistryLogDao = dgaRegistryLogDao;
        this.resultCount = 0;
        this.stationCount = 0;
        this.dgaClientService = dgaClientService;
        this.notificationService = notificationService;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        //Get execution variables from properties file and fill stationConfigurationDtos
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //If the execution is in production, then use the dga_sync_properties.json, otherwise, use the example in the resources folder
            InputStream inputStream = variables.getEnvironment().equals("PRODUCTION") ?
                    new FileInputStream(variables.getPropertiesFilename()) :
                    getClass().getClassLoader().getResourceAsStream("dga_sync_properties.json");
            TypeReference<List<StationConfigurationDto>> typeReference = new TypeReference<>() {
            };
            stationConfigurationDtos = objectMapper.readValue(inputStream, typeReference);

        } catch (Exception e) {
            log.error("Ha ocurrido el siguiente error: {}", e.getMessage());
            e.printStackTrace();
            notificationService.reportError(e);
            throw new ItemStreamException(e.getMessage());
        }

        //Get last sent status to the DGA for each configured station
        this.dgaRegistryLogs = dgaRegistryLogDao.findLastByInformationNumber(stationConfigurationDtos.stream().map(StationConfigurationDto::getSiteCode).collect(Collectors.toList()));

        //Log if some stations data have not been sent to the DGA in more hours than the defined amount
        for (DgaRegistryLogEntity dgaRegistryLog : dgaRegistryLogs) {
            if (getHoursDiff(dgaRegistryLog.getDate(), new Date()) > variables.getHoursRegressionTrigger()) {
                log.warn("La información de la estación {} no ha sido enviada a la DGA desde hace {} horas", dgaRegistryLog.getStationId(), getHoursDiff(dgaRegistryLog.getDate(), new Date()));
                notificationService.reportError(
                        new Exception(
                                String.format("La información de la estación %d no ha sido enviada a la DGA desde hace %d horas", dgaRegistryLog.getStationId(), getHoursDiff(dgaRegistryLog.getDate(), new Date()))
                        )
                );
            }
        }
    }

    @Override
    public Map<String, Object> read() throws Exception {
        //Uncomment to test with data set manually
//        if(true){
//            DgaRequiredInformationDto test = new DgaRequiredInformationDto();
//            test.setStationNumber(1);
//            test.setSiteCode("OC-1002-22");
//            test.setFlow(0L);
//            test.setTotalizer(0L);
//            test.setPhreaticLevel(0L);
//            test.setDate(new Date());
//
//            dgaClientService.sendDataExtrationToDga(test);
//            return null;
//        }

        //If there is not a valid configuration file loaded, then finish the job
        if (stationConfigurationDtos == null || stationConfigurationDtos.isEmpty()) {
            return null;
        }

        //If the stationCount is less than the amount of stations configured, then process the station number
        if (stationCount <= stationConfigurationDtos.size()) {
            //If resultCount is 0 then we are starting to send a station data... therefore we have to retrieve it form DB
            if (resultCount == 0) {
                //Prepare start date to read
                DgaRegistryLogEntity dgaRegistryLog = dgaRegistryLogs.stream().filter(d -> d.getStationId().equals(stationConfigurationDtos.get(stationCount).getStationNumber())).findFirst().orElse(null);
                Date startDate = DateUtils.addHours(new Date(), (int) (dgaRegistryLog != null ? -this.getHoursDiff(dgaRegistryLog.getDate(), new Date()) : -1L));

                //Search in DB
                archivedInformationToSave = viewArchivedInformationDao.findHourlyAverage(
                        stationConfigurationDtos.stream().map(StationConfigurationDto::returnInformationNumbers).flatMap(Collection::stream).collect(Collectors.toList()),
                        startDate,
                        stationConfigurationDtos.get(stationCount).getFlowInfNumber(),
                        stationConfigurationDtos.get(stationCount).getTotalizerInfNumber(),
                        stationConfigurationDtos.get(stationCount).getPhreaticLevelInfNumber(),
                        variables.getHoursSearchOffset()
                );
            }

            //If there is information to process in the current station, then send it to the process
            if (resultCount < archivedInformationToSave.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("station_data", stationConfigurationDtos.get(stationCount));
                response.put("informationData", archivedInformationToSave.get(resultCount));

                resultCount++;
                return response;
            }
            resultCount = 0;
            stationCount++;
        }

        //Otherwise, finish the job
        return null;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        //NADA
    }

    @Override
    public void close() throws ItemStreamException {
        //NADA
    }

    //Calculate difference in hours between two dates
    public long getHoursDiff(Date startDate, Date endDate) {
        long secs = (endDate.getTime() - startDate.getTime()) / 1000;

        return secs / 3600;
    }
}
