package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {

  @Query("""
      select count(t) from ThanhToan t
      where (:contestId is null or t.dangKy.cuocThi.id = :contestId)
        and lower(t.paymentStatus) in ('paid', 'completed')
        and (:from is null or t.thoiGianGiaoDich >= :from)
        and (:to is null or t.thoiGianGiaoDich < :to)
        and (:saleId is null or t.dangKy.thiSinh.assignedSale.id = :saleId)
      """)
  long countPaidRegistrations(@Param("contestId") Long contestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("saleId") Long saleId);

  @Query("""
      select coalesce(sum(t.soTien), 0) from ThanhToan t
      where (:contestId is null or t.dangKy.cuocThi.id = :contestId)
        and lower(t.paymentStatus) in ('paid', 'completed')
        and (:from is null or t.thoiGianGiaoDich >= :from)
        and (:to is null or t.thoiGianGiaoDich < :to)
        and (:saleId is null or t.dangKy.thiSinh.assignedSale.id = :saleId)
      """)
  BigDecimal sumPaidRevenue(@Param("contestId") Long contestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("saleId") Long saleId);

  @Query("""
      select count(t) from ThanhToan t
      where (:contestId is null or t.dangKy.cuocThi.id = :contestId)
        and lower(t.paymentStatus) = 'completed'
        and (:from is null or t.thoiGianGiaoDich >= :from)
        and (:to is null or t.thoiGianGiaoDich < :to)
        and (:saleId is null or t.dangKy.thiSinh.assignedSale.id = :saleId)
      """)
  long countDashboardPaidRegistrations(@Param("contestId") Long contestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("saleId") Long saleId);

  @Query("""
      select coalesce(sum(t.soTien), 0) from ThanhToan t
      where (:contestId is null or t.dangKy.cuocThi.id = :contestId)
        and lower(t.paymentStatus) = 'completed'
        and (:from is null or t.thoiGianGiaoDich >= :from)
        and (:to is null or t.thoiGianGiaoDich < :to)
        and (:saleId is null or t.dangKy.thiSinh.assignedSale.id = :saleId)
      """)
  BigDecimal sumDashboardPaidRevenue(@Param("contestId") Long contestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("saleId") Long saleId);

  @Query("""
      select t.bangDau, count(t) from ThanhToan t
      where (:contestId is null or t.dangKy.cuocThi.id = :contestId)
        and (:from is null or t.thoiGianGiaoDich >= :from)
        and (:to is null or t.thoiGianGiaoDich < :to)
        and (:saleId is null or t.dangKy.thiSinh.assignedSale.id = :saleId)
      group by t.bangDau
      """)
  java.util.List<Object[]> countByBangDau(@Param("contestId") Long contestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("saleId") Long saleId);

  @Query("""
      select t.bangDau, coalesce(sum(t.soTien), 0) from ThanhToan t
      where (:contestId is null or t.dangKy.cuocThi.id = :contestId)
        and lower(t.paymentStatus) = 'completed'
        and (:from is null or t.thoiGianGiaoDich >= :from)
        and (:to is null or t.thoiGianGiaoDich < :to)
        and (:saleId is null or t.dangKy.thiSinh.assignedSale.id = :saleId)
      group by t.bangDau
      """)
  java.util.List<Object[]> sumRevenueByBangDau(@Param("contestId") Long contestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("saleId") Long saleId);
}
