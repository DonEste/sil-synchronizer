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
@Table(name = "View_Stations")
@Data
@NoArgsConstructor
public class ViewStationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STA_SiteNumber", insertable = false, nullable = false, updatable = false)
    private Long stationNumber;

    @Column(name = "STA_Label", insertable = false, nullable = false, updatable = false)
    private String label;
}
