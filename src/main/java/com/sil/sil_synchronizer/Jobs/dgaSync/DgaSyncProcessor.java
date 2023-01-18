package com.sil.sil_synchronizer.Jobs.dgaSync;

import com.sil.sil_synchronizer.Dtos.DgaRequiredInformationDto;
import com.sil.sil_synchronizer.Dtos.StationConfigurationDto;
import com.sil.sil_synchronizer.Entities.ViewArchivedInformationEntity;
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
