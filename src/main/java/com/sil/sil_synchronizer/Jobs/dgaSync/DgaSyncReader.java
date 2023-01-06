package com.sil.sil_synchronizer.Jobs.dgaSync;

import com.sil.sil_synchronizer.Dtos.StationConfigurationDto;
import com.sil.sil_synchronizer.Entities.DgaRegisrtyLogEntity;
import com.sil.sil_synchronizer.Entities.ViewArchivedInformationEntity;
import com.sil.sil_synchronizer.Repositories.IDgaRegistryLogDao;
import com.sil.sil_synchronizer.Repositories.IViewArchivedInformationDao;
import com.sil.sil_synchronizer.Variables;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
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

    private List<ViewArchivedInformationEntity> viewArchivedInformationToSave;

    private List<DgaRegisrtyLogEntity> dgaRegistryLogs;

    public DgaSyncReader(Variables variables, IViewArchivedInformationDao viewArchivedInformationDao, IDgaRegistryLogDao dgaRegistryLogDao) {
        this.variables = variables;
        this.viewArchivedInformationDao = viewArchivedInformationDao;
        this.dgaRegistryLogDao = dgaRegistryLogDao;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        //Get execution variables from properties file and fill stationConfigurationDtos
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = new FileInputStream(variables.getPropertiesFilename());
            TypeReference<List<StationConfigurationDto>> typeReference = new TypeReference<>() {
            };
            stationConfigurationDtos = objectMapper.readValue(inputStream, typeReference);
        } catch (Exception e) {
            log.error("Ha ocurrido el siguiente error: {}", e.getMessage());
            e.printStackTrace();
            //TODO: enviar alerta
            throw new ItemStreamException(e.getMessage());
        }

        //Get last sent status to the DGA
        dgaRegistryLogs = dgaRegistryLogDao.findLastByInformationNumber(stationConfigurationDtos.stream().map(StationConfigurationDto::getSiteCode).collect(Collectors.toList()));

        //Check last sent data
        long offlineHours = 1;
        if (!dgaRegistryLogs.isEmpty() && getHoursDiff(dgaRegistryLogs.get(0).getDate(), new Date()) > variables.getHoursRegressionTrigger()) {
            offlineHours = getHoursDiff(dgaRegistryLogs.get(0).getDate(), new Date());
            log.warn("No se han enviado datos a la DGA durante {} horas", offlineHours);
            //TODO: enviar alerta
        }

        //Set how many hours back do we have to bring from DB
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.HOUR, offlineHours > variables.getHoursRegressionRetry() ? -variables.getHoursRegressionRetry() : (int) -offlineHours);

        //Bring data from DB
        viewArchivedInformationToSave = viewArchivedInformationDao.findLastByInformationNumber(
                stationConfigurationDtos.stream().map(StationConfigurationDto::returnInformationNumbers).flatMap(Collection::stream).collect(Collectors.toList()),
                startDate.getTime()
        );
    }

    @Override
    public Map<String, Object> read() {
        if (!stationConfigurationDtos.isEmpty()) {
            StationConfigurationDto currentStation = stationConfigurationDtos.get(0);

            List<ViewArchivedInformationEntity> thisViewArchivedInformationToSave = viewArchivedInformationToSave.stream().filter(v -> currentStation.returnInformationNumbers().contains(v.getNumberInStation())).collect(Collectors.toList());

            ViewArchivedInformationEntity auxInformation = thisViewArchivedInformationToSave.remove(0);

            if (thisViewArchivedInformationToSave.isEmpty()) {
                stationConfigurationDtos.remove(0);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("station_data", currentStation);
            response.put("informationData", auxInformation);
        }

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

    public long getHoursDiff(Date startDate, Date endDate) {
        long secs = (endDate.getTime() - startDate.getTime()) / 1000;
        long hours = secs / 3600;

        return hours;
    }
}
