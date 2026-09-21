package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
    List<NhanVien> findByPhanQuyenIgnoreCase(String phanQuyen);
}
