package com.company.clawboard.mapper;

import com.company.clawboard.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for EmployeeMapper using H2 database
 */
@MybatisTest
@ActiveProfiles("test")
@DisplayName("EmployeeMapper Integration Tests")
class EmployeeMapperTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        employeeMapper.deleteAll();
    }

    @Test
    @DisplayName("Should insert and select employee")
    @Sql(statements = {
        "INSERT INTO dashboard_employee (employee_id, employee_name, team_name, is_active) VALUES ('E001', 'John Doe', 'Engineering', 1)"
    })
    void testInsertAndSelect() {
        // When
        List<Employee> employees = employeeMapper.selectAllActive();

        // Then
        assertThat(employees).isNotNull().hasSize(1);
        assertThat(employees.get(0).getEmployeeId()).isEqualTo("E001");
        assertThat(employees.get(0).getEmployeeName()).isEqualTo("John Doe");
        assertThat(employees.get(0).getTeamName()).isEqualTo("Engineering");
        assertThat(employees.get(0).getIsActive()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return empty list when no active employees")
    void testSelectAllActive_Empty() {
        // When
        List<Employee> employees = employeeMapper.selectAllActive();

        // Then
        assertThat(employees).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should filter inactive employees")
    @Sql(statements = {
        "INSERT INTO dashboard_employee (employee_id, employee_name, team_name, is_active) VALUES ('E001', 'John Doe', 'Engineering', 1)",
        "INSERT INTO dashboard_employee (employee_id, employee_name, team_name, is_active) VALUES ('E002', 'Jane Smith', 'Marketing', 0)"
    })
    void testSelectAllActive_FiltersInactive() {
        // When
        List<Employee> employees = employeeMapper.selectAllActive();

        // Then
        assertThat(employees).isNotNull().hasSize(1);
        assertThat(employees.get(0).getEmployeeId()).isEqualTo("E001");
    }

    @Test
    @DisplayName("Should select by employee ID")
    @Sql(statements = {
        "INSERT INTO dashboard_employee (employee_id, employee_name, team_name, is_active) VALUES ('E001', 'John Doe', 'Engineering', 1)"
    })
    void testSelectByEmployeeId() {
        // When
        Employee employee = employeeMapper.selectByEmployeeId("E001");

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getEmployeeId()).isEqualTo("E001");
        assertThat(employee.getEmployeeName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return null for non-existent employee")
    void testSelectByEmployeeId_NotFound() {
        // When
        Employee employee = employeeMapper.selectByEmployeeId("NONEXISTENT");

        // Then
        assertThat(employee).isNull();
    }

    @Test
    @DisplayName("Should update employee")
    @Sql(statements = {
        "INSERT INTO dashboard_employee (employee_id, employee_name, team_name, is_active) VALUES ('E001', 'John Doe', 'Engineering', 1)"
    })
    void testUpdateEmployee() {
        // Given
        Employee employee = new Employee();
        employee.setEmployeeId("E001");
        employee.setEmployeeName("John Updated");
        employee.setTeamName("Product");
        employee.setIsActive(1);

        // When
        int rowsAffected = employeeMapper.updateEmployee(employee);

        // Then
        assertThat(rowsAffected).isEqualTo(1);
        
        Employee updated = employeeMapper.selectByEmployeeId("E001");
        assertThat(updated).isNotNull();
        assertThat(updated.getEmployeeName()).isEqualTo("John Updated");
        assertThat(updated.getTeamName()).isEqualTo("Product");
    }

    @Test
    @DisplayName("Should delete employee")
    @Sql(statements = {
        "INSERT INTO dashboard_employee (employee_id, employee_name, team_name, is_active) VALUES ('E001', 'John Doe', 'Engineering', 1)"
    })
    void testDeleteEmployee() {
        // When
        int rowsAffected = employeeMapper.deleteByEmployeeId("E001");

        // Then
        assertThat(rowsAffected).isEqualTo(1);
        
        Employee employee = employeeMapper.selectByEmployeeId("E001");
        assertThat(employee).isNull();
    }
}
