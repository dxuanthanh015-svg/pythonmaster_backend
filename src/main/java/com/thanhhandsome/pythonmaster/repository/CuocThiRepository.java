package com.thanhhandsome.pythonmaster.repository;

import com.thanhhandsome.pythonmaster.entity.CuocThi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuocThiRepository extends JpaRepository<CuocThi, Long> {
    Optional<CuocThi> findByIdCuocThi(String idCuocThi);
}
