package com.sil.sil_synchronizer.Repositories;

import com.sil.sil_synchronizer.Entities.DgaRegistryLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDgaRegistryLogDao extends JpaRepository<DgaRegistryLogEntity, Long> {

    @Query(value = "SELECT * FROM ( " +
            "    SELECT ROW_NUMBER() OVER (PARTITION BY SITE_code ORDER BY INF_Date DESC) as RowNum, * " +
            "    FROM Dga_Registry_Log where SITE_code IN (:siteCodes)) vai " +
            "WHERE RowNum = 1", nativeQuery = true)
    List<DgaRegistryLogEntity> findLastByInformationNumber(@Param("siteCodes") List<String> siteCodes);
}