package securitygame;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class DefenderHelper
{
    private Network net;
    private String name;
    private PrintWriter pw;
    private int budget;
    public DefenderHelper(Network n, String file, String s)
    {
        budget = Parameters.DEFENDER_BUDGET;
        net = n;
        name = s;
        try
        {
            pw = new PrintWriter(name+"-"+file + ".defence", "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public DefenderHelper(String defenderName, String graphFile){
        budget = Parameters.DEFENDER_BUDGET;
        net = Parser.parseGraph(graphFile+".graph");
        File csvTrainData = new File(defenderName+"-"+graphFile+".defence");
		try
		{
			CSVParser parser = CSVParser.parse(csvTrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			//CSVParser parseRecords= CSVParser.parse(csvTrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			for (CSVRecord csvRecord : parser)
			{
				Iterator<String> itr = csvRecord.iterator();
                int mode = Integer.parseInt(itr.next());
                switch (mode){
                    case 0://strengthen
                        if(budget > Parameters.STRENGTHEN_RATE){
                            int id = Integer.parseInt(itr.next());
                            Node n = net.getNode(id);
                            if(n != null){
                                budget -= Parameters.STRENGTHEN_RATE;
                                n.setSv(n.getSv()+1);
                            }
                            else
                                budget -= Parameters.INVALID_RATE;
                        }
                        break;
                    case 1://firewall
                        if(budget < Parameters.FIREWALL_RATE)
                            budget -= Parameters.INVALID_RATE;
                        else
                        {
                            int id1 = Integer.parseInt(itr.next());
                            int id2 = Integer.parseInt(itr.next());
                            Node n1 = net.getNode(id1);
                            Node n2 = net.getNode(id2);
                            if(n1 == null || n2 == null)
                                budget -= Parameters.INVALID_RATE;
                            else if(n1.neighbor.size()==1 || n2.neighbor.size()==1)
                                budget -= Parameters.INVALID_RATE;
                            else if(n1.neighbor.contains(n2))
                            {
                                n1.neighbor.remove(n2);
                                n2.neighbor.remove(n1);
                                budget -= Parameters.FIREWALL_RATE;
                            }
                        }
                        break;
                    case 2://honeypot
                        if(budget < Parameters.HONEYPOT_RATE)
                            budget -= Parameters.INVALID_RATE;
                        else
                        {
                            int sv = Integer.parseInt(itr.next());
                            int pv = Integer.parseInt(itr.next());
                            ArrayList<Integer> newNeighbors = new ArrayList<Integer>();
                            while (itr.hasNext())
                                newNeighbors.add(Integer.parseInt(itr.next()));
                            int[] n = new int[newNeighbors.size()];
                            for(int i = 0; i < n.length; i++)
                                n[i] = newNeighbors.get(i);
                            net.addHoneypot(sv, pv, n);
                            budget -= Parameters.HONEYPOT_RATE;
                        }
                     break;
                     default://invald
                        budget -= Parameters.INVALID_RATE;
                     break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        net.setName(Integer.parseInt(defenderName+"-"+graphFile));
        net.printNetwork();
    }

    public void close()
    {
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
            String s = "2,"+sv+","+pv+",";
            for(int i =0; i < newNeighbors.length-1;i++)
                s = s + newNeighbors[i]+",";
            s = s + newNeighbors[newNeighbors.length-1];
            pw.write(s);
            pw.println();
        }
    }
    public int getBudget()
    {
        return budget;
    }
}
