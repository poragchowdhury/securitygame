package securitygame;

import com.sun.xml.internal.fastinfoset.tools.PrintTable;
import org.omg.Dynamic.Parameter;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class Defender
{
    private String name;
    private int budget;
    private Network net;
    private PrintWriter pw;
    public Defender(String newName, String graphFile)
    {
        name = newName;
        budget = Parameters.DEFENDER_BUDGET;
        net = Parser.parseGraph(graphFile+".graph");
        try
        {
            pw = new PrintWriter(name+"-"+graphFile + ".defence", "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        run();
        pw.close();
    }
    public void invalid()
    {
        budget -= Parameters.INVALID_RATE;
        pw.write("-1");
        pw.println();
    }
    public void strengthen(int id)
    {
        if(budget < Parameters.STRENGTHEN_RATE)
            invalid();
        else if(net.getNode(id)!=null && net.getNode(id).getPv()!=0)
        {
            budget -= Parameters.STRENGTHEN_RATE;
            net.getNode(id).setSv(net.getNode(id).getSv());
            pw.write("0,"+id);
            pw.println();
        }
        else
            invalid();
    }

    public void firewall(int id1, int id2)
    {
        if(budget < Parameters.FIREWALL_RATE)
            invalid();
        else if(net.getNode(id1) == null || net.getNode(id2) == null)
            invalid();
        else if(net.getNode(id1).neighbor.size()==1 || net.getNode(id2).neighbor.size()==1)
            invalid();
        else if(net.getNode(id1).neighbor.contains(net.getNode(id2)))
        {
            net.getNode(id1).neighbor.remove(net.getNode(id2));
            net.getNode(id2).neighbor.remove(net.getNode(id1));
            budget -= Parameters.FIREWALL_RATE;
            pw.write("1,"+id1+","+id2);
            pw.println();
        }
        else
            invalid();

    }
    public void honeypot(int sv, int pv, int[] newNeighbors)
    {
        if(budget < Parameters.HONEYPOT_RATE)
            invalid();
        else{
            Arrays.sort(newNeighbors);
            for(int i = 0; i < newNeighbors.length-1;i++)
                if(newNeighbors[i]==newNeighbors[i+1] || net.getNode(newNeighbors[i])==null)
                {
                    invalid();
                    return;
                }
            net.addHoneypot(sv, pv, newNeighbors);
            budget -= Parameters.HONEYPOT_RATE;
            String s = "2,"+net.getSize()+","+pv+","+sv+",";
            for(int i =0; i < newNeighbors.length-1;i++)
                s = s + newNeighbors[i]+",";
            s = s + newNeighbors[newNeighbors.length-1];
            pw.write(s);
            pw.println();
        }
    }
    public void run()
    {
        Random r = new Random();
        while(budget > 0){
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
        //strengthen(r.nextInt(net.getSize()));
        //firewall(r.nextInt(net.getSize()),r.nextInt(net.getSize()));
        //int[] list =  {3,2,7,5};
        //honeypot(19,3,list);
        //System.out.print(net.toString());
    }

}
