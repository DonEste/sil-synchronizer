package com.sil.sil_synchronizer.Dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Data
public class StationConfigurationDto {

    @JsonProperty("numero_estacion")
    private int stationNumber;

    @JsonProperty("codigo_obra")
    private String siteCode;

    @JsonProperty("numero_informacion_nivel_freatico")
    private long phreaticLevelInfNumber;

    @JsonProperty("numero_informacion_totalizador")
    private long totalizerInfNumber;

    @JsonProperty("numero_informacion_caudal")
    private long flowInfNumber;

    public List<Long> returnInformationNumbers(){
        return Arrays.asList(phreaticLevelInfNumber, totalizerInfNumber, flowInfNumber);
    }
}
