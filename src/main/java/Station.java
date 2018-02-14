/**
 * Created by Mateusz on 08.02.2018
 */

public class Station {

    private float x;
    private float y;
    private boolean active; //if station is a part of the solution
    private int id;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void switchActive() {
        this.active = !this.active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}