import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import net.morbz.osmonaut.*;
import net.morbz.osmonaut.osm.Entity;
import net.morbz.osmonaut.osm.EntityType;
import net.morbz.osmonaut.osm.Tags;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

public class MapTools {

    public static void FindClients(String CityName) throws FileNotFoundException, UnsupportedEncodingException {

        EntityFilter filter = new EntityFilter(true, true, false);
        String path ="resources/data/";
        path=path.concat(CityName);
        PrintWriter clients = new PrintWriter(path.concat("/clients.txt"), "UTF-8");
        PrintWriter clients_clustered = new PrintWriter(path.concat("/clients_clustered.txt"),"UTF-8");
        final ArrayList<double[]> ClientCoords = new ArrayList<double[]>();
        final List<DoublePoint> ClientsToCluster = new ArrayList<>();
        List<CentroidCluster> ClusteredClients;

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

        for (int i=0;i<ClientsToCluster.size();i++)
        {
            clients.println(ClientsToCluster.get(i).getPoint()[0]+","+ClientsToCluster.get(i).getPoint()[1]);
        }

        System.out.println("Wypisałem klientów do klastrowania");

        KMeansPlusPlusClusterer ClientsClusterer = new KMeansPlusPlusClusterer(ClientsToCluster.size()/40,-1);
        ClusteredClients = ClientsClusterer.cluster(ClientsToCluster);

        for (int i=0;i<ClusteredClients.size();i++)
        {
            clients_clustered.println(ClusteredClients.get(i).getCenter().getPoint()[0]+","+ClusteredClients.get(i).getCenter().getPoint()[1]/*+";"+ClusteredClients.get(i).getPoints().size()*/);
        }
        System.out.println("Wypisałem klastry klientów");


        clients.close();
        clients_clustered.close();


    }

    public static void FindServers(String CityName,int StationsToPut) throws FileNotFoundException, UnsupportedEncodingException {
        EntityFilter filter = new EntityFilter(true, true, false);
        String path ="resources/data/";
        path=path.concat(CityName);
        PrintWriter servers = new PrintWriter(path.concat("/servers.txt"), "UTF-8");
        PrintWriter servers_clustered = new PrintWriter(path.concat("/servers_clustered.txt"),"UTF-8");
        final ArrayList<double[]> ServerCoords = new ArrayList<double[]>();
        final List<DoublePoint> ServersToCluster = new ArrayList<>();
        List<CentroidCluster> ClusteredServers;

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
                        || tags.hasKey("shop"));
            }

            @Override
            public void foundEntity(Entity entity) {

                DoublePoint point;
                if (entity.getTags().hasKeyValue("aeroway", "aerodrome")
                        || entity.getTags().hasKeyValue("amenity", "charging_station")
                        || entity.getTags().hasKeyValue("amenity", "college")
                        || entity.getTags().hasKeyValue("amenity", "kindergarten")
                        || entity.getTags().hasKeyValue("amenity", "place_of_worship")
                        || entity.getTags().hasKeyValue("amenity", "school")
                        || entity.getTags().hasKeyValue("amenity", "language_school")
                        || entity.getTags().hasKeyValue("amenity", "university")
                        || entity.getTags().hasKeyValue("amenity", "bank")
                        || entity.getTags().hasKeyValue("amenity", "clinic")
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
                        || entity.getTags().hasKeyValue("office", "government")
                        || entity.getTags().hasKeyValue("office", "administrative")
                        || entity.getTags().hasKeyValue("highway", "rest_area")
                        || entity.getTags().hasKeyValue("highway", "services")
                        || entity.getTags().hasKeyValue("leisure", "fitness_centre")
                        || entity.getTags().hasKeyValue("leisure", "sports_centre")
                        || entity.getTags().hasKeyValue("shop", "supermarket")
                        || entity.getTags().hasKeyValue("shop", "mall")
                        || entity.getTags().hasKeyValue("shop", "department_store")) {
                    xCoord = (float) entity.getCenter().getLat();
                    yCoord = (float) entity.getCenter().getLon();
                    ServerCoords.add(new double[2]);
                    ServerCoords.get(ServerCoords.size()-1)[0]=xCoord;
                    ServerCoords.get(ServerCoords.size()-1)[1]=yCoord;
                    point =  new DoublePoint(ServerCoords.get(ServerCoords.size()-1));
                    ServersToCluster.add(point);
                }
            }
        });

        for (int i=0;i<ServersToCluster.size();i++)
        {
            servers.println(ServersToCluster.get(i).getPoint()[0]+","+ServersToCluster.get(i).getPoint()[1]);
        }

        System.out.println("Wypisałem serwery do klastrowania");

        KMeansPlusPlusClusterer ClientsClusterer = new KMeansPlusPlusClusterer(StationsToPut,-1);
        ClusteredServers = ClientsClusterer.cluster(ServersToCluster);

        for (int i=0;i<ClusteredServers.size();i++)
        {
            servers_clustered.println(ClusteredServers.get(i).getCenter().getPoint()[0]+","+ClusteredServers.get(i).getCenter().getPoint()[1]/*+";"+ClusteredClients.get(i).getPoints().size()*/);
        }
        System.out.println("Wypisałem klastry serwerów");


        servers.close();
        servers_clustered.close();

    }
}
