package main.java.com.wdhays.gol;

public enum GameZoom {
    SMALL(3, 100,"SMALL"),
    NORMAL(5, 90,"NORMAL"),
    BIG(8, 75,"BIG");

    private int cellSize; //In milliseconds
    private int gridSize;
    private String label;

    GameZoom(int cellSize, int gridSize,String label) {
        this.cellSize=cellSize;
        this.gridSize=gridSize;
        this.label=label;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getGridSize() {
        return gridSize;
    }

    public String getLabel() {
        return label;
    }
}
