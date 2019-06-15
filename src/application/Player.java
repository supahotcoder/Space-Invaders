package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Player {

    private IntegerProperty row;
    private IntegerProperty column = new SimpleIntegerProperty(0);

    public Player(int row) {
        this.row = new SimpleIntegerProperty(row);
    }

    public void setRow(int row) {
        this.row.set(row);
    }

    public void setColumn(int column) {
        this.column.set(column);
    }

    public int getRow() {
        return row.get();
    }

    public int getColumn() {
        return column.get();
    }

    public IntegerProperty columnProperty() {
        return column;
    }

}
