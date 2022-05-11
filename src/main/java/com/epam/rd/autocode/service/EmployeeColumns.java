package com.epam.rd.autocode.service;

public enum EmployeeColumns {
    ID_EMP(1, "id"),
    FIRSTNAME(2, "firstname"),
    LASTNAME(3, "lastname"),
    MIDDLENAME(4, "middlename"),
    POSITION(5,"position"),
    MANAGER(6, "manager"),
    HIREDATE(7, "hiredate"),
    SALARY(8, "salary"),
    DEPARTMENT(9, "department");

    private final int index;
    private final String name;

    EmployeeColumns(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
