/**
 * Created by Mateusz on 08.02.2018
 */

public class Cluster {
    private float x;
    private float y;
    private float weight;

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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}





















/*

Runtime rt = Runtime.getRuntime();
        long m0 = rt.totalMemory() -  rt.freeMemory();
        Object obj = new Object();      // create your object here
        long m1 = rt.totalMemory() -  rt.freeMemory();
        System.out.println(m1 + "  " + m0/1000000 );
        System.out.println(rt.maxMemory() );

 */