package com.sil.sil_synchronizer.Jobs.dgaSync;

import com.sil.sil_synchronizer.Dtos.DgaRequiredInformationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

@Slf4j
public class DgaSyncProcessor implements ItemProcessor<Map<String, Object>, DgaRequiredInformationDto> {

    public DgaSyncProcessor(){

    }

    @Override
    public DgaRequiredInformationDto process(Map<String, Object> map) {
        return null;
    }
}
