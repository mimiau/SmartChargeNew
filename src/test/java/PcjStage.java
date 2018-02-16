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

@RegisterStorage(PcjStage.Shared.class)
public class PcjStage implements StartPoint {

    @Storage(PcjStage.class)
    enum Shared {
        localSolution,
        localSolutionValue,
        checkedOut

    }

    boolean[] localSolution = new boolean[3700];
    float localSolutionValue = 0;
    boolean checkedOut = false;

    public static void main(String[] args) throws IOException {

        NodesDescription nd;
        if (AuxTools.FileExists("nodes.txt")) {  // hpc run case
            nd = new NodesDescription("nodes.txt");
        } else {  //local run case
            String[] nodes = {"localhost"};//, "localhost"};//,"localhost", "localhost"};
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







        //SET CITY GROUPS (and get thisManyStations for each core);
        int thisManyStations = -1;

        int num = 0; //int num = 0;
        while (num < 1) {

            for (int i = 0; i < cityProblemSizeOverwrite.size(); i++) {
                for (int j = 0; j < cityProblemSizeOverwrite.get(i); j++) {
                    //System.out.println(names[i]);
                    if (PCJ.myId() == num) {
                        cityGroup = PCJ.join(cityName.get(i));
                        thisManyStations = cityProblemSize.get(i);
                    }
                    num++;
                }
            }
        }

        System.out.println( PCJ.myId() + "  " + cityGroup.myId()  );
                PCJ.barrier();
                System.out.println(cityGroup.getGroupName());


        //LOADING MAP DATA (CLIENTS/STATIONS)
        ListMapTools dataLoader = new ListMapTools();

        //NOTE TO SELF: don't be bothered by "E: Node for way not found". It's normal. It also takes considerable amount of time
        List<Cluster> LCluster = new ArrayList<>();
        LCluster = dataLoader.FindClients(cityGroup.getGroupName(),10); //divides number of stations clustering ratio
        List<Station> LStation = new ArrayList<>();
        LStation = dataLoader.FindServers(cityGroup.getGroupName());



//SMART CHARGE START--------------------------------------------------------------





















//end temp initialization------------------------------------------------------------------------------------


        System.out.println("LStation.size() = " + LStation.size() + ", LCluster.size() = " + LCluster.size());

        int alreadyAddedStations; //number of added stations with below statement
        alreadyAddedStations =  AuxTools.setIntercityStationsActive(LStation);

        LStation = ListMapTools.ServerCandidatesClustering (LStation, thisManyStations, alreadyAddedStations, 1.5); //number of stations in the output = multiplier * thisManyStations

        float[][] distancesArray = new float[LCluster.size()][LStation.size()];  //[i][j]
        distancesArray = AuxTools.makeDistanceArray(LCluster, LStation);
        System.out.println("distancesArray lengths: " + distancesArray.length + ",  " + distancesArray[0].length);

        int[][] sortedDistancesArrayIndexesArray = new int[distancesArray.length][distancesArray[0].length];
        sortedDistancesArrayIndexesArray = AuxTools.makeSortedDistanceArrayIndexesArray(distancesArray);

        int searchRangeStartIndex = AuxTools.findSearchRangeStartIndex(LStation);
        int searchRangeStopIndex = LStation.size()-1;
        System.out.println("searchRangeStartIndex = "+ searchRangeStartIndex);
        System.out.println("searchRangeStopIndex = " + searchRangeStopIndex);
        AuxTools.initializeLocalSolution(false, localSolution, searchRangeStartIndex, searchRangeStopIndex);

        dataLoader.initializeByClustering(localSolution, LStation, thisManyStations, alreadyAddedStations, searchRangeStartIndex, searchRangeStopIndex);


        //OBJ FUNCTION CALCULATION TEMP - START
        localSolutionValue = AuxTools.objectiveFunction3(localSolution, distancesArray, sortedDistancesArrayIndexesArray);
        System.out.println("Objective function value = " + localSolutionValue);
        //OBJ FUNCTION CALCULATION TEMP - END















        boolean checkedOut;
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
        int kMax = 20;//(int) ((float) 0.04 * (float) thisManyStations);
        int kStart = 1; //minimum of shaken stations
        int kStep = 1;

        //VNS_2: Every thread stores (identical) local solution in localSolution array.

        //VNS_3:

        int counterWhileTime = 0;
        int counterSHAKE = 0;

        long startTime = System.nanoTime();
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
                while (improve) { //instead (see below) we consider the improvement value relative to the value at the beginning
                //while (improveValue/previousValue > 0.0002) { //0.0001
                    improveWhileCounter++;
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


//                        //lsAdditional: printing every 5th solution to file
//                        if(improveWhileCounter % 10 == 0){
//
//                            String fileName = "out/temp_" + improveWhileCounter + ".txt";
//
//                            PrintWriter paper2 = new PrintWriter(fileName, "UTF-8");
//                            for (int i = 0; i < searchRangeStopIndex; i++) {
//                                if(tempLocalSolution[i] == true){
//                                    paper2.println(LStation.get(i).getX() + ";" + LStation.get(i).getY());
//                                }
//                            }
//                            paper2.close();
//                        }

                        //lsAdditional: adding this solution to file
                        if(cityGroup.myId() == 0) {
                            String fileName = "out/ObjFunct_" + cityGroup.getGroupName() + ".csv";
                            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
                                out.println(fNew);
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        }

                    }
                }
                //REDUCED LOCAL SEARCH (RLS1) - part of VARIABLE NEIGHBORHOOD SEARCH (VNS) - END





















                //VNS_12: As we do not create S" but make changes to directly to S' (tempLocalSolution)
                float NEW = AuxTools.objectiveFunction3(tempLocalSolution, distancesArray, sortedDistancesArrayIndexesArray);
                if(NEW < AuxTools.objectiveFunction3(localSolution, distancesArray, sortedDistancesArrayIndexesArray)){

                    //VNS_13:
                    // CHANGE: localSolution must be blocked during updating process so other thread does not
                    //         try to acces it at that time
                    checkedOut = true;
                    System.arraycopy(tempLocalSolution, 0, localSolution, 0, tempLocalSolution.length);
                    localSolutionValue = NEW;
                    checkedOut = false;

                    //VNS_14: not from 1 but from kStart
                    k = kStart;

                    //VNS_15:
                } else {
                    //VNS_16: we move by kStep not by one
                    k += kStep;
                    System.out.println("kURWA ELESE" + k);
                }

                //VNS_PARALLEL-start
                PcjFuture checkedOutFuture = null;
                for (int i = 0; i < cityGroup.threadCount(); i++) {
                    do{
                    checkedOutFuture = cityGroup.asyncGet(i, Shared.checkedOut);
                    }while((boolean) checkedOutFuture.get());


                    PcjFuture localSolutionValueFuture = null;
                    localSolutionValueFuture = cityGroup.asyncGet(i, Shared.localSolutionValue);
                    float templocalSolutionValue = (float)localSolutionValueFuture.get();


                    if(templocalSolutionValue < localSolutionValue) {

                        PcjFuture localSolutionFuture = null;
                        localSolutionFuture = cityGroup.asyncGet(i, Shared.localSolution);
                        boolean[] array = (boolean[]) localSolutionFuture.get();

                        checkedOut = true;
                        System.arraycopy(array, 0, localSolution, 0, array.length);
                        localSolutionValue = templocalSolutionValue;
                        checkedOut = false;

                    }
                }



                //VNS_PARALLEL-stop




                //VNS_17: end of the loop
            }
        }// while( t < tMax) - END

        //VARIABLE NEIGHBORHOOD SEARCH (VNS) - END



















        if(cityGroup.myId() == 0) {
            String fileName = "out/localSolution_" + cityGroup.getGroupName() + ".txt";
            PrintWriter paper = new PrintWriter(fileName, "UTF-8");
            for (int i = 0; i < searchRangeStopIndex; i++) {
                if (localSolution[i] == true) {
                    paper.println(LStation.get(i).getX() + ";" + LStation.get(i).getY());
                }

            }
            paper.close();
        }





























//SMART CHARGE START--------------------------------------------------------------

    } //end main throws...


}







