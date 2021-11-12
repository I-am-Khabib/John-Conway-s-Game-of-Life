package com.company;
import Cell.Cell;
public class Grid {
    private int row;
    private int columns;
    private Cell[][] Grid_;

    public Grid(int row, int columns) {
        this.row = row;
        this.columns = columns;

    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }



}
