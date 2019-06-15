package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Missile {

    private IntegerProperty row;
    private IntegerProperty column;

    private BooleanProperty alive = new SimpleBooleanProperty(true);
    private boolean drawed = false;

    private int rDir = 0;

    public Missile(int row, int column) {
        this.row  = new SimpleIntegerProperty(row);
        this.column = new  SimpleIntegerProperty(column);
    }

    public boolean isAlive() {
        return alive.get();
    }

    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }

    public BooleanProperty aliveProperty() {
        return alive;
    }

    public boolean isDrawed() {
        return drawed;
    }

    public void setDrawed(boolean drawed) {
        this.drawed = drawed;
    }

    public void update(){
        setRow(getRow() + rDir);
    }

    public IntegerProperty rowProperty() {
        return row;
    }

    public IntegerProperty columnProperty() {
        return column;
    }

    public int getRow() {
        return row.get();
    }

    public int getColumn() {
        return column.get();
    }

    public void setRow(int row) {
        this.row.set(row);
    }

    public void setColumn(int column) {
        this.column.set(column);
    }

    public void setrDir(int rDir) {
        this.rDir = rDir;
    }

    public int getrDir() {
        return rDir;
    }
}
