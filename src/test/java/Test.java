import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Test {
    public static void main(String[] args) throws IOException {

        ListMapTools lister = new ListMapTools();
        /*FileMapTools finder = new FileMapTools();
        finder.FindClients("rzeszow");
        finder.FindServers("rzeszow",100);*/
        List<Station> a = new ArrayList<Station>();
        int count=0;
        a = lister.FindServers("warszawa");

        for (int i=0;i<a.size();i++)
        {
            if (a.get(i).isActive())
            {
                count++;
                System.out.println(a.get(i).getX()+","+a.get(i).getY());

            }
        }
        System.out.println(count);
    }


}