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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RegisterStorage(PcjStage.Shared.class)
public class PcjStage implements StartPoint {

    @Storage(PcjStage.class)
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

        PCJ.deploy(PcjStage.class, nd);
    }

    @Override
    public void main() throws Throwable {
        Group cityGroup = new Group() {
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
        Group citySubGroup = new Group() {
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

        List<String> cityName = new ArrayList<>();
        List<Integer> cityProblemSize = new ArrayList<>();
        List<Integer> cityProblemSizeOverwrite = new ArrayList<>();

        if (AuxTools.FileExists("resources/data/info.txt")) {  // hpc run case
            Scanner sc = null;
            sc = new Scanner(new File("resources/data/info.txt"));

            String line;
            while(sc.hasNext()) {
                if (sc.hasNextLine()) {
                    line = sc.nextLine();

                    cityName.add(line.split(";")[0]);
                    cityProblemSize.add(Integer.parseInt(line.split(";")[1]));
                    cityProblemSizeOverwrite.add(Integer.parseInt(line.split(";")[2]));

                } else {
                    sc.next();
                }
            }

        } else {
            System.out.println("ERROR: resources/data/info.txt does not exist");
        }

        //CALCULATION OF NODES TO BE USED
        List<Integer> nodesPerCity = new ArrayList<>();

        int coresMulti = 24;
        for (int i = 0; i < cityProblemSize.size(); i++) {
            // USING OVERWRITE ONLY
            nodesPerCity.add(cityProblemSizeOverwrite.get(i) * coresMulti);
        }







        //SET CITY GROUPS
        int num = 0;
        for (int i = 0; i < cityProblemSizeOverwrite.size(); i++) {
            for (int j = 0; j < cityProblemSizeOverwrite.get(i); j++) {
                //System.out.println(names[i]);
                if(PCJ.myId() == num) cityGroup = PCJ.join(cityName.get(i));
                num++;
            }
        }














    } //end main throws...


}







