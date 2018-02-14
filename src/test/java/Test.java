import java.io.IOException;


public class Test {
    public static void main(String[] args) throws IOException {

        MapTools finder = new MapTools();
        finder.FindClients("rzeszow");
        finder.FindServers("rzeszow",100);
    }


}