package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.DangKy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DangKyRepository extends JpaRepository<DangKy, Long> {

    @Query("""
            select count(d) from DangKy d
            where (:contestId is null or d.cuocThi.id = :contestId)
              and (:from is null or d.ngayDangKy >= :from)
              and (:to is null or d.ngayDangKy < :to)
              and (:saleId is null or d.thiSinh.assignedSale.id = :saleId)
            """)
    long countRegistrations(@Param("contestId") Long contestId,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to,
                            @Param("saleId") Long saleId);

    @Query("""
            select count(d) from DangKy d
            where (:contestId is null or d.cuocThi.id = :contestId)
              and (:from is null or d.ngayDangKy >= :from)
              and (:to is null or d.ngayDangKy < :to)
              and (:saleId is null or d.thiSinh.assignedSale.id = :saleId)
            """)
    long countDashboardRegistrations(@Param("contestId") Long contestId,
                                     @Param("from") LocalDateTime from,
                                     @Param("to") LocalDateTime to,
                                     @Param("saleId") Long saleId);

    /**
     * Đếm số đăng ký theo từng ngày, dùng cho biểu đồ xu hướng.
     * Trả về Object[]{LocalDate date, Long count}
     */
    @Query("""
            select cast(d.ngayDangKy as date), count(d) from DangKy d
            where (:contestId is null or d.cuocThi.id = :contestId)
              and (:from is null or d.ngayDangKy >= :from)
              and (:to is null or d.ngayDangKy < :to)
              and (:saleId is null or d.thiSinh.assignedSale.id = :saleId)
            group by cast(d.ngayDangKy as date)
            order by cast(d.ngayDangKy as date) asc
            """)
    List<Object[]> countDailyRegistrations(@Param("contestId") Long contestId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to,
                                           @Param("saleId") Long saleId);

    /**
     * Đếm số đăng ký chưa có thanh toán hoàn thành (lead cần chăm sóc lại).
     */
    @Query("""
            select count(d) from DangKy d
            where (:contestId is null or d.cuocThi.id = :contestId)
              and (:from is null or d.ngayDangKy >= :from)
              and (:to is null or d.ngayDangKy < :to)
              and (:saleId is null or d.thiSinh.assignedSale.id = :saleId)
              and not exists (
                  select 1 from ThanhToan t
                  where t.dangKy.id = d.id
                    and lower(t.paymentStatus) = 'completed'
              )
            """)
    long countLeadNeedFollowUp(@Param("contestId") Long contestId,
                               @Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to,
                               @Param("saleId") Long saleId);
}
