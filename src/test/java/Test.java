import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Test {
    public static void main(String[] args) throws IOException {
        boolean z[] = new boolean[3700];
        int groupedStations[] = new int[4000];
        ListMapTools lister = new ListMapTools();
        //FileMapTools finder = new FileMapTools();
        //finder.FindClients("warszawa",10);
        //finder.FindServers("warszawa",929);
       List<Cluster> a = new ArrayList<Cluster>();
        int count=0;
        a = lister.FindClients("warszawa",100);
        /*a = lister.FindServers("warszawa");
        a = lister.ServerCandidatesClustering(a,1000,13,1.5);
        for (int i=0;i<a.size();i++)
        {
            if (a.get(i).isActive())
            {
                count++;
                System.out.println(a.get(i).getX()+","+a.get(i).getY());

            }
        }
        System.out.println(count);
        lister.initializeByClustering(z,a,1000,13,30,a.size()-1);
        lister.groupStations(z,a,groupedStations,30 );*/

    }


}