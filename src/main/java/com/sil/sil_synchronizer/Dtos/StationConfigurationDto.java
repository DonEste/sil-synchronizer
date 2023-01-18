package com.sil.sil_synchronizer.Dtos;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("numero_estacion")
    private Long stationNumber;

    @JsonProperty("codigo_obra")
    @SerializedName("codigo_obra")
    private String siteCode;

    @JsonProperty("numero_informacion_nivel_freatico")
    @SerializedName("numero_informacion_nivel_freatico")
    private Long phreaticLevelInfNumber;

    @JsonProperty("numero_informacion_totalizador")
    @SerializedName("numero_informacion_totalizador")
    private Long totalizerInfNumber;

    @JsonProperty("numero_informacion_caudal")
    @SerializedName("numero_informacion_caudal")
    private Long flowInfNumber;

    public List<Long> returnInformationNumbers(){
        return Arrays.asList(phreaticLevelInfNumber, totalizerInfNumber, flowInfNumber);
    }

    @Override
    public String toString() {
        return "StationConfigurationDto{" +
                "stationNumber=" + stationNumber +
                ", siteCode='" + siteCode + '\'' +
                ", phreaticLevelInfNumber=" + phreaticLevelInfNumber +
                ", totalizerInfNumber=" + totalizerInfNumber +
                ", flowInfNumber=" + flowInfNumber +
                '}';
    }
}
