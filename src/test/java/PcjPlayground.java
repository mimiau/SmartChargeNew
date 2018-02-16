/**
 * Created by Mateusz on 08.02.2018.
 * <p>
 * IMPORTANT
 * Before you run make sure you have enough memory heap to handle the application
 * <p>
 * https://plumbr.io/outofmemoryerror/java-heap-space
 * https://www.jetbrains.com/help/idea/increasing-memory-heap.html
 */

import org.pcj.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RegisterStorage(PcjPlayground.Shared.class)
public class PcjPlayground implements StartPoint {

    @Storage(PcjPlayground.class)
    enum Shared {
        localSolution

    }

    boolean[] localSolution = new boolean[3700];

    public static void main(String[] args) throws IOException {

        NodesDescription nd;
        if (AuxTools.FileExists("nodes.txt")) {  // hpc run case
            nd = new NodesDescription("nodes.txt");
        } else {  //local run case
            String[] nodes = {"localhost", "localhost","localhost", "localhost"};
            nd = new NodesDescription(nodes);
        }

        PCJ.deploy(PcjPlayground.class, nd);
    }

    @Override
    public void main() throws Throwable {
//        System.out.println("Hello from: " + PCJ.myId() + " of " + PCJ.threadCount());

//        Group g = PCJ.join("group" + (PCJ.myId() % 2));
//        System.out.println(PCJ.myId() + " " + g.myId() + " " + g.threadCount() + " " + g.getGroupName());

        int[] amount = {1, 3};
        String[] names = {"warszawa","rzeszow"};

        Group gCity = new Group() {
            @Override
            public int myId() {
                return 0;
            }

            @Override
            public int threadCount() {
                return 0;
            }

            @Override
            public String getGroupName() {
                return null;
            }

            @Override
            public PcjFuture<Void> asyncBarrier() {
                return null;
            }

            @Override
            public PcjFuture<Void> asyncBarrier(int i) {
                return null;
            }

            @Override
            public <T> PcjFuture<T> asyncGet(int i, Enum<?> anEnum, int... ints) {
                return null;
            }

            @Override
            public <T> PcjFuture<Void> asyncPut(T t, int i, Enum<?> anEnum, int... ints) {
                return null;
            }

            @Override
            public <T> PcjFuture<T> asyncAt(int i, AsyncTask<T> asyncTask) {
                return null;
            }

            @Override
            public <T> PcjFuture<Void> asyncBroadcast(T t, Enum<?> anEnum, int... ints) {
                return null;
            }
        };

        int num = 0;
        for (int i = 0; i < amount.length; i++) {
            for (int j = 0; j < amount[i]; j++) {
                //System.out.println(names[i]);
                if(PCJ.myId() == num) gCity = PCJ.join(names[i]);
                num++;
            }
        }

        for (int i = 0; i < PCJ.threadCount() ; i++) {
            if(PCJ.myId() == i){
                System.out.println(PCJ.myId() + "  " + gCity.getGroupName());
            }
        }




    } //end main throws...


}







