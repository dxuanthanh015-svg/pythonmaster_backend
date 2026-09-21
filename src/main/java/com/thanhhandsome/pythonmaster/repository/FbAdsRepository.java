package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.FbAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FbAdsRepository extends JpaRepository<FbAds, Long> {
    @Query("""
            select count(f) from FbAds f
            where (:from is null or f.createdAt >= :from)
              and (:to is null or f.createdAt < :to)
              and (:saleId is null or f.thiSinh.assignedSale.id = :saleId)
            """)
    long countApproaches(@Param("from") LocalDateTime from,
                         @Param("to") LocalDateTime to,
                         @Param("saleId") Long saleId);
}

