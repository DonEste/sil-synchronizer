package com.sil.sil_synchronizer.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class DgaRequiredInformationDto {

    private long stationNumber;

    private String siteCode;

    private double flow;

    private double phreaticLevel;

    private double totalizer;

    private Date date;

    @Override
    public String toString() {
        return "DgaRequiredInformationDto{" + "stationNumber=" + stationNumber +
                ", siteCode='" + siteCode + '\'' +
                ", flow=" + flow +
                ", phreaticLevel=" + phreaticLevel +
                ", totalizer=" + totalizer +
                ", date=" + date +
                '}';
    }
}
