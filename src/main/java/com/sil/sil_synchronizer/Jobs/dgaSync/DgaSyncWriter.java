package com.sil.sil_synchronizer.Jobs.dgaSync;

import com.sil.sil_synchronizer.Dtos.DgaRequiredInformationDto;
import com.sil.sil_synchronizer.Entities.DgaRegistryLogEntity;
import com.sil.sil_synchronizer.Repositories.IDgaRegistryLogDao;
import com.sil.sil_synchronizer.Services.DgaClientService;
import com.sil.sil_synchronizer.Variables;
import com.sil.sil_synchronizer.webservices.wsdl.AuthSendDataExtraccionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Slf4j
public class DgaSyncWriter implements ItemWriter<DgaRequiredInformationDto> {

    private final Variables variables;

    private final IDgaRegistryLogDao dgaRegistryLogDao;

    @Autowired
    DgaClientService dgaClientService;

    public DgaSyncWriter(Variables variables, IDgaRegistryLogDao dgaRegistryLogDao) {
        this.variables = variables;
        this.dgaRegistryLogDao = dgaRegistryLogDao;
    }

    @Override
    public void write(List<? extends DgaRequiredInformationDto> items) throws Exception {
        //If the environment is set to PRODUCTION, then send the data to the DGA and save log
        if (variables.getEnvironment().equals("PRODUCTION")) {
            for (DgaRequiredInformationDto item : items) {
                int attempts = 0;
                while (true) {
                    try {
                        //Send data to the DGA
                        AuthSendDataExtraccionResponse response = dgaClientService.sendDataExtrationToDga(item);

                        //Create log Object
                        DgaRegistryLogEntity dgaLog = new DgaRegistryLogEntity();
                        dgaLog.setSiteCode(item.getSiteCode());
                        dgaLog.setStationId(item.getStationNumber());
                        dgaLog.setFlow(item.getFlow());
                        dgaLog.setPhreaticLevel(item.getPhreaticLevel());
                        dgaLog.setTotalizer(item.getTotalizer());
                        dgaLog.setDate(item.getDate());
                        dgaLog.setRegistryDate(new Date());
                        dgaLog.setRegistryId(response.getNumeroComprobante());

                        //Save log
                        dgaRegistryLogDao.save(dgaLog);
                        log.info("Se ha guardado el log en bd: {}", dgaLog);

                        variables.setSavedRegistries(1);
                        break;
                    } catch (Exception e) {
                        if (attempts >= variables.getDgaWebServicesMaxAttempts()) {
                            log.error("Se ha superado el número máximo de intentos");
                            throw e;
                        }
                        attempts++;
                    }
                }
            }
        } else {
            for (DgaRequiredInformationDto item : items) {
                log.info("The final object to send is: {}", item);
            }
        }
    }
}