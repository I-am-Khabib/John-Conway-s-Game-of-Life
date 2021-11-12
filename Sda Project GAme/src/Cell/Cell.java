package Cell;

public class Cell {
    private int X_Position;
    private int Y_Position;
    private boolean is_dead;

    public Cell(int x_Position, int y_Position, boolean is_dead) {
        X_Position = x_Position;
        Y_Position = y_Position;
        this.is_dead = is_dead;
    }

    public int get_X_Position() {
        return X_Position;
    }

    public void set_X_Position(int x_Position) {
        X_Position = x_Position;
    }

    public int get_Y_Position() {
        return Y_Position;
    }

    public void set_Y_Position(int y_Position) {
        Y_Position = y_Position;
    }

    public boolean isIs_dead() {
        return is_dead;
    }

    public void setIs_dead(boolean is_dead) {
        this.is_dead = is_dead;
    }
}
