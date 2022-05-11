package com.epam.rd.autocode.service;

import com.epam.rd.autocode.ConnectionSource;
import com.epam.rd.autocode.dao.DepartmentDao;
import com.epam.rd.autocode.domain.Department;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartmentService implements DepartmentDao {
    public static final String GET_BY_ID_DEP = "SELECT * FROM department WHERE id=?";
    public static final String GET_ALL_DEP = "SELECT * FROM department";
    public static final String DELETE_BY_ID_DEP = "DELETE FROM department WHERE id=?";
    public static final String ADD_ENTRY_DEP = "INSERT INTO department (name, location, id) VALUES (?, ?, ?)";
    public static final String UPDATE_DEP = "UPDATE department SET name=?, location=? WHERE id=?";
    public static final String EXCEPTION_LOG_FORMAT = "Exception: ";

    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    private final ConnectionSource connectionSource = ConnectionSource.instance();

    @Override
    public Optional<Department> getById(BigInteger id) {
        Department department = null;
        try (final Connection conn = connectionSource.createConnection();
             final PreparedStatement preparedStatement = conn.prepareStatement(GET_BY_ID_DEP)) {
            preparedStatement.setInt(1, id.intValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                department = getDepartment(resultSet);
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
        return Optional.ofNullable(department);
    }

    @Override
    public List<Department> getAll() {
        List<Department> departmentList = new ArrayList<>();

        try (final Connection conn = connectionSource.createConnection();
             final Statement statement = conn.createStatement();
             final ResultSet resultSet = statement.executeQuery(GET_ALL_DEP)) {
            while (resultSet.next()) {
                departmentList.add(getDepartment(resultSet));
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }

        return departmentList;
    }

    @Override
    public Department save(Department department) {
        PreparedStatement preparedStatement;
        try (final Connection conn = connectionSource.createConnection()) {
            if (getById(department.getId()).isPresent()) {
                preparedStatement = conn.prepareStatement(UPDATE_DEP);
            } else {
                preparedStatement = conn.prepareStatement(ADD_ENTRY_DEP);
            }
            preparedStatement.setString(DepartmentColumns.NAME.getIndex(), department.getName());
            preparedStatement.setString(DepartmentColumns.LOCATION.getIndex(), department.getLocation());
            preparedStatement.setInt(DepartmentColumns.ID_DEP.getIndex(), department.getId().intValue());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
        return department;
    }

    @Override
    public void delete(Department department) {
        try (final Connection conn = connectionSource.createConnection();
             final PreparedStatement preparedStatement =
                     conn.prepareStatement(DELETE_BY_ID_DEP)) {
            preparedStatement.setInt(1, department.getId().intValue());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.SEVERE, EXCEPTION_LOG_FORMAT, sqlEx);
        }
    }

    private Department getDepartment(ResultSet resultSet) throws SQLException {
        BigInteger departmentId = resultSet.getBigDecimal(DepartmentColumns.ID_DEP.getName()).toBigInteger();
        String name = resultSet.getString(DepartmentColumns.NAME.getName());
        String location = resultSet.getString(DepartmentColumns.LOCATION.getName());
        return new Department(departmentId, name, location);
    }
}
