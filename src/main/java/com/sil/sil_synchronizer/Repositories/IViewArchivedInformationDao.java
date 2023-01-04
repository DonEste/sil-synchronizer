package com.sil.sil_synchronizer.Repositories;

import com.sil.sil_synchronizer.Entities.ViewArchivedInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IViewArchivedInformationDao extends JpaRepository<ViewArchivedInformationEntity, Long> {

    @Query(value = "SELECT vai, MAX(vai.date) " +
            " FROM ViewArchivedInformationEntity vai " +
            " WHERE vai.numberInStation IN (:informationNumbers)" +
            " GROUP BY vai.numberInStation")
    List<ViewArchivedInformationEntity> findLastByInformationNumber(@Param("informationNumbers") List<Long> informationNumbers);

    @Query(value = "SELECT vai " +
            " FROM View_ArchivedInformations vai " +
            " WHERE vai.numberInStation IN (:informationNumbers) " +
            " AND vai.INF_Date > :startDate " +
            " GROUP BY DATE_ADD( DATE(vai.INF_Date), INTERVAL( HOUR(vai.INF_Date) - HOUR(vai.INF_Date) %% %d ) HOUR) as hourly", nativeQuery = true)
    List<ViewArchivedInformationEntity> findByInformationNumberPreviousPeriods(@Param("informationNumbers") List<Long> informationNumbers,
                                                                               @Param("startDate") Date startDate);
}