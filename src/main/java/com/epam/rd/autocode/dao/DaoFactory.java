package com.epam.rd.autocode.dao;

import com.epam.rd.autocode.service.DepartmentService;
import com.epam.rd.autocode.service.EmployeeService;

public class DaoFactory {
    public EmployeeDao employeeDAO() {
        return new EmployeeService();
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentService();
    }
}
