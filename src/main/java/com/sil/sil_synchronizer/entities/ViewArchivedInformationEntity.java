package com.sil.sil_synchronizer.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "View_ArchivedInformations")
@Data
@NoArgsConstructor
public class ViewArchivedInformationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", insertable = false, nullable = false, updatable = false)
    private Long id;

    @Column(name = "STA_SiteNumber", insertable = false, nullable = false, updatable = false)
    private long stationId;

    @Column(name = "INF_NumberInStation", insertable = false, nullable = false, updatable = false)
    private Long informationNumber;

    @Column(name = "INF_Date", insertable = false, nullable = false, updatable = false)
    private Date date;

    @Column(name = "INF_Flow", insertable = false, nullable = false, updatable = false)
    private Long flow;

    @Column(name = "INF_Totalizer", insertable = false, nullable = false, updatable = false)
    private Long totalizer;

    @Column(name = "INF_PhreaticLevel", insertable = false, nullable = false, updatable = false)
    private Long phreaticLevel;
}
