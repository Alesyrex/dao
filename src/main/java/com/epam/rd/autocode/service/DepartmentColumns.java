package com.epam.rd.autocode.service;

public enum DepartmentColumns {
    NAME(1, "name"),
    LOCATION(2, "location"),
    ID_DEP(3, "id");

    private final int index;
    private final String name;

    DepartmentColumns(int index, String name) {
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
