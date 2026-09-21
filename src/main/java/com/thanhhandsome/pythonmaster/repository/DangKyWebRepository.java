package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.DangKyWeb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DangKyWebRepository extends JpaRepository<DangKyWeb, Long> {

    @Query("""
            select distinct d.partner from DangKyWeb d
            where d.partner is not null and d.partner <> ''
            """)
    List<String> findDistinctPartners();

    @Query("""
            select d.partner, count(d) from DangKyWeb d
            where d.partner is not null and d.partner <> ''
            group by d.partner
            """)
    List<Object[]> countThiSinhByPartner();
}
