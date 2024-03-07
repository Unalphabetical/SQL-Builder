package com.unalphabetical.sqlproject;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private String name;

    private List<String> columns;

    private List<String> dataTypes;

    private List<String> keys;

    private final List<Table> tableReferences;

    private final List<String> columnReferences;

    public Table(String name){
        this.name = name;
        this.columns = new ArrayList<>();
        this.dataTypes = new ArrayList<>();

        this.keys = new ArrayList<>();
        this.tableReferences = new ArrayList<>();
        this.columnReferences = new ArrayList<>();
    }

    public Table setName(String name) {
        this.name = name;
        return this;
    }

    public Table setColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    public Table setDataTypes(List<String> dataTypes) {
        this.dataTypes = dataTypes;
        return this;
    }

    public Table column(String column) {
        this.columns.add(column);
        return this;
    }

    public Table dataType(String dataType){
        this.dataTypes.add(dataType);
        return this;
    }

    public Table key(String key){
        this.keys.add(key);
        return this;
    }

    public Table tableReference(Table reference){
        this.tableReferences.add(reference);
        return this;
    }

    public Table columnReference(String reference){
        this.columnReferences.add(reference);
        return this;
    }

    public String getName() {
        return name;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<String> getDataTypes() {
        return dataTypes;
    }

    public List<String> getKeys() {
        return keys;
    }

    public List<Table> getTableReferences() {
        return tableReferences;
    }

    public List<String> getColumnReferences() {
        return columnReferences;
    }

}
