package com.virtualbankingsystem.log_service.repository;

import com.virtualbankingsystem.log_service.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
