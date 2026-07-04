package com.sil.sil_synchronizer.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "Dga_Registry_Log")
@Data
@NoArgsConstructor
//Entity used to save dga sent data logs in db (table wil be created automatically if does not exist)
public class DgaRegistryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", insertable = false, nullable = false, updatable = false)
    private Long id;

    @Column(name = "SITE_code", insertable = false, nullable = false, updatable = false)
    private String siteCode;

    @Column(name = "STA_Id", insertable = false, nullable = false, updatable = false)
    private Long stationId;

    @Column(name = "INF_Flow", nullable = false, updatable = false)
    private Double flow;

    @Column(name = "INF_Phreatic_level", nullable = false, updatable = false)
    private Double phreaticLevel;

    @Column(name = "INF_Totalizer", nullable = false, updatable = false)
    private Double totalizer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INF_Date", nullable = false, updatable = false) //Informed date
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_Date", nullable = false, updatable = false) //Actual date where it was informed
    private Date registryDate;

    @Column(name = "REG_Id", nullable = false, updatable = false) //Actual date where it was informed
    private String registryId;

    @Override
    public String toString() {
        return "DgaRegistryLogEntity{" +
                "id=" + id +
                ", siteCode='" + siteCode + '\'' +
                ", stationId=" + stationId +
                ", flow=" + flow +
                ", phreaticLevel=" + phreaticLevel +
                ", totalizer=" + totalizer +
                ", date=" + date +
                ", registryDate=" + registryDate +
                ", registryId='" + registryId + '\'' +
                '}';
    }
}
