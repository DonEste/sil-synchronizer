package com.sil.sil_synchronizer.Jobs.dgaSync;

import com.sil.sil_synchronizer.Dtos.DgaRequiredInformationDto;
import com.sil.sil_synchronizer.Variables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class DgaSyncWriter implements ItemWriter<DgaRequiredInformationDto> {

    private final Variables variables;

    public DgaSyncWriter(Variables variables) {
        this.variables = variables;
    }

    @Override
    public void write(List<? extends DgaRequiredInformationDto> items) {
        log.debug("***** {} Records have been saved in DB  *****", 0);
    }
}