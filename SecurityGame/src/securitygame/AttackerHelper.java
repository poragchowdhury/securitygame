package securitygame;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

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
    private Random r;
    private ArrayList<Node> availableNodes;

    /**
     * Constructor used by Attacker to initialize visibility file and keep track of attacker history.
     * @param network Graph being secured given a budget
     * @param graphFile Contains original name of graph i.e. "1" for 1.graph
     * @param attackerName Name of defender will be prepended to attacker file i.e. "allinners" for allinners-1.attack
     */
    public AttackerHelper(Network network, String graphFile, String attackerName)
    {
    	budget = Parser.parseAttackerHistory(attackerName + "-" + graphFile + ".history");
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
        budget = Parser.parseAttackerHistory(attackerName + "-" + graphFile + ".history");
        net = Parser.parseGraph(graphFile+".graph");
        visibleNet = Parser.parseGraph(attackerName + "-" + graphFile + ".visible");
        availableNodes = visibleNet.getAvailableNodes();
        r = new Random();
        
        File csv = new File(attackerName+"-"+graphFile+".attack");
		try{
			pw = new PrintWriter(new BufferedWriter(new FileWriter(attackerName + "-" + graphFile + ".history", true)));
			CSVParser parser = CSVParser.parse(csv, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			for(CSVRecord csvRecord : parser){
				Iterator<String> itr = csvRecord.iterator();
                int mode = Integer.parseInt(itr.next());
                int id;
                switch (mode){
                	case 0://attack node
                    id = Integer.parseInt(itr.next());
                    if(isAvailableNode(id) && isValidAttack(id))
                    {
                        budget -= Parameters.ATTACK_RATE;
                        Node n = net.getNode(id);
                        int attackRoll = r.nextInt(Parameters.ATTACK_ROLL) + 1; 
                        if(attackRoll >= n.getSv()){
                        	visibleNet.getNode(id).setPv(n.getPv());
                        	visibleNet.getNode(id).setSv(n.getSv());
                        	visibleNet.getNode(id).setHoneyPot(n.getHoneyPot());
                        	visibleNet.getNode(id).setCaptured(true);
                        	for(int j = 0; j < n.neighbor.size(); j++)
                        		visibleNet.getNode(id).addNeighbor(visibleNet.getNode(n.neighbor.get(j).getNodeID()));
                        	System.out.println("attack on node " + id + " was successful with a roll of " + attackRoll + "!");
                        	pw.println("0," + id + ",true," + attackRoll);
                        }else{
                        	System.out.println("attack on node " + id + " was unsuccessful with a roll of " + attackRoll);
                        	pw.println("0," + id + ",false," + attackRoll);
                        }
                    }
                    else{
                    	System.out.println("Invalid attack on node "+ id + "!");
                    	pw.println("-1");
                        budget -= Parameters.INVALID_RATE;
                    }
                    break;
                	case 1://super attack node
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidSuperAttack(id))
                        {
                            budget -= Parameters.SUPERATTACK_RATE;
                            Node n = net.getNode(id);
                            
                            int attackRoll = r.nextInt(Parameters.SUPERATTACK_ROLL) + 1; 
                            if(attackRoll >= n.getSv()){
                            	visibleNet.getNode(id).setPv(n.getPv());
                            	visibleNet.getNode(id).setSv(n.getSv());
                            	visibleNet.getNode(id).setHoneyPot(n.getHoneyPot());
                            	visibleNet.getNode(id).setCaptured(true);
                            	for(int j = 0; j < n.neighbor.size(); j++)
                            		visibleNet.getNode(id).addNeighbor(visibleNet.getNode(n.neighbor.get(j).getNodeID()));
                            	System.out.println("super attack on node " + id + " was successful with a roll of " + attackRoll + "!");
                            	pw.println("1," + id + ",true," + attackRoll);
                            }else{
                            	System.out.println("super attack on node " + id + " was unsuccessful with a roll of " + attackRoll);
                            	pw.println("1," + id + ",false," + attackRoll);
                            }
                        }
                        else{
                        	System.out.println("Invalid superattack on node "+ id + "!");
                        	pw.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	case 2://probe security value
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidProbeSV(id))
                        {
                            budget -= Parameters.PROBE_SECURITY_RATE;
                            Node n = net.getNode(id);
                            
                            int sv = n.getSv();
                            visibleNet.getNode(id).setSv(sv);
                            System.out.println("probed node " + id + "'s security value: " + sv);
                            pw.println("2," + id + "," + sv);
                        }
                        else{
                        	System.out.println("Invalid probing of security value on node "+ id + "!");
                        	pw.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	case 3://probe point value
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidProbePV(id))
                        {
                            budget -= Parameters.PROBE_POINT_RATE;
                            Node n = net.getNode(id);
                            
                            int pv = n.getPv();
                            visibleNet.getNode(id).setPv(n.getPv());
                            System.out.println("probed node " + id + "'s point value: " + pv);
                            pw.println("3," + id + "," + pv);
                        }
                        else{
                        	System.out.println("Invalid probing of point value on node "+ id + "!");
                        	pw.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	case 4://probe connections
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidProbeConn(id))
                        {
                            budget -= Parameters.PROBE_CONNECTIONS_RATE;
                            Node n = net.getNode(id);
                            
                            int[] nodes = new int[n.neighbor.size()];
                            for(int j = 0; j < n.neighbor.size(); j++){
                        		nodes[j] = n.neighbor.get(j).getNodeID();
                        		visibleNet.getNode(id).addNeighbor(visibleNet.getNode(nodes[j]));
                            }
                            System.out.print("probed node " + id + "'s connections: ");
                            pw.println("4," + id + ",");
                            int k;
                            for(k = 0; k < nodes.length - 1; k++){
                            	System.out.print(nodes[k] + ",");
                            	pw.print(nodes[k] + ",");
                            }
                            System.out.println(nodes[k]);
                            pw.println(nodes[k]);
                        }
                        else{
                        	System.out.println("Invalid probing of connections on node "+ id + "!");
                        	pw.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	case 5://probe honey pot
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidProbeHP(id))
                        {
                            budget -= Parameters.PROBE_HONEY_RATE;
                            Node n = net.getNode(id);
                            
                            visibleNet.getNode(id).setHoneyPot(n.isHoneyPot());
                            System.out.println("probed node " + id + "'s honey pot: " + n.isHoneyPot());
                            pw.println("5," + id + "," + n.isHoneyPot());
                        }
                        else{
                        	System.out.println("Invalid probing of honey pot on node "+ id + "!");
                        	pw.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	default://some other case not defined
                		System.out.println("Invalid Move!");
                		pw.println("-1");
                        budget -= Parameters.INVALID_RATE;
                        break;
                }
            }
            parser.close();
            pw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
		visibleNet.setName(attackerName + "-" + graphFile);
		visibleNet.printVisibleNetwork();
    }

    /**
     * Attacker should call this method when done adding actions.
     */
    public void close()
    {
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
     * Attacks a node
     * @param id The id of the node being attacked
     */
    public void attack(int id)
    {
        if(isValidAttack(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.ATTACK_RATE;
            pw.write("0," + id);
            pw.println();
        }
        else
            invalid();
    }
    
   
    /**
     * Attacks a node with better chances
     * @param id The id of the node being attacked
     */
    public void superAttack(int id)
    {
        if(isValidSuperAttack(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.SUPERATTACK_RATE;
            pw.write("1," + id);
            pw.println();
        }
        else
            invalid();
    }
    
    /**
     * Probes a node to discover its security value
     * @param id The id of the node being probed
     */
    public void probeSecurity(int id)
    {
        if(isValidProbeSV(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.PROBE_SECURITY_RATE;
            pw.write("2," + id);
            pw.println();
        }
        else
            invalid();
    }
    
    /**
     * Probes a node to discover its point value
     * @param id The id of the node being probed
     */
    public void probePoint(int id)
    {
        if(isValidProbePV(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.PROBE_POINT_RATE;
            pw.write("3," + id);
            pw.println();
        }
        else
            invalid();
    }
    
    /**
     * Probes a node to discover its number of connections
     * @param id The id of the node being probed
     */
    public void probeConnections(int id)
    {
        if(isValidProbeConn(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.PROBE_CONNECTIONS_RATE;
            pw.write("4," + id);
            pw.println();
        }
        else
            invalid();
    }
    
    /**
     * Probes a node to discover if it is a honey pot or not
     * @param id The id of the node being probed
     */
    public void probeHoney(int id)
    {
        if(isValidProbeHP(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.PROBE_HONEY_RATE;
            pw.write("5," + id);
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
    
    private boolean isAvailableNode(int id){
    	Node n = new Node(id);
    	return availableNodes.contains(n);
    }
}
