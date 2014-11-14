package securitygame;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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

public class AttackerMonitor
{
    private Network net;
    private Network visibleNet;
    
    private String attackerName;
    private String defenderName;
    private String graphName;
    private PrintWriter history;
    
    private int budget;
    private Random r;
    private ArrayList<Node> availableNodes;

    public AttackerMonitor(String attackerName, String defenderName, String graphName){
    	budget = Parameters.ATTACKER_BUDGET;
    	System.out.println("Initial Budget1: " + budget);
    	r = new Random();
    	this.attackerName = attackerName;
    	this.defenderName = defenderName;
    	this.graphName = graphName;
    	try {
    		net = Parser.parseGraph(defenderName + "-" + graphName + ".graph");
    		visibleNet = Parser.parseGraph(defenderName + "-" + graphName + "-hidden.graph");
    		availableNodes = visibleNet.getAvailableNodes();
    		
    		//clears history and adds the public nodes to the history
			history = new PrintWriter(new FileWriter(attackerName + "-" + defenderName + "-" + graphName + ".history", false));
			ArrayList<Node> publicNodes = visibleNet.getCapturedNodes();
			for(int i = 0; i < publicNodes.size(); i++){
				ArrayList<Node> neighbors = publicNodes.get(i).getNeighborList();
				history.print("6,");
				history.print(publicNodes.get(i).getNodeID()+",");
				for(int j = 0; j < neighbors.size(); j++){
					if(j == neighbors.size() - 1)
						history.print(neighbors.get(j).getNodeID());
					else
						history.print(neighbors.get(j).getNodeID() + ",");
				}
				history.println();
				history.close();
			}
		} catch (IOException e) {e.printStackTrace();}
    	history.close();
    }
    
    public void readMove(){
		try{
			history = new PrintWriter(new FileWriter(attackerName + "-" + defenderName + "-" + graphName + ".history", true));
			
			File csv = new File(attackerName + "-" + defenderName + "-" + graphName + ".attack");
			CSVParser parser = CSVParser.parse(csv, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			for(CSVRecord csvRecord : parser){
				Iterator<String> itr = csvRecord.iterator();
                int mode = Integer.parseInt(itr.next());
                int id;
                String neighbors = ",";
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
                        	for(int j = 0; j < n.neighbor.size(); j++){
                        		visibleNet.getNode(id).addNeighbor(visibleNet.getNode(n.neighbor.get(j).getNodeID()));
                        		neighbors += n.neighbor.get(j).getNodeID() + ",";
                        	}
                        	neighbors = neighbors.substring(0, neighbors.length()-1);
                        	System.out.println("attack on node " + id + " was successful with a roll of " + attackRoll + "!");
                        	history.println("0," + id + ",true," + attackRoll + "," + n.getPv() + "," + n.getSv() + "," + n.getHoneyPot() + neighbors);
                        }else{
                        	System.out.println("attack on node " + id + " was unsuccessful with a roll of " + attackRoll);
                        	history.println("0," + id + ",false," + attackRoll);
                        }
                    }
                    else{
                    	System.out.println("Invalid attack on node "+ id + "!");
                    	history.println("-1");
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
                            	for(int j = 0; j < n.neighbor.size(); j++){
                            		visibleNet.getNode(id).addNeighbor(visibleNet.getNode(n.neighbor.get(j).getNodeID()));
                            		neighbors += n.neighbor.get(j).getNodeID() + ",";
                            	}
                            	neighbors = neighbors.substring(0, neighbors.length()-1);
                            	System.out.println("super attack on node " + id + " was successful with a roll of " + attackRoll + "!");
                            	history.println("1," + id + ",true," + attackRoll + "," + n.getPv() + "," + n.getSv() + "," + n.getHoneyPot() + neighbors);
                            }else{
                            	System.out.println("super attack on node " + id + " was unsuccessful with a roll of " + attackRoll);
                            	history.println("1," + id + ",false," + attackRoll);
                            }
                        }
                        else{
                        	System.out.println("Invalid superattack on node "+ id + "!");
                        	history.println("-1");
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
                            history.println("2," + id + "," + sv);
                        }
                        else{
                        	System.out.println("Invalid probing of security value on node "+ id + "!");
                        	history.println("-1");
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
                            history.println("3," + id + "," + pv);
                        }
                        else{
                        	System.out.println("Invalid probing of point value on node "+ id + "!");
                        	history.println("-1");
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
                            System.out.println("probed node " + id + "'s connections: " + n.neighbor.size());
                            history.println("4," + id + "," + n.neighbor.size());
                        }
                        else{
                        	System.out.println("Invalid probing of connections on node "+ id + "!");
                        	history.println("-1");
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
                            history.println("5," + id + "," + n.getHoneyPot());
                        }
                        else{
                        	System.out.println("Invalid probing of honey pot on node "+ id + "!");
                        	history.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	default://some other case not defined
                		System.out.println("Invalid Move!");
                		history.println("-1");
                        budget -= Parameters.INVALID_RATE;
                        break;
                }
            }
            parser.close();
            history.close();
            availableNodes = visibleNet.getAvailableNodes();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Attacker should call this method when done adding actions.
     */
    public void close()
    {
        history.close();
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
