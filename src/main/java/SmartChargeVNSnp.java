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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

@RegisterStorage(SmartChargeVNSnp.Shared.class)
public class SmartChargeVNSnp implements StartPoint {

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

    @Storage(SmartChargeVNSnp.class)
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

        PCJ.deploy(SmartChargeVNSnp.class, nd);
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
        thisManyStations = 600;

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

        if (AuxTools.FileExists("/Users/Mateusz/Desktop/warszawa_clients_clustered.txt")) {  // hpc run case
            Scanner sc = null;
            sc = new Scanner(new File("/Users/Mateusz/Desktop/warszawa_clients_clustered.txt"));

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

        int alreadyAddedStations; //number of added stations with below statement
        alreadyAddedStations =  AuxTools.setIntercityStationsActive(LStation);

        //Initial Stations clustering function here

        float[][] distancesArray = new float[LCluster.size()][LStation.size()];  //[i][j]
        distancesArray = AuxTools.makeDistanceArray(LCluster, LStation);
        int[][] sortedDistancesArrayIndexesArray = new int[distancesArray.length][distancesArray[0].length];
        sortedDistancesArrayIndexesArray = AuxTools.makeSortedDistanceArrayIndexesArray(distancesArray);

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

        //MICHAŁ TU WKLEJ FUNKCJĘ - START
        //initialize(boolean[] localSolution, List<station> LStation, int thisManyStations, int alreadyAddedStations, int searchRangeStartIndex,  int searchRangeStopIndex)

        ListMapTools.initializeByClustering(localSolution, LStation, thisManyStations, alreadyAddedStations, searchRangeStartIndex, searchRangeStopIndex);

        //MICHAŁ TU WKLEJ FUNKCJĘ - STOP

        stopTime = System.nanoTime();
        System.out.println("TWOJA FUNKCJA - END");
        elapsedTime = stopTime - startTime;
        System.out.println("TWOJA FUNKCJA  elapsed time: " + elapsedTime + "ns ( " +elapsedTime/1000000000 +"s )" );


//        for (int i = searchRangeStartIndex+200; i < searchRangeStartIndex+thisManyStations-200; i++) {
//            localSolution[i] = true;
//        }














        //OBJ FUNCT TEMP
        float objFunctVal = AuxTools.objectiveFunction3(localSolution, distancesArray, sortedDistancesArrayIndexesArray);
        System.out.println("Objective function value = " + objFunctVal);
        //OBJ FUNCT TEMP
















        //VARIABLE NEIGHBORHOOD SEARCH (VNS) - START
        //     we create tempLocalSolution to make changes on it (search for the more optimal solution). From now on
        //     variable localSolution will house the best solution found so far. It will be exchanged between threads
        //     when one of them finds something better.
        //     S  is localSolution
        //     S' is tempLocalSolution {S'= S in RLS1 notation}
        //     S" is tempTempLocalSolution {S" = S' in RLS1 notation}
        boolean tempLocalSolution[] = new boolean[localSolution.length];
        System.arraycopy(localSolution, 0, tempLocalSolution, 0, localSolution.length);

        //VNS_0: needed at VNS5 so we don't shuake our initial solution
        boolean firstTime = true;

        //VNS_1: Temporarily set as constant. CHANGE: parametrize all below differently
        int kMax = (int) ((float) 0.04 * (float) thisManyStations);
        int kStart = 1; //minimum of shaken stations
        int kStep = 1;

        //VNS_2: Every thread stores (identical) local solution in localSolution array.

        //VNS_3:

        int counterWhileTime = 0;
        int counterSHAKE = 0;
        int counterLS = 0;

        startTime = System.nanoTime();
        while( (System.nanoTime()-startTime)/1000000000 < 10*60 ){//while(t <tMax); "time in minutes"*60
            counterWhileTime++;
            System.out.println("             WhileTIME " + counterWhileTime);
            //VNS_4:
            int k = kStart;

            //VNS_5
            while(k < kMax) {

                //if(firstTime = false) {// we don't want to shuffle our initial solution (1 loop run)
                    counterSHAKE++;
                    System.out.println("      SHAKE " + counterSHAKE);
                    //VNS_6-10: we don't use hash. Just perform shake;
                    //shake - start
                    System.arraycopy(localSolution, 0, tempLocalSolution, 0, localSolution.length);
                    for (int exchange = 0; exchange < k; exchange++) {
                        int random = ThreadLocalRandom.current().nextInt(searchRangeStartIndex, searchRangeStopIndex + 1);
                        tempLocalSolution[random] = !tempLocalSolution[random];
                        boolean mem = tempLocalSolution[random];

                        do {
                            random = ThreadLocalRandom.current().nextInt(searchRangeStartIndex, searchRangeStopIndex + 1);
                        } while (tempLocalSolution[random] != mem);
                        tempLocalSolution[random] = !tempLocalSolution[random];
                    }
                    //shake - end
                //}
                firstTime = false;


                //VNS_11:





























                //REDUCED LOCAL SEARCH (RLS1) - part of VARIABLE NEIGHBORHOOD SEARCH (VNS) - START
                // tempLocalSolution[]       -   RLS1 notation: S ; VNS notation S'
                // tempTempLocalSolution[]   -   RLS1 notation: S' ; it is NOT  S" in VNS notation -> we make changes directly to S'
                boolean tempTempLocalSolution[] = new boolean[localSolution.length]; //
                System.arraycopy(tempLocalSolution, 0, tempTempLocalSolution, 0, tempLocalSolution.length);

                System.out.println("LS-START ");

                //ls1:
                boolean improve = true; // used alternatively with 2 statements below (both); used with while(true) at ls2
                float improveValue = Float.MAX_VALUE; //used with while("relative improvement") at ls2
                float previousValue = 1;

                System.out.println("While(improve)-START");
                //ls2:
                int improveWhileCounter = 0;
                //while (improve) { //instead (see below) we consider the improvement value relative to the value at the beginning
                    while (improveValue/previousValue > 0.0002) { //0.0001
                    improveWhileCounter ++;
                    System.out.println("While(improve) " + improveWhileCounter + " value: " + improveValue/previousValue);

                    //ls3:
                    improve = false; //substituted with value below
                    improveValue = 0;

                    //ls4: instead of measuring changes of the functions (deltaFDrop) we calculate the functions themselves (Fdrop)
                    //     and compare them directly.
                    float fDrop = Float.MAX_VALUE;
                    int jDrop = -1; //there is "IF" below so there is a warning about potencial lack of initialization. The
                    // value assigned here does not mean anything. Meaningful value is assigned at ls8.

                    float f;

                    //ls5: -we exclude real world stations (they are immovable) hence searchRangeStartIndex (they are placed at
                    //      the beginning of solution array).
                    //     -solution array is longer than the length of an actual solution. There is no need to iterate through
                    //      it all hence searchRangeStopIndex is applied.
                    System.out.println("SEARCH FOR jDROP -START");
                    for (int j = searchRangeStartIndex; j <= searchRangeStopIndex; j++) {
                        if (tempLocalSolution[j] == true) {// has to be equal to true because we search through active stations only
                            //ls6: f represents value of objective function increased as a result of substracting one station (of
                            //     index: j) from the solution.
                            tempLocalSolution[j] = false;
                            f = AuxTools.objectiveFunction3(tempLocalSolution, distancesArray, sortedDistancesArrayIndexesArray);
                            tempLocalSolution[j] = true;

                            //ls7: we want to drop the station whoose dissapearance increased objective function the less.
                            if (f < fDrop) {
                                //ls8:
                                jDrop = j;
                                //ls9:
                                fDrop = f;
                            }
                        }
                    } // here we end search for the function to drop. It is indicated by jDrop index of tempLocalSoluton.

                    //ls10:
                    tempTempLocalSolution[jDrop] = false;

                    //ls11: we assign fAdd max value as we search for the smallest f value not biggest (see ls14)
                    float fAdd = Float.MAX_VALUE;
                    int jAdd = -1; // there is "IF" below so there is a warning about potencial lack of initialization. The
                    // value assigned here does not mean anything. Meaningful value is assigned at ls15.
                    System.out.println("SEARCH FOR jADD -START");
                    //ls12:
                    for (int j = searchRangeStartIndex; j <= searchRangeStopIndex; j++) {
                        if (tempLocalSolution[j] == false) {// has to be equal to true because we search through active stations
                            // only.

                            //ls13:
                            tempTempLocalSolution[j] = true;
                            f = AuxTools.objectiveFunction3(tempTempLocalSolution, distancesArray, sortedDistancesArrayIndexesArray);
                            tempTempLocalSolution[j] = false;

                            //ls14: we want to minimize objective function so we search for the smallest f and assign it to fAdd
                            if (f < fAdd) {
                                //ls15:
                                jAdd = j;
                                //ls16:
                                fAdd = f;
                            }
                        }
                    }

                    //ls17:
                    tempTempLocalSolution[jAdd] = true;

                    //ls18: we want to minimize objective function so we substitute S' (RLS1 notation) for S (RLS1 notation) when
                    //      S' (RLS1 notation) is smaller.
                    float fNew = AuxTools.objectiveFunction3(tempTempLocalSolution, distancesArray, sortedDistancesArrayIndexesArray);
                    float fOld = AuxTools.objectiveFunction3(tempLocalSolution, distancesArray, sortedDistancesArrayIndexesArray);
                    if (fNew < fOld) {
                        //above: if( f(S') < f(S) ) where S' = S - {jDrop} + {jAdd}

                        //ls19:
                        tempLocalSolution[jDrop] = false;
                        tempLocalSolution[jAdd] = true;

                        //ls20:
                        improve = true; //this is substituted with value of improvement
                        improveValue = fOld - fNew;
                        previousValue = fOld;


                        //lsAdditional: printing every 5th solution to file
                        if(improveWhileCounter % 10 == 0){

                            String fileName = "/Users/Mateusz/Desktop/OUT/temp_" + improveWhileCounter + ".txt";

                            PrintWriter paper2 = new PrintWriter(fileName, "UTF-8");
                            for (int i = 0; i < searchRangeStopIndex; i++) {
                                if(tempLocalSolution[i] == true){
                                    paper2.println(LStation.get(i).getX() + ";" + LStation.get(i).getY());
                                }
                            }
                            paper2.close();
                        }

                        //lsAdditional: adding this solution to file
                        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/Mateusz/Desktop/OUT/ObjFunct.csv", true)))) {
                            out.println(fNew);
                        }catch (IOException e) {
                            System.err.println(e);
                        }

                    }
                }
                //REDUCED LOCAL SEARCH (RLS1) - part of VARIABLE NEIGHBORHOOD SEARCH (VNS) - END





















                //VNS_12: As we do not create S" but make changes to directly to S' (tempLocalSolution)
                if(AuxTools.objectiveFunction3(tempLocalSolution, distancesArray, sortedDistancesArrayIndexesArray) < AuxTools.objectiveFunction3(localSolution, distancesArray, sortedDistancesArrayIndexesArray)){
                    //VNS_13:
                    // CHANGE: localSolution must be blocked during updating process so other thread does not
                    //         try to acces it at that time
                    System.arraycopy(tempLocalSolution, 0, localSolution, 0, tempLocalSolution.length);

                    //VNS_14: not from 1 but from kStart
                    k = kStart;

                    //VNS_15:
                } else {
                    //VNS_16: we move by kStep not by one
                    k += kStep;
                }

                //VNS_17: end of the loop
            }
        }// while( t < tMax) - END

        //VARIABLE NEIGHBORHOOD SEARCH (VNS) - END




























































        PrintWriter paper = new PrintWriter("/Users/Mateusz/Desktop/OUT/localSolution.txt", "UTF-8");
        for (int i = 0; i < searchRangeStopIndex; i++) {
            if(localSolution[i] == true){
                paper.println(LStation.get(i).getX() + ";" + LStation.get(i).getY());
            }

        }
        paper.close();



    } //end main throws...


}







