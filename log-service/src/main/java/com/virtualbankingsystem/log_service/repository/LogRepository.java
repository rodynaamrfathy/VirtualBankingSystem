package com.virtualbankingsystem.log_service.repository;

import com.virtualbankingsystem.log_service.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
//Spring Data JPA automatically generates the SQL to save the Log entity
// into the database configured in application.properties.