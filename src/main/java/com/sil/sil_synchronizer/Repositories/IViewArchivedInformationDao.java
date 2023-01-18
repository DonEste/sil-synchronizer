package com.sil.sil_synchronizer.Repositories;

import com.sil.sil_synchronizer.Entities.ViewArchivedInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IViewArchivedInformationDao extends JpaRepository<ViewArchivedInformationEntity, Long> {

    //TODO: Test query with real data
    @Query(value = "SELECT ID,  " +
            "       STA_SiteNumber,  " +
            "       INF_NumberInStation,  " +
            "       MAX(FORMAT(INF_Date, 'yyyy-MM-dd HH:00:00')) INF_Date,  " +
            "       CASE INF_NumberInStation when :flowNumber THEN AVG(INF_Value) ELSE 0 END as INF_Flow,  " +
            "       CASE INF_NumberInStation when :totalizerNumber THEN SUM(INF_Value) ELSE 0 END as INF_Totalizer,  " +
            "       CASE INF_NumberInStation when :pheaticLevel THEN AVG(INF_Value) ELSE 0 END as INF_PhreaticLevel  " +
            "FROM View_ArchivedInformations  " +
            "WHERE INF_NumberInStation in (:informationNumbers)  " +
            "  AND INF_Date > :startDate  " +
            "  AND INF_Date < DATEADD(hour, -:offsetHours, SYSDATETIMEOFFSET())  " +
            "GROUP BY FORMAT(INF_Date, 'yyyy-MM-dd HH'), STA_SiteNumber, INF_NumberInStation, ID  " +
            "HAVING count(*) > :minDataSamples", nativeQuery = true)
    List<ViewArchivedInformationEntity> findHourlyAverage(@Param("informationNumbers") List<Long> informationNumbers,
                                                          @Param("startDate") Date startDate,
                                                          @Param("flowNumber") Long flowNumber,
                                                          @Param("totalizerNumber") Long totalizerNumber,
                                                          @Param("pheaticLevel") Long pheaticLevel,
                                                          @Param("offsetHours") int offsetHours);
}