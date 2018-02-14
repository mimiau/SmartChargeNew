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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

@RegisterStorage(SmartCharge.Shared.class)
public class SmartCharge implements StartPoint {

    public void groupPCJbarrier(int firstGroupMember, int numberOfThreadsInGroup) {

        if(numberOfThreadsInGroup != 1) {
            if (PCJ.myId() >= firstGroupMember + 1 && PCJ.myId() < firstGroupMember + numberOfThreadsInGroup - 1) {
                PCJ.barrier(PCJ.myId() + 1);
                PCJ.barrier(PCJ.myId() - 1);
            } else if (PCJ.myId() == (firstGroupMember + numberOfThreadsInGroup - 1)) {
                PCJ.barrier(firstGroupMember);
                PCJ.barrier(PCJ.myId() - 1);
            } else if (PCJ.myId() == firstGroupMember) {
                PCJ.barrier(firstGroupMember + numberOfThreadsInGroup - 1);
                PCJ.barrier(PCJ.myId() + 1);
            }
        } else {
            PCJ.barrier();
        }
    }

    @Storage(SmartCharge.class)
    enum Shared {
        localSolution,
        currentBestColumnSum,
        currentBestCandidateListIndex

    }

    boolean[] localSolution = new boolean[3700]; //size has to be greater than number of potential stations to accomodate real
                                                 // world stations that are obligatory solutions
    float currentBestColumnSum = 1;
    int currentBestCandidateListIndex = 0;

    public static void main(String[] args) throws IOException {

        NodesDescription nd;
        if (AuxTools.FileExists("nodes.txt")) {  // hpc run case
            nd = new NodesDescription("nodes.txt");
        } else {  //local run case
            String[] nodes = {"localhost"};//, "localhost","localhost", "localhost"};
            nd = new NodesDescription(nodes);
        }

        PCJ.deploy(SmartCharge.class, nd);
    }

    @Override
    public void main() throws Throwable {

        System.out.println("Hello from: " + PCJ.myId() + " of " + PCJ.threadCount());


        // entry data for constructive solution search
        List<Cluster> LCluster = new ArrayList<>(); //Arraylist is created by copying linkedlist to preinit. Arraylist
        List<Station> LStation = new ArrayList<>();
        int thisManyStations; // number of stations to be allocated in a given station list
        int alpha = 0; //temporarily we use all threads. Later only some portion will be assigned
        int MyThreadsNum = PCJ.threadCount(); //number of threads assigned counted from alpha

        //temp initialization.
        thisManyStations = 1000;

        if (AuxTools.FileExists("/Users/Mateusz/Desktop/warszawa_servers_restricted.txt")) {  // hpc run case
            Scanner sc = null;
            sc = new Scanner(new File("/Users/Mateusz/Desktop/warszawa_servers_restricted.txt"));

            int counter = 0;
            String line;
            while(sc.hasNext()){
                if (sc.hasNextLine()) {
                    line = sc.nextLine();

                    Station A = new Station();

                    A.setX(Float.parseFloat(line.split(";")[0]));
                    //System.out.println(line.split(";")[0]);
                    A.setY(Float.parseFloat(line.split(";")[1]));
                    //System.out.println(line.split(";")[1]);
                    switch( Integer.parseInt(line.split(";")[2]) ){
                        case 1: A.setActive(true);
                            break;
                        case 0: A.setActive(false);
                            break;
                    }
                    A.setId(Integer.parseInt(line.split(";")[3]));
                    //System.out.println(line.split(";")[3]);

                    LStation.add(A);
                }
                else{ sc.next(); }
            }
            System.out.println("Loaded LStations from FILE");


        } else {

            for (int i = 0; i < 1000; i++) { // 3000
                Station A = new Station();
                A.setX((float) i / 10000);
                A.setY((float) i / 10000);
                LStation.add(A);
            }
            System.out.println("Loaded LStations from GENERATOR");
        }

        if (AuxTools.FileExists("/Users/Mateusz/Desktop/warszawa_clients_restricted.txt")) {  // hpc run case
            Scanner sc = null;
            sc = new Scanner(new File("/Users/Mateusz/Desktop/warszawa_clients_restricted.txt"));

            int counter = 0;
            String line;
            while(sc.hasNext()){
                if (sc.hasNextLine()) {
                    line = sc.nextLine();

                    Cluster A = new Cluster();

                    A.setX(Float.parseFloat(line.split(";")[0]));
                    //System.out.println(line.split(";")[0]);
                    A.setY(Float.parseFloat(line.split(";")[1]));
                    //System.out.println(line.split(";")[1]);
                    A.setWeight(Float.parseFloat(line.split(";")[2]));
                    //System.out.println(line.split(";")[2]);

                    LCluster.add(A);
                }
                else{ sc.next(); }
            }
            System.out.println("Loaded LClusters from FILE");


        } else {
            for (int i = 0; i < 1; i++) { // up to 60000
                Cluster B = new Cluster();
                B.setX((float) 50.057); //i/10000);
                B.setY((float) 21.7183);//i/10000);
                B.setWeight(ThreadLocalRandom.current().nextInt(1, 10 + 1));
                LCluster.add(B);
            }
            System.out.println("Loaded LClusters from GENERATOR");
        }


        PCJ.barrier();
        //end temp initialization------------------------------------------------------------------------------------


        System.out.println("alpha = " + alpha + ", MyThreadsNum = " + MyThreadsNum);
        System.out.println("LStation.size() = " + LStation.size() + ", LCluster.size() = " + LCluster.size());

        AuxTools.setIntercityStationsActive(LStation);

        float[][] distancesArray = new float[LCluster.size()][LStation.size()];  //[i][j]
        distancesArray = AuxTools.makeDistanceArray(LCluster, LStation);

        int searchRangeStartIndex = AuxTools.findSearchRangeStartIndex(LStation);
        int searchRangeStopIndex = LStation.size()-1;
        System.out.println("searchRangeStartIndex = "+ searchRangeStartIndex);
        System.out.println("searchRangeStopIndex = " + searchRangeStopIndex);

        AuxTools.initializeLocalSolution(false, localSolution, searchRangeStartIndex, searchRangeStopIndex);

        System.out.println("distancesArray lengths: " + distancesArray.length + ",  " + distancesArray[0].length);





















        long startTime;
        long stopTime;
        long elapsedTime;
        System.out.println("TWOJA FUNKCJA - START");
        startTime = System.nanoTime();

        //TU WKLEJ FUNKCJĘ - START
        //initialize(boolean[] localSolution, List<station> LStation, int thisManyStations, int alreadyAddedStations, int searchRangeStartIndex,  int searchRangeStopIndex)





        //TU WKLEJ FUNKCJĘ - STOP

        stopTime = System.nanoTime();
        System.out.println("TWOJA FUNKCJA - END");
        elapsedTime = stopTime - startTime;
        System.out.println("TWOJA FUNKCJA  elapsed time: " + elapsedTime + "ns ( " +elapsedTime/1000000000 +"s )" );























//        //RANDOM INITIALIZATION (B option)
//        for (int i = searchRangeStartIndex; i < searchRangeStartIndex+thisManyStations; i++) {
//            localSolution[i] = true;
//        }


        int[][] sortedDistancesArrayIndexesArray = AuxTools.makeSortedDistanceArrayIndexesArray(distancesArray);

        float objFunctVal = AuxTools.objectiveFunction3(localSolution, distancesArray, sortedDistancesArrayIndexesArray);
        System.out.println("Objective function value = " + objFunctVal);
































        PrintWriter paper = new PrintWriter("/Users/Mateusz/Desktop/localSolution.txt", "UTF-8");
        for (int i = 0; i < searchRangeStopIndex; i++) {
            if(localSolution[i] == true){
                paper.println(LStation.get(i).getX() + ";" + LStation.get(i).getY());
            }

        }
        paper.close();



    } //end main throws...


}







