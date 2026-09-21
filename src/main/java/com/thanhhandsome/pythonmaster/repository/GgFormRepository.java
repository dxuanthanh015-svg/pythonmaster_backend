package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.GgForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface GgFormRepository extends JpaRepository<GgForm, Long> {
    @Query("""
            select count(g) from GgForm g
            where (:from is null or g.thoiGian >= :from)
              and (:to is null or g.thoiGian < :to)
              and (:saleId is null or g.thiSinh.assignedSale.id = :saleId)
            """)
    long countApproaches(@Param("from") LocalDateTime from,
                         @Param("to") LocalDateTime to,
                         @Param("saleId") Long saleId);
}
