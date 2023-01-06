package com.sil.sil_synchronizer.Repositories;

import com.sil.sil_synchronizer.Entities.ViewArchivedInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IViewArchivedInformationDao extends JpaRepository<ViewArchivedInformationEntity, Long> {

    @Query(value = "SELECT ID, STA_SiteNumber, INF_NumberInStation, MAX(FORMAT(INF_Date, 'yyyy-MM-dd HH:00:00')) INF_Date, SUM(INF_Value) INF_Value " +
            "FROM View_ArchivedInformations " +
            "WHERE INF_NumberInStation in (:informationNumbers)  " +
            "AND INF_Date > :startDate " +
            "GROUP BY FORMAT(INF_Date, 'yyyy-MM-dd HH'), STA_SiteNumber, INF_NumberInStation, ID", nativeQuery = true)
    List<ViewArchivedInformationEntity> findLastByInformationNumber(@Param("informationNumbers") List<Long> informationNumbers,
                                                                    @Param("startDate") Date startDate);
}