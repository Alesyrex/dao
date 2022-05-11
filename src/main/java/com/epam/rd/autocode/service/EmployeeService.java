package com.epam.rd.autocode.service;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.dao.EmployeeDao;
import com.epam.rd.autocode.domain.Department;
import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeService implements EmployeeDao {
    public static final String GET_BY_ID_EMP = "SELECT * FROM employee WHERE ID=?";
    public static final String DELETE_BY_ID_EMP = "DELETE FROM employee WHERE ID=?";
    public static final String ADD_ENTRY_EMP = "INSERT INTO employee (id, firstname, lastname, middlename, " +
            "position, manager, hiredate, salary, department) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String GET_BY_DEP = "SELECT * FROM employee WHERE department=?";
    public static final String GET_BY_MAN = "SELECT * FROM employee WHERE manager=?";
    public static final String GET_ALL_EMP = "SELECT * FROM employee";
    public static final String EXCEPTION_LOG_FORMAT = "Exception: ";

    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());

    private final ConnectionSource connectionSource = ConnectionSource.instance();

    @Override
    public Optional<Employee> getById(BigInteger id) {
        Employee employee = null;
        try (final Connection conn = connectionSource.createConnection();
             final PreparedStatement preparedStatement = conn.prepareStatement(GET_BY_ID_EMP)) {
            preparedStatement.setInt(1, id.intValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employee = getEmployee(resultSet);
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
        return Optional.ofNullable(employee);
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employeeList = new ArrayList<>();

        try (final Connection conn = connectionSource.createConnection();
             final Statement statement = conn.createStatement();
             final ResultSet resultSet = statement.executeQuery(GET_ALL_EMP)) {
            while (resultSet.next()) {
                employeeList.add(getEmployee(resultSet));
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
        return employeeList;
    }

    @Override
    public Employee save(Employee employee) {
        try (final Connection conn = connectionSource.createConnection();
             final PreparedStatement preparedStatement = conn.prepareStatement(ADD_ENTRY_EMP)) {
            preparedStatement.setInt(EmployeeColumns.ID_EMP.getIndex(), employee.getId().intValue());
            preparedStatement.setString(EmployeeColumns.FIRSTNAME.getIndex(), employee.getFullName().getFirstName());
            preparedStatement.setString(EmployeeColumns.LASTNAME.getIndex(), employee.getFullName().getLastName());
            preparedStatement.setString(EmployeeColumns.MIDDLENAME.getIndex(), employee.getFullName().getMiddleName());
            preparedStatement.setString(EmployeeColumns.POSITION.getIndex(), employee.getPosition().toString());
            preparedStatement.setInt(EmployeeColumns.MANAGER.getIndex(), employee.getManagerId().intValue());
            Date date = Date.valueOf(employee.getHired());
            preparedStatement.setDate(EmployeeColumns.HIREDATE.getIndex(), date);
            preparedStatement.setDouble(EmployeeColumns.SALARY.getIndex(), employee.getSalary().doubleValue());
            preparedStatement.setInt(EmployeeColumns.DEPARTMENT.getIndex(), employee.getDepartmentId().intValue());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
        return employee;
    }

    @Override
    public void delete(Employee employee) {
        try (final Connection conn = connectionSource.createConnection();
             final PreparedStatement preparedStatement =
                     conn.prepareStatement(DELETE_BY_ID_EMP)) {
            preparedStatement.setInt(1, employee.getId().intValue());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
    }

    @Override
    public List<Employee> getByDepartment(Department department) {
        List<Employee> employeeList = new ArrayList<>();

        try (final Connection conn = connectionSource.createConnection();
             final PreparedStatement preparedStatement =
                     conn.prepareStatement(GET_BY_DEP)) {
            preparedStatement.setInt(1, department.getId().intValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeList.add(getEmployee(resultSet));
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
        return employeeList;
    }

    @Override
    public List<Employee> getByManager(Employee employee) {
        List<Employee> employeeList = new ArrayList<>();

        try (final Connection conn = connectionSource.createConnection();
             final PreparedStatement preparedStatement =
                     conn.prepareStatement(GET_BY_MAN)) {
            preparedStatement.setInt(1, employee.getId().intValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeList.add(getEmployee(resultSet));
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
        return employeeList;
    }

    private Employee getEmployee(ResultSet resultSet) throws SQLException {
        BigInteger employeeId = resultSet.getBigDecimal(EmployeeColumns.ID_EMP.getName()).toBigInteger();
        BigInteger managerId = BigInteger.valueOf(0);
        BigInteger departmentId = BigInteger.valueOf(0);
        BigDecimal manager = resultSet.getBigDecimal(EmployeeColumns.MANAGER.getName());
        if (manager != null) {
            managerId = manager.toBigInteger();
        }
        BigDecimal department = resultSet.getBigDecimal(EmployeeColumns.DEPARTMENT.getName());
        if (department != null) {
            departmentId = department.toBigInteger();
        }
        FullName fullName = new FullName(resultSet.getString(EmployeeColumns.FIRSTNAME.getName()),
                resultSet.getString(EmployeeColumns.LASTNAME.getName()),
                resultSet.getString(EmployeeColumns.MIDDLENAME.getName()));
        Position position = Position.valueOf(resultSet.getString(EmployeeColumns.POSITION.getName()));
        LocalDate hired = resultSet.getDate(EmployeeColumns.HIREDATE.getName()).toLocalDate();
        return new Employee(employeeId, fullName, position, hired,
                resultSet.getBigDecimal(EmployeeColumns.SALARY.getName()), managerId, departmentId);
    }
}
