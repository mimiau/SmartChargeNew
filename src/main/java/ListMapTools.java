import net.morbz.osmonaut.EntityFilter;
import net.morbz.osmonaut.IOsmonautReceiver;
import net.morbz.osmonaut.Osmonaut;
import net.morbz.osmonaut.osm.Entity;
import net.morbz.osmonaut.osm.EntityType;
import net.morbz.osmonaut.osm.Tags;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import java.util.ArrayList;
import java.util.List;

public class ListMapTools {

    public static List<Cluster> FindClients(String CityName, int ClusteringRatio)  {

        EntityFilter filter = new EntityFilter(true, true, false);
        String path ="resources/data/";
        path=path.concat(CityName);
        final ArrayList<double[]> ClientCoords = new ArrayList<double[]>();
        final List<DoublePoint> ClientsToCluster = new ArrayList<>();
        List<CentroidCluster> ClusteredClients;
        List<Cluster> Clusters = new ArrayList<Cluster>();


        Osmonaut naut = new Osmonaut(path.concat("/map.pbf"), filter);

        naut.scan(new IOsmonautReceiver() {
            int count = 0;
            float xCoord = 0;
            float yCoord = 0;


            @Override
            public boolean needsEntity(EntityType type, Tags tags) {
                return (tags.hasKey("addr:housenumber"));
            }

            @Override
            public void foundEntity(Entity entity) {

                DoublePoint point;
                if (entity.getTags().hasKey("addr:housenumber")) {
                    xCoord = (float) entity.getCenter().getLat();
                    yCoord = (float) entity.getCenter().getLon();
                    ClientCoords.add(new double[2]);
                    ClientCoords.get(ClientCoords.size()-1)[0]=xCoord;
                    ClientCoords.get(ClientCoords.size()-1)[1]=yCoord;
                    point =  new DoublePoint(ClientCoords.get(ClientCoords.size()-1));
                    ClientsToCluster.add(point);
                }
            }
        });

        KMeansPlusPlusClusterer ClientsClusterer = new KMeansPlusPlusClusterer((int) (ClientsToCluster.size()/ClusteringRatio),-1);
        ClusteredClients = ClientsClusterer.cluster(ClientsToCluster);
        for (int i=0;i<ClusteredClients.size();i++)
        {
            Cluster temp = new Cluster();
            temp.setX((float)ClusteredClients.get(i).getCenter().getPoint()[0]);
            temp.setY((float)ClusteredClients.get(i).getCenter().getPoint()[1]);
            temp.setWeight(ClusteredClients.get(i).getPoints().size());
            Clusters.add(temp);

        }
    return Clusters;
    }

    public static List<Station> FindServers(String CityName)  {
        EntityFilter filter = new EntityFilter(true, true, false);
        String path ="resources/data/";
        path=path.concat(CityName);
        final ArrayList<double[]> ServerCoords = new ArrayList<double[]>();
        final List<DoublePoint> ExistingStations = new ArrayList<>();
        final List<DoublePoint> ServersToCluster = new ArrayList<>();
        List<CentroidCluster> ClusteredServers;
        List<Station> Stations = new ArrayList<Station>();

        Osmonaut naut = new Osmonaut(path.concat("/map.pbf"), filter);

        naut.scan(new IOsmonautReceiver() {
            float xCoord = 0;
            float yCoord = 0;


            @Override
            public boolean needsEntity(EntityType type, Tags tags) {
                return (tags.hasKey("amenity")
                        || tags.hasKey("office")
                        || tags.hasKey("leisure")
                        || tags.hasKey("aeroway")
                        || tags.hasKey("building")
                        || tags.hasKey("highway")
                        || tags.hasKey("shop")
                        || tags.hasKey("tourism"));
            }

            @Override
            public void foundEntity(Entity entity) {

                DoublePoint point;
                if (entity.getTags().hasKeyValue("aeroway", "aerodrome")
                        || entity.getTags().hasKeyValue("amenity", "charging_station")
                        || entity.getTags().hasKeyValue("amenity", "fuel")
                        || entity.getTags().hasKeyValue("amenity", "public_building")
                        || entity.getTags().hasKeyValue("amenity", "college")
                        || entity.getTags().hasKeyValue("amenity", "kindergarten")
                        || entity.getTags().hasKeyValue("amenity", "place_of_worship")
                        || entity.getTags().hasKeyValue("amenity", "school")
                        || entity.getTags().hasKeyValue("amenity", "language_school")
                        || entity.getTags().hasKeyValue("amenity", "university")
                        || entity.getTags().hasKeyValue("amenity", "bank")
                        || entity.getTags().hasKeyValue("amenity", "clinic")
                        || entity.getTags().hasKeyValue("amenity", "dentist")
                        || entity.getTags().hasKeyValue("amenity", "hospital")
                        || entity.getTags().hasKeyValue("amenity", "pharmacy")
                        || entity.getTags().hasKeyValue("amenity", "veterinary")
                        || entity.getTags().hasKeyValue("amenity", "arts_centre")
                        || entity.getTags().hasKeyValue("amenity", "cinema")
                        || entity.getTags().hasKeyValue("amenity", "casino")
                        || entity.getTags().hasKeyValue("amenity", "community_centre")
                        || entity.getTags().hasKeyValue("amenity", "planetarium")
                        || entity.getTags().hasKeyValue("amenity", "theatre")
                        || entity.getTags().hasKeyValue("amenity", "courthouse")
                        || entity.getTags().hasKeyValue("amenity", "post_office")
                        || entity.getTags().hasKeyValue("building", "hotel")
                        || entity.getTags().hasKeyValue("building", "trasformer_tower")
                        || entity.getTags().hasKeyValue("building", "civic")
                        || entity.getTags().hasKeyValue("building", "retail")
                        || entity.getTags().hasKeyValue("building", "supermarket")
                        || entity.getTags().hasKeyValue("building", "commercial")
                        || entity.getTags().hasKeyValue("office", "government")
                        || entity.getTags().hasKeyValue("office", "administrative")
                        || entity.getTags().hasKeyValue("highway", "rest_area")
                        || entity.getTags().hasKeyValue("highway", "services")
                        || entity.getTags().hasKeyValue("leisure", "fitness_centre")
                        || entity.getTags().hasKeyValue("leisure", "sports_centre")
                        || entity.getTags().hasKeyValue("shop", "supermarket")
                        || entity.getTags().hasKeyValue("shop", "mall")
                        || entity.getTags().hasKeyValue("shop", "department_store")
                        || entity.getTags().hasKeyValue("shop", "beauty")
                        || entity.getTags().hasKeyValue("tourism", "gallery")
                        || entity.getTags().hasKeyValue("tourism", "hotel")
                        || entity.getTags().hasKeyValue("tourism", "museum")
                        || entity.getTags().hasKeyValue("tourism", "zoo")
                        ) {
                    xCoord = (float) entity.getCenter().getLat();
                    yCoord = (float) entity.getCenter().getLon();
                    ServerCoords.add(new double[2]);
                    ServerCoords.get(ServerCoords.size()-1)[0]=xCoord;
                    ServerCoords.get(ServerCoords.size()-1)[1]=yCoord;

                    point =  new DoublePoint(ServerCoords.get(ServerCoords.size()-1));
                    if (entity.getTags().hasKeyValue("amenity","charging_station"))
                    {
                        ExistingStations.add(point);
                    }
                    else {
                        ServersToCluster.add(point);
                    }

                }
            }
        });



        for (int i=0;i<ExistingStations.size();i++)
        {
            Station temp = new Station();
            temp.setX((float)ExistingStations.get(i).getPoint()[0]);
            temp.setY((float)ExistingStations.get(i).getPoint()[1]);
            temp.setActive(true);
          Stations.add(temp);
        }

        for (int i=0;i<ServersToCluster.size();i++)
        {
            Station temp = new Station();
            temp.setX((float)ServersToCluster.get(i).getPoint()[0]);
            temp.setY((float)ServersToCluster.get(i).getPoint()[1]);
            temp.setActive(false);
            Stations.add(temp);

        }


    return Stations;
    }

    public static List<Station> ServerCandidatesClustering(List<Station> LStation, int thisManyStations, int alreadyAddedStations,double multipler)
    {
        final ArrayList<double[]> ServerCoords = new ArrayList<double[]>();
        final List<DoublePoint> ServersToCluster = new ArrayList<>();
        List<CentroidCluster> ClusteredServers;
        DoublePoint point;
        List<Station> ChoosenStations = new ArrayList<>();
        int FixedStationsAmount=0;
        for (int i=0;i<LStation.size();i++) {
            if (LStation.get(i).isActive() == false) {
                ServerCoords.add(new double[2]);
                ServerCoords.get(ServerCoords.size() - 1)[0] = LStation.get(i).getX();
                ServerCoords.get(ServerCoords.size() - 1)[1] = LStation.get(i).getY();
                point = new DoublePoint(ServerCoords.get(ServerCoords.size() - 1));
                ServersToCluster.add(point);
            }
            else
            {
                FixedStationsAmount++;
            }
        }

            KMeansPlusPlusClusterer ServerClusterer = new KMeansPlusPlusClusterer((int)((thisManyStations-alreadyAddedStations)*multipler),-1);
            ClusteredServers = ServerClusterer.cluster(ServersToCluster);
            DistanceMeasure distancer = new DistanceMeasure() {
                @Override
                public double compute(double[] doubles, double[] doubles1) throws DimensionMismatchException {
                    int degToKmConstantX = 111;
                    int degToKmConstantY = 65;

                    double distance1 = Math.sqrt((doubles[0] - doubles1[0]) * (degToKmConstantX) * (doubles[0] - doubles1[0]) * (degToKmConstantX)
                            + (doubles[1] - doubles1[1]) * (degToKmConstantY) * (doubles[1] - doubles1[1]) * (degToKmConstantY));


                    return distance1;
                }
            };
            for (int i=0;i<ClusteredServers.size();i++){
                List<DoublePoint> temp = new ArrayList<>();
                temp = ClusteredServers.get(i).getPoints();
                double min =distancer.compute(ClusteredServers.get(i).getCenter().getPoint(),temp.get(0).getPoint());
                int temp_min_index =0;
                for (int j=1;j<ClusteredServers.get(i).getPoints().size();j++){
                    double temp_min =distancer.compute(ClusteredServers.get(i).getCenter().getPoint(),temp.get(j).getPoint());
                    if (temp_min<min)
                    {
                        min = temp_min;
                        temp_min_index=j;
                    }


                }
                ClusteredServers.get(i).getCenter().getPoint()[0]=temp.get(temp_min_index).getPoint()[0];
                ClusteredServers.get(i).getCenter().getPoint()[1]=temp.get(temp_min_index).getPoint()[1];
            }

            //ChoosenStations.clear();
            for (int i=0;i<ClusteredServers.size();i++)
            {
                Station temp = new Station();
                temp.setX((float)ClusteredServers.get(i).getCenter().getPoint()[0]);
                temp.setY((float)ClusteredServers.get(i).getCenter().getPoint()[1]);
                temp.setActive(false);
                ChoosenStations.add(temp);
            }
        LStation = LStation.subList(0,FixedStationsAmount);
        LStation.addAll(ChoosenStations);

    return LStation;
    }


    public static void initializeByClustering(boolean[] localSolution, List<Station> LStation, int thisManyStations, int alreadyAddedStations,
                                              int searchRangeStartIndex, int searchRangeStopIndex)
    {
        /*boolean[] localSolution_initialize(List<Station> LStation,
        int thisManyStations, int alreadyAddedStations, int searchRangeStartIndex,
        int searchRangeStopIndex)*/

        final ArrayList<double[]> ServerCoords = new ArrayList<double[]>();
        final List<DoublePoint> ServersToCluster = new ArrayList<>();
        List<CentroidCluster> ClusteredServers;
        DoublePoint point;

        for (int i=searchRangeStartIndex;i<LStation.size();i++){
            ServerCoords.add(new double[2]);
            ServerCoords.get(ServerCoords.size()-1)[0]=LStation.get(i).getX();
            ServerCoords.get(ServerCoords.size()-1)[1]=LStation.get(i).getY();
            point =  new DoublePoint(ServerCoords.get(ServerCoords.size()-1));
            ServersToCluster.add(point);
        }

        KMeansPlusPlusClusterer ServerClusterer = new KMeansPlusPlusClusterer(thisManyStations-alreadyAddedStations,-1);
        ClusteredServers = ServerClusterer.cluster(ServersToCluster);
        DistanceMeasure distancer = new DistanceMeasure() {
            @Override
            public double compute(double[] doubles, double[] doubles1) throws DimensionMismatchException {
                int degToKmConstantX = 111;
                int degToKmConstantY = 65;

                double distance1 = Math.sqrt((doubles[0] - doubles1[0]) * (degToKmConstantX) * (doubles[0] - doubles1[0]) * (degToKmConstantX)
                        + (doubles[1] - doubles1[1]) * (degToKmConstantY) * (doubles[1] - doubles1[1]) * (degToKmConstantY));


                return distance1;
            }
        };
        for (int i=0;i<ClusteredServers.size();i++){
            List<DoublePoint> temp = new ArrayList<>();
            temp = ClusteredServers.get(i).getPoints();
            double min =distancer.compute(ClusteredServers.get(i).getCenter().getPoint(),temp.get(0).getPoint());
            int temp_min_index =0;
            for (int j=1;j<ClusteredServers.get(i).getPoints().size();j++){
                double temp_min =distancer.compute(ClusteredServers.get(i).getCenter().getPoint(),temp.get(j).getPoint());
                if (temp_min<min)
                {
                    min = temp_min;
                    temp_min_index=j;
                }


            }
            ClusteredServers.get(i).getCenter().getPoint()[0]=temp.get(temp_min_index).getPoint()[0];
            ClusteredServers.get(i).getCenter().getPoint()[1]=temp.get(temp_min_index).getPoint()[1];
        }

      /*  for (int k=0;k<ClusteredServers.size();k++)
        {
            System.out.println(ClusteredServers.get(k).getCenter().getPoint()[0]+","+ClusteredServers.get(k).getCenter().getPoint()[1]);
        }*/

        for (int i=0;i<ClusteredServers.size();i++)
        {
            for (int j=0;j<ServerCoords.size();j++)
            {
                if (ClusteredServers.get(i).getCenter().getPoint()[0]
                        ==ServerCoords.get(j)[0] &&
                        ClusteredServers.get(i).getCenter().getPoint()[1]
                                ==ServerCoords.get(j)[1])
                {
                    localSolution[searchRangeStartIndex+j]=true;
                }
            }
        }
    }
}
