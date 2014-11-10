package securitygame;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Auxiliary class for creating and parsing .attack files.
 * Attacker will use the three parameter constructor and combination of the two attack methods and the four probe methods
 * to generate an .attack file. Have Attacker remember to call close() when finished for safety.
 * Game Master will use the two parameter constructor for parsing original network and attacker file to generate the attacker's visible network and history.
 *
 * Actions deemed invalid will be charged the Parameters.INVALID_RATE value.
 *
 * @author      Marcus Gutierrez
 * @version     Nov 7, 2014
 */

public class AttackerHelper
{
    private Network net;
    private Network visibleNet;
    private String name;
    private PrintWriter pw;
    private int budget;

    /**
     * Constructor used by Attacker to initialize visibility file and keep track of attacker history.
     * @param network Graph being secured given a budget
     * @param graphFile Contains original name of graph i.e. "1" for 1.graph
     * @param attackerName Name of defender will be prepended to attacker file i.e. "allinners" for allinners-1.attack
     */
    public AttackerHelper(Network network, String graphFile, String attackerName)
    {
        budget = Parameters.ATTACKER_BUDGET;
        net = network;
        name = attackerName;
        try
        {
            pw = new PrintWriter(name+"-"+graphFile + ".attack", "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Constructor used by GameMaster to initialize a new visibility graph based on the attacker's visibility of the graph.
     * @param attackerName Name of attacker prepended to an .attack file i.e. "allinners" for allinners-1.attack
     * @param graphFile Contains original name of graph i.e. "1" for 1.graph
     */
    public AttackerHelper(String attackerName, String graphFile){
        name = attackerName;
        budget = Parameters.ATTACKER_BUDGET;
        net = Parser.parseGraph(graphFile+".graph");
        visibleNet = Parser.parseGraph(attackerName + "-" + graphFile + ".attackVis");
        File csv = new File(attackerName+"-"+graphFile+".attack");
		try{
			CSVParser parser = CSVParser.parse(csv, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			for(CSVRecord csvRecord : parser){
				Iterator<String> itr = csvRecord.iterator();
                int mode = Integer.parseInt(itr.next());
                switch (mode){
                	case 0://attack node
                    int id = Integer.parseInt(itr.next());
                    if(isValidAttack(id))
                    {
                        budget -= Parameters.ATTACK_RATE;
                        Node n = net.getNode(id);
                        n.setSv(n.getSv()+1);
                    }
                    else
                        budget -= Parameters.INVALID_RATE;
                    break;
                    case 1://firewall
                        int id1 = Integer.parseInt(itr.next());
                        int id2 = Integer.parseInt(itr.next());
                        if(isValidFirewall(id1,id2)){
                            Node n1 = net.getNode(id1);
                            Node n2 = net.getNode(id2);
                            n1.neighbor.remove(n2);
                            n2.neighbor.remove(n1);
                            budget -= Parameters.FIREWALL_RATE;
                        }
                        else
                            budget -= Parameters.INVALID_RATE;
                        break;
                    case 2://honeypot
                        int sv = Integer.parseInt(itr.next());
                        int pv = Integer.parseInt(itr.next());
                        ArrayList<Integer> newNeighbors = new ArrayList<Integer>();
                        while (itr.hasNext())
                            newNeighbors.add(Integer.parseInt(itr.next()));
                        int[] n = new int[newNeighbors.size()];
                        for(int i = 0; i < n.length; i++)
                            n[i] = newNeighbors.get(i);
                        if(isValidHoneypot(sv, pv, n)){
                            net.addHoneypot(sv, pv, n);
                            budget -= Parameters.HONEYPOT_RATE;
                        }
                        else
                            budget -= Parameters.INVALID_RATE;
                     break;
                     default://some other case not defined
                        budget -= Parameters.INVALID_RATE;
                     break;
                }
            }
            parser.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        net.setName(name+"-"+graphFile);
        net.shuffleNetwork();//avoid predictable location of honeypot (last node in list)
        net.printNetwork();
    }

    /**
     * Attacker should call this method when done adding actions.
     */
    public void close()
    {
    	visibleNet.printNetwork();
        pw.close();
    }

    /**
     * Action has been deemed invalid
     */
    public void invalid()
    {
        budget -= Parameters.INVALID_RATE;
        pw.write("-1");
        pw.println();
    }

    /**
     * Adds 1 to a node security value if the node is not public or not already at maximum security.
     * @param id The id of the node being strengthened
     */
    public void strengthen(int id)
    {
        if(isValidStrengthen(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.STRENGTHEN_RATE;
            n.setSv(n.getSv());
            pw.write("0,"+id);
            pw.println();
        }
        else
            invalid();
    }

    /**
     * Removes the edge between two nodes. Will not remove if doing so will isolate a node. Will not remove if there is no
     * edge to remove.
     *
     * @param id1 First node's id
     * @param id2 Second node's id
     */
    public void firewall(int id1, int id2)
    {
        if(isValidFirewall(id1, id2))
        {
            Node n1 = net.getNode(id1);
            Node n2 = net.getNode(id2);
            n1.neighbor.remove(n2);
            n2.neighbor.remove(n1);
            budget -= Parameters.FIREWALL_RATE;
            pw.write("1,"+id1+","+id2);
            pw.println();
        }
        else
            invalid();

    }

    /**
     * Adds a honeypot node to the graph if possible. Otherwise charges an invalid.
     * @param sv Security Value for the honeypot
     * @param pv Point Value for the honeypot
     * @param newNeighbors Array of Node ID's specifying which nodes to connect the honeypot to
     */
    public void honeypot(int sv, int pv, int[] newNeighbors)
    {
        if(isValidHoneypot(sv, pv, newNeighbors))
        {
            net.addHoneypot(sv, pv, newNeighbors);
            budget -= Parameters.HONEYPOT_RATE;
            String s = "2,"+sv+","+pv+",";
            for(int i =0; i < newNeighbors.length-1;i++)
                s = s + newNeighbors[i]+",";
            s = s + newNeighbors[newNeighbors.length-1];
            pw.write(s);
            pw.println();
        }
        else
            invalid();
    }

    /**
     * Returns current budget.
     * @return current budget
     */
    public int getBudget()
    {
        return budget;
    }

    public boolean isValidAttack(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.ATTACK_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidSuperAttack(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.SUPERATTACK_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidProbeSV(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.PROBE_SECURITY_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidProbePV(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.PROBE_POINT_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidProbeConn(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.PROBE_CONNECTIONS_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidProbeHP(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.PROBE_HONEY_RATE || n == null)
            return false;
        return true;
    }
}
