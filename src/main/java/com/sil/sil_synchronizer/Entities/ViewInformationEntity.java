package com.sil.sil_synchronizer.Entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "View_Informations")
@Data
@NoArgsConstructor
public class ViewInformationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", insertable = false, nullable = false, updatable = false)
    private Long id;

    @Column(name = "STA_Label", insertable = false, nullable = false, updatable = false)
    private String stationLabel;

    @Column(name = "INF_Label", insertable = false, nullable = false, updatable = false)
    private String informationLabel;

    @Column(name = "INF_Unit", insertable = false, nullable = false, updatable = false)
    private String unit;
}
