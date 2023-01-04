package com.sil.sil_synchronizer.Repositories;

import com.sil.sil_synchronizer.Entities.DgaRegisrtyLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDgaRegistryLogDao extends JpaRepository<DgaRegisrtyLogEntity, Long> {

    @Query(value = "SELECT drl, MAX(drl.id) " +
            " FROM DgaRegisrtyLogEntity drl " +
            " WHERE drl.siteCode IN (:siteCodes)" +
            " GROUP BY drl.siteCode")
    List<DgaRegisrtyLogEntity> findLastByInformationNumber(@Param("siteCodes") List<String> siteCodes);

}