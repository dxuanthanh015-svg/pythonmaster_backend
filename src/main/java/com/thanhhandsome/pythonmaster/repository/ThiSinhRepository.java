package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.ThiSinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ThiSinhRepository extends JpaRepository<ThiSinh, Long>, JpaSpecificationExecutor<ThiSinh> {

    @Query("""
            select count(t) from ThiSinh t
            where t.createdAt >= :from and t.createdAt < :to
            and (:saleId is null or t.assignedSale.id = :saleId)
            """)
    long countLeads(@Param("from") LocalDateTime from,
                    @Param("to") LocalDateTime to,
                    @Param("saleId") Long saleId);

    @Query("""
            select count(t) from ThiSinh t
            where (:from is null or t.createdAt >= :from)
            and (:to is null or t.createdAt < :to)
            and (:saleId is null or t.assignedSale.id = :saleId)
            """)
    long countDashboardLeads(@Param("from") LocalDateTime from,
                             @Param("to") LocalDateTime to,
                             @Param("saleId") Long saleId);

    @Query("""
            select t.tinhThanh, count(t) from ThiSinh t
            where (:from is null or t.createdAt >= :from)
            and (:to is null or t.createdAt < :to)
            and (:saleId is null or t.assignedSale.id = :saleId)
            group by t.tinhThanh
            """)
    List<Object[]> countByTinhThanh(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to,
                                    @Param("saleId") Long saleId);

    @Query("""
            select coalesce(t.truongHoc, 'Khác'), count(t) from ThiSinh t
            where (:from is null or t.createdAt >= :from)
            and (:to is null or t.createdAt < :to)
            and (:saleId is null or t.assignedSale.id = :saleId)
            group by coalesce(t.truongHoc, 'Khác')
            order by count(t) desc
            """)
    List<Object[]> countByTruongHoc(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to,
                                    @Param("saleId") Long saleId);

    @Query("""
            select distinct t.truongHoc from ThiSinh t
            where t.truongHoc is not null and trim(t.truongHoc) <> ''
            order by t.truongHoc asc
            """)
    List<String> findDistinctTruongHoc();

    @Query("select t.ngaySinh from ThiSinh t where t.ngaySinh is not null")
    List<LocalDate> findAllNgaySinh();
}
