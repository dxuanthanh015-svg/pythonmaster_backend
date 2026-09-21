package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.DoanhNghiep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoanhNghiepRepository extends JpaRepository<DoanhNghiep, Long>, JpaSpecificationExecutor<DoanhNghiep> {

    /** Lấy danh sách phân loại duy nhất (nganhNghe) để đổ dropdown. */
    @Query("select distinct d.nganhNghe from DoanhNghiep d where d.nganhNghe is not null order by d.nganhNghe")
    List<String> findDistinctPhanLoai();

    boolean existsByMaSoThue(String maSoThue);

    boolean existsByMaSoThueAndIdNot(String maSoThue, Long id);

    boolean existsByTenDoanhNghiepIgnoreCase(String tenDoanhNghiep);

    Optional<DoanhNghiep> findByMaSoThue(String maSoThue);
}
