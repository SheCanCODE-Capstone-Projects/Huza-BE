package com.huza.huzabackend.repository;

import com.huza.huzabackend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByTinNumber(String tinNumber);
    boolean existsByTinNumber(String tinNumber);
}