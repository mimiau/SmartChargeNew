/**
 * Created by Mateusz on 08.02.2018.
 * <p>
 * Auxiliary tools class
 */

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AuxTools {

    public static boolean FileExists(String fname) {
        File file = new File(fname);
        return file.exists();
    }

    public static float[][] makeDistanceArray(List<Cluster> LCluster, List<Station> LStation) {

        int degToKmConstantX = 111;
        int degToKmConstantY = 65; //circa for the middle of Poland

        float[][] distancesArray = new float[LCluster.size()][LStation.size()];  //[i][j]
        for (int currentStationIndex = 0; currentStationIndex < LStation.size(); currentStationIndex++) {

            float currentStationX = LStation.get(currentStationIndex).getX();
            float currentStationY = LStation.get(currentStationIndex).getY();

            for (int currentClusterIndex = 0; currentClusterIndex < LCluster.size(); currentClusterIndex++) {

                float currentClusterX = LCluster.get(currentClusterIndex).getX();
                float currentClusterY = LCluster.get(currentClusterIndex).getY();
                float currentClusterWeight = LCluster.get(currentClusterIndex).getWeight();
                distancesArray[currentClusterIndex][currentStationIndex] = currentClusterWeight * (float) Math.sqrt((currentStationX - currentClusterX)*(degToKmConstantX) * (currentStationX - currentClusterX)*(degToKmConstantX) + (currentStationY - currentClusterY)*(degToKmConstantY) * (currentStationY - currentClusterY)*(degToKmConstantY));

            }
        }

        return distancesArray;
    }

    public static float objectiveFunction(boolean[] solution, float[][] distancesArray, int dropIndex, int addIndex) {
        //to calculate objective function without dropping or adding anything to the solution pass "-1" as dropIndex
        // and addIndex arguments

        int eyes = distancesArray.length; //number of rows in distancesArray
        int jays = distancesArray[0].length; //number of columns in distancesArray
        float currentMin;
        float minSum = 0;

        for (int i = 0; i < eyes; i++) {

            currentMin = distancesArray[i][0];
            for (int j = 1; j < jays; j++) {
                if ((solution[j] == true || j == addIndex)) {
                    if (distancesArray[i][j] < currentMin && j != dropIndex) {
                        currentMin = distancesArray[i][j];
                    }
                }
            }
            minSum += currentMin;
        }

        return minSum;
    }

    public static float objectiveFunction2(boolean[] solution, float[][] distancesArray) {

        int eyes = distancesArray.length; //number of rows in distancesArray
        int jays = distancesArray[0].length; //number of columns in distancesArray
        float currentMin;
        float minSum = 0;

        for (int i = 0; i < eyes; i++) {

            currentMin = Float.MAX_VALUE;
            for (int j = 0; j < jays; j++) {

                if (solution[j]) {
                    if (distancesArray[i][j] < currentMin) {
                        currentMin = distancesArray[i][j];
                    }
                }
            }
            minSum += currentMin;
        }

        return minSum;
    }

    public static Integer[] getSortedIndexes(float[] array){

        final float[] toBeSorted = array;
        Integer[] sortedIndexes = new Integer[toBeSorted.length];
        for(int i = 0; i < toBeSorted.length; i++) sortedIndexes[i] = i;
        Arrays.sort(sortedIndexes, new Comparator<Integer>(){
            public int compare(Integer o1, Integer o2){
                return Float.compare(toBeSorted[o1], toBeSorted[o2]);
            }
        });

        return sortedIndexes;

    }

    public static int[][] makeSortedDistanceArrayIndexesArray(final float[][] distancesArray) {

        int[][] sortedDistancesArrayIndexesArray = new int[distancesArray.length][distancesArray[0].length];  //[i][j]
        Integer[] tempSorted = new Integer[distancesArray[0].length];

        for (int currentClusterIndex = 0; currentClusterIndex < distancesArray.length; currentClusterIndex++) {

            tempSorted = AuxTools.getSortedIndexes(distancesArray[currentClusterIndex]);
            for (int i = 0; i < distancesArray[0].length; i++) {
                sortedDistancesArrayIndexesArray[currentClusterIndex][i] = tempSorted[i];
            }

        }

        return sortedDistancesArrayIndexesArray;

    }

    public static float objectiveFunction3(boolean[] solution, float[][] distancesArray, int[][] sortedDistancesArrayIndexesArray) {

        int eyes = distancesArray.length; //number of rows in distancesArray
        float minSum = 0;

        for (int currentI = 0; currentI < eyes; currentI++) {
            int iterator = 0;
            while( solution[ sortedDistancesArrayIndexesArray[currentI][iterator] ] == false) iterator++;
            minSum += distancesArray[currentI][ sortedDistancesArrayIndexesArray[currentI][iterator] ];
        }

        return minSum;
    }

    public static void initializeLocalSolution(boolean initializeWithThis, boolean[] localSolution, int searchRangeStartIndex, int searchRangeStopIndex) {

        for (int i = 0; i < searchRangeStartIndex; i++) {
            localSolution[i] = true;
        }

        for (int i = searchRangeStartIndex; i <= searchRangeStopIndex; i++) {
            localSolution[i] = initializeWithThis;
        }

    }

    public static int findSearchRangeStartIndex(List<Station> LStation) {
        int searchRangeStartIndex = 0;

        while (LStation.get(searchRangeStartIndex).isActive()) {
            searchRangeStartIndex++;
        }

        return searchRangeStartIndex;
    }

    public static int setIntercityStationsActive(List<Station> LStation) {
        //RETURNS NUMBER OF ADDED STATIONS!!!!

        // The aim of this function is to locate stations on the city outskirts in order to minimize the distance
        // between closest stations of every city pair. It is done intelligently though. We set the absolute needed
        // minimum.

        String[] cityName = {"bialystok", "bielsko-biala", "bydgoszcz", "czestochowa",
                "gdansk", "gorzow_wielkopolski", "kalisz", "katowice", "kielce",
                "konin", "koszalin", "krakow", "lublin", "lodz",
                "nowy_sacz", "olsztyn", "opole", "plock", "poznan",
                "radom", "rybnik", "rzeszow", "szczecin", "tarnow",
                "torun", "walbrzych", "warszawa", "wroclaw", "zielona_gora"
        };

        float[] cityX = {(float) 53.132398, (float) 49.822118, (float) 53.1219648, (float) 50.8120466,
                (float) 54.347628, (float) 52.7309926, (float) 51.7577649, (float) 50.2598987, (float) 50.8717883,
                (float) 52.2301933, (float) 54.19092, (float) 50.0619474, (float) 51.250559, (float) 51.7687323,
                (float) 49.6249173, (float) 53.7754106, (float) 50.6668184, (float) 52.5464521, (float) 52.4082663,
                (float) 51.4010698, (float) 50.0955793, (float) 50.0374531, (float) 53.4302122, (float) 50.6668184,
                (float) 53.0210671, (float) 50.7659054, (float) 52.2319237, (float) 51.1089776, (float) 51.9383336
        };

        float[] cityY = {(float) 23.1591679, (float) 19.0448936, (float) 18.0002529, (float) 19.113213,
                (float) 18.6452029, (float) 15.2400451, (float) 18.083346, (float) 19.0215852, (float) 20.6308354,
                (float) 18.2521112, (float) 16.17707, (float) 19.9368564, (float) 22.5701022, (float) 19.4569911,
                (float) 20.691346, (float) 20.4812248, (float) 17.9236408, (float) 19.7008606, (float) 16.9335199,
                (float) 21.1534113, (float) 18.5419933, (float) 22.0047174, (float) 14.5509784, (float) 17.9236408,
                (float) 18.618612, (float) 16.2825424, (float) 21.0067265, (float) 17.0326689, (float) 15.5049951
        };

        int degToKmConstantX = 111;
        int degToKmConstantY = 65; //circa for the middle of Poland

        // My random cooridnates:
        float MyX = LStation.get(0).getX();
        float MyY = LStation.get(0).getY();

        // Search for my index in the reference tables (my name is cityName[myCityIndex] )
        int myCityIndex = -1;
        float smallestDistance = Float.MAX_VALUE;
        float distance = 0;
        for (int i = 0; i < cityX.length; i++) {

            distance = (float) Math.sqrt((MyX - cityX[i])* (degToKmConstantX) * (MyX - cityX[i])* (degToKmConstantX) + (MyY - cityY[i])* (degToKmConstantY) * (MyY - cityY[i]))* (degToKmConstantY);
            if (distance < smallestDistance) {
                smallestDistance = distance;
                myCityIndex = i;
            }

        }
        //System.out.println("My name is: " + cityName[myCityIndex]);

        // set the stations
        int bestStationIndex = -1;
        smallestDistance = Float.MAX_VALUE;
        distance = 0;
        int newStationsCounter = 0;
        for (int i = 0; i < cityX.length; i++) {//ADD: sprawdzenie czy i = myCityIndex

            bestStationIndex = -1;
            smallestDistance = Float.MAX_VALUE;
            distance = 0;

            if (i != myCityIndex) {
                for (int j = 0; j < LStation.size(); j++) {

                    float currentStationX = LStation.get(j).getX();
                    float currentStationY = LStation.get(j).getY();

                    distance = (float) Math.sqrt((currentStationX - cityX[i]) * (currentStationX - cityX[i]) + (currentStationY - cityY[i]) * (currentStationY - cityY[i]));
                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        bestStationIndex = j;
                    }

                }


                if (LStation.get(bestStationIndex).isActive() == false) {

                    float closestToTheBestStationDistance = Float.MAX_VALUE;

                    for (int j = 0; j < newStationsCounter; j++) {
                        float alreadyAddedStationX = LStation.get(j).getX();
                        float alreadyAddedStationY = LStation.get(j).getY();
                        float bestStationX = LStation.get(bestStationIndex).getX();
                        float bestStationY = LStation.get(bestStationIndex).getY();
                        float distance1 = (float) Math.sqrt((alreadyAddedStationX - bestStationX) * (degToKmConstantX) * (alreadyAddedStationX - bestStationX) * (degToKmConstantX) + (alreadyAddedStationY - bestStationY) * (degToKmConstantY) * (alreadyAddedStationY - bestStationY) * (degToKmConstantY));

                        if (distance1 < closestToTheBestStationDistance) {
                            closestToTheBestStationDistance = distance1;
                        }
                    }


                    if (closestToTheBestStationDistance > 1) { //1km - minimum distance between stations

                        Station tempStationContainer = LStation.get(bestStationIndex);
                        LStation.remove(bestStationIndex);

                        tempStationContainer.setActive(true);
                        LStation.add(0, tempStationContainer);

                        newStationsCounter++; //przesunąć do warunku środkowego

                    }


                }
            }

        }

        System.out.println("Activated " + newStationsCounter + " new inter-city stations");
        return newStationsCounter;
    }


}//end of class
