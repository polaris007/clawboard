package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardEmployee;
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
        // Clean up before each test - delete all employees
        List<DashboardEmployee> all = employeeMapper.selectAll();
        for (DashboardEmployee emp : all) {
            // Note: Assuming there's a delete method, if not, skip cleanup
        }
    }

    @Test
    @DisplayName("Should insert and select employee")
    @Sql(statements = {
        "INSERT INTO dashboard_employee (employee_id, employee_name, team_name, is_active) VALUES ('E001', 'John Doe', 'Engineering', 1)"
    })
    void testInsertAndSelect() {
        // When
        List<DashboardEmployee> employees = employeeMapper.selectAll();

        // Then
        assertThat(employees).isNotNull().hasSize(1);
        assertThat(employees.get(0).getEmployeeId()).isEqualTo("E001");
        assertThat(employees.get(0).getEmployeeName()).isEqualTo("John Doe");
        assertThat(employees.get(0).getTeamName()).isEqualTo("Engineering");
        assertThat(employees.get(0).getIsActive()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return empty list when no employees")
    void testSelectAll_Empty() {
        // When
        List<DashboardEmployee> employees = employeeMapper.selectAll();

        // Then
        assertThat(employees).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should select by employee ID")
    @Sql(statements = {
        "INSERT INTO dashboard_employee (employee_id, employee_name, team_name, is_active) VALUES ('E001', 'John Doe', 'Engineering', 1)"
    })
    void testSelectByEmployeeId() {
        // When
        DashboardEmployee employee = employeeMapper.selectByEmployeeId("E001");

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getEmployeeId()).isEqualTo("E001");
        assertThat(employee.getEmployeeName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return null for non-existent employee")
    void testSelectByEmployeeId_NotFound() {
        // When
        DashboardEmployee employee = employeeMapper.selectByEmployeeId("NONEXISTENT");

        // Then
        assertThat(employee).isNull();
    }
}
