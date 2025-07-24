package com.attendance.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.attendance.entity.Employee;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, String> {
    Optional<Employee> findByEmployeeIdAndDeleteFlag(String employeeId, String deleteFlag);
}
