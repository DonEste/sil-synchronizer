package com.sil.sil_synchronizer.jobs.dgaSync;

import com.sil.sil_synchronizer.dtos.DgaRequiredInformationDto;
import com.sil.sil_synchronizer.dtos.StationConfigurationDto;
import com.sil.sil_synchronizer.entities.ViewArchivedInformationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

@Slf4j
public class DgaSyncProcessor implements ItemProcessor<Map<String, Object>, DgaRequiredInformationDto> {

    public DgaSyncProcessor() {

    }

    @Override
    public DgaRequiredInformationDto process(Map<String, Object> map) {
        StationConfigurationDto stationConfiguration = (StationConfigurationDto) map.get("station_data");
        ViewArchivedInformationEntity archivedInformation = (ViewArchivedInformationEntity) map.get("informationData");

        DgaRequiredInformationDto response = new DgaRequiredInformationDto();

        response.setStationNumber(stationConfiguration.getStationNumber());
        response.setSiteCode(stationConfiguration.getSiteCode());
        response.setFlow(archivedInformation.getFlow());
        response.setPhreaticLevel(archivedInformation.getPhreaticLevel());
        response.setTotalizer(archivedInformation.getTotalizer());
        response.setDate(archivedInformation.getDate());

        return response;
    }
}
