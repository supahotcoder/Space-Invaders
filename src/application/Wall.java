package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Wall {

    private IntegerProperty row = new SimpleIntegerProperty(2);
    private IntegerProperty column;
    private BooleanProperty alive = new SimpleBooleanProperty(true);

    private int health;

    public Wall(int column, int health) {
        this.column = new SimpleIntegerProperty(column);
        this.health = health;
    }

    public void takeDamage() {
        this.health -= 1;
        if (!isAlive()){
            setAlive(false);
        }
    }

    private void setAlive(boolean alive) {
        this.alive.set(alive);
    }

    public boolean isAlive() {
        return health > 0;
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

    public IntegerProperty columnProperty() {
        return column;
    }
}
