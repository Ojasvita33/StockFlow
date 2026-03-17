package com.stockflow.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.stockflow.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

}