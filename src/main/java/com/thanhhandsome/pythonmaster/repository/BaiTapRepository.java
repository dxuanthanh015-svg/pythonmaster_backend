package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.BaiTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BaiTapRepository extends JpaRepository<BaiTap, Long> {
    @Query("""
            select count(distinct b.thiSinh.id) from BaiTap b
            where (:contestId is null or b.cuocThi.id = :contestId)
              and lower(b.trangThai) in ('submitted', 'graded')
              and (:from is null or b.createdAt >= :from) and (:to is null or b.createdAt < :to)
              and (:saleId is null or b.thiSinh.assignedSale.id = :saleId)
            """)
    long countParticipants(@Param("contestId") Long contestId,
                           @Param("from") LocalDateTime from,
                           @Param("to") LocalDateTime to,
                           @Param("saleId") Long saleId);
}
