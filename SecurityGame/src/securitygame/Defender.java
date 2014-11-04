package securitygame;

import com.sun.xml.internal.fastinfoset.tools.PrintTable;
import org.omg.Dynamic.Parameter;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class Defender
{
    private String name;
    private Network net;
    private DefenderHelper dh;
    public Defender(String newName, String graphFile)
    {
        name = newName;
        net = Parser.parseGraph(graphFile+".graph");
        dh = new DefenderHelper(net,graphFile,name);
        run();
        dh.close();
    }
    public void invalid()
    {
        dh.invalid();
    }
    public void strengthen(int id)
    {
        dh.strengthen(id);
    }

    public void firewall(int id1, int id2)
    {
        dh.firewall(id1,id2);
    }
    public void honeypot(int sv, int pv, int[] newNeighbors)
    {
        dh.honeypot(sv,pv,newNeighbors);
    }
    public void run()
    {
        Random r = new Random();
        while(dh.getBudget() > 0){
            double x = r.nextDouble();
            if(x <= 4.0 / 9.0)
                strengthen(r.nextInt(net.getSize()));
            else if( x <= 8.0 / 9.0)
                firewall(r.nextInt(net.getSize()),r.nextInt(net.getSize()));
            else{
                int[] list =  {0,1};
                honeypot(19,3,list);
            }
        }
    }
}
