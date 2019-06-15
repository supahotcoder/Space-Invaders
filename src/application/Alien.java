package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Alien {

    private IntegerProperty row;
    private IntegerProperty column;
    private BooleanProperty alive = new SimpleBooleanProperty(true);

    private int rDir = 0;
    private int cDir = 1;

    public Alien(int row, int column) {
        super();
        this.row = new SimpleIntegerProperty(row);
        this.column = new SimpleIntegerProperty(column);
    }

    public void restart(){
        rDir = 0;
        cDir = 1;
    }

    public void update(){
        setRow(getRow() + rDir);
        if (rDir != 1) setColumn(getColumn() + cDir);
    }

    public void hBounce(){
        cDir *= -1;
    }

    public void moveDown(){
        rDir = 1;
    }

    public void stayOnRow(){
        rDir = 0;
    }

    public IntegerProperty rowProperty() {
        return row;
    }

    public IntegerProperty columnProperty() {
        return column;
    }

    public BooleanProperty aliveProperty() {
        return alive;
    }

    public int getRow() {
        return row.get();
    }

    public int getColumn() {
        return column.get();
    }

    public boolean isAlive() {
        return alive.get();
    }

    public void setRow(int row) {
        this.row.set(row);
    }

    public void setColumn(int column) {
        this.column.set(column);
    }

    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }
}
