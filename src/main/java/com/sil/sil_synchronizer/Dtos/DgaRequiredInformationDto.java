package com.sil.sil_synchronizer.Dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class DgaRequiredInformationDto {

    private int stationNumber;

    private String siteCode;

    private double flow;

    private double phreaticLevel;

    private double totalizer;

    private Date date;
}
