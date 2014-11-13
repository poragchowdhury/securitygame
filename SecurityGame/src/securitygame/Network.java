package securitygame;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Iterator;
import java.util.Random;
/*import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;*/
/**
 * Network class is use for generating a network.
 * Game master will use this class to generate a network. 
 *
 * @author      Porag Chowdhury, Anjon Basak
 * @version     2014/11/01
 */

public class Network {
	private int name;
	private String fullGraphName;//for when the graph is modified by an agent i.e. Miners-1
	private Node[] nodes = new Node[Parameters.NUMBER_OF_NODES];
	 
	public Network(){}

	/**
	 * Constructor used by Game master to initialize network.
	 * @param networkName An integer indicates network name
	 */

	public Network(int networkName)
	{
		name = networkName;
        fullGraphName = ""+name;//for now
		for(int i=0; i<Parameters.NUMBER_OF_NODES; i++)
		{
			nodes[i] = new Node();
			nodes[i].setNodeID(i);
		}
		generateNetwork();
	}

	 /**
	 * Constructor used by Game master to initialize network.
	 * @param networkName An integer indicates network name
	 * @param numNodes An integer indicates number of nodes in the network
	 */
	public Network(int networkName, int numNodes)
	{
		name = networkName;
		fullGraphName = "" + name;
        nodes = new Node[numNodes];
		for(int i=0; i<numNodes; i++){
			nodes[i] = new Node();
			nodes[i].setNodeID(i);
		}
	}

	/**
     * Returns network name.
     * @return network name
     */
	public int getName() {
		return name;
	}

	/**
     * Sets netwrok full name.
     * @param name network name
     */
	public void setName(String name) {
		fullGraphName = name;
	}

	/**
     * Sets netwrok name.
     * @param name network name
     */
	public void setName(int name) {
		this.name = name;
	}

	/**
	 * Returns node
	 * @param nodeId An integer indicates nodeId
     * @return returns node.
     */
	public Node getNode(int nodeId)
	{
		if(nodeId >= nodes.length || nodeId < 0)
			return null;
		for(int i=0; i<nodes.length; i++)
		{
			if(nodes[i].getNodeID()==nodeId)
			{
				return nodes[i];
			}
		}
		return null;
	}

	/**
     * Adds edges to the node.
     * @param routerIndex An integer indicates router id
     * @param adjacencyMatrix A two dimensional array for adjacency
     */
	public void addMoreEdges(int routerIndex, int [][] adjacencyMatrix)
	{
		ArrayList<Integer> routerNeighbors = new ArrayList<Integer>();
		Random r = new Random(name);
		int neighbourCount = 0;
		for (int i = 0; i < nodes.length; i++)
		{
			if(adjacencyMatrix[routerIndex][i] == 1){
				routerNeighbors.add(i);
				neighbourCount++;
			}
		}
		if(neighbourCount >= Parameters.MAX_ROUTER_EDGES)
			return;
		while(neighbourCount < Parameters.MAX_ROUTER_EDGES)
		{
			int neighborindex= r.nextInt(nodes.length);
			if(neighborindex != routerIndex){
				if(!routerNeighbors.contains(neighborindex))
				{
					adjacencyMatrix[routerIndex][neighborindex]=1;
					adjacencyMatrix[neighborindex][routerIndex]=1;
					routerNeighbors.add(neighborindex);
					neighbourCount++;
				}
			}
		}
	}

	/**
     * Returns boolean validating a node to be eligible for Neighbor or not
     * @param currentIndex An integer indicates current node id
     * @param neighborIndex An integer indicates neighbor node id
     * @param adjacencyMatrix A two dimensional array for adjacency
     * @return boolean True/False validating a node to be eligible for Neighbor or not
     */
	public boolean isAllowedToBeNeighbor(int currentIndex, int neighborIndex, int [][] adjacencyMatrix)
	{
		if (currentIndex == neighborIndex)
			return false;
		int neighborCount = 0;
		for(int i=0; i < adjacencyMatrix[neighborIndex].length; ++i)
		{
			if (adjacencyMatrix[neighborIndex][i] == 1)
				neighborCount++;
		}
		if(neighborCount < Parameters.MAX_NEIGHBORS)
			return true;
		else
			return false;

	}

	/**
     * Returns size of the network
     * @return size of the network i.e. number of total nodes
     */
	public int getSize()
	{
		return nodes.length;
	}

	/**
     * Adds Honeypot in the network
     * @param sv An integer indicates security value
     * @param pv An integer indicates point value
     * @param neighbors An integer array indicates all the neighbors
     */	
	public void addHoneypot(int sv, int pv, int[]neighbors)
	{
		Node[] n = new Node[nodes.length+1];
		for(int i = 0; i < nodes.length; i++)
			n[i] = nodes[i];
		n[nodes.length] = new Node(nodes.length,sv,pv,1);

		for(int i = 0; i < neighbors.length; i++)
		{
			n[nodes.length].neighbor.add(nodes[neighbors[i]]);
			nodes[neighbors[i]].neighbor.add(n[nodes.length]);
		}
		nodes = n;
	}

	/**
     * Print hidden network in a file
     */	
	public void printHiddenNetwork()
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter(fullGraphName + "-hidden.graph", "UTF-8");
			for (int i = 0; i < nodes.length; i++)
			{
				Node node = getNode(i);
				if (node.isPublic() == true)
				{
					int neighborSize = node.neighbor.size();
					
					int neighborCounter = 0;
					for(Node neighbor: node.neighbor)
					{
						if(neighbor.getNodeID()!=node.getNodeID())
						{
							if(neighborCounter==neighborSize-1)
								writer.print(neighbor.getNodeID());
							else 
								writer.print(neighbor.getNodeID()+",");
						}
						neighborCounter++;
					}
					writer.println();
				}
				else 
					writer.println("-1");
			}
			for (int i = 0; i < nodes.length; i++)
			{
				Node node = getNode(i);
				if(node.isPublic() == true)
					writer.println(node.getPv()+","+node.getSv()+","+node.getHoneyPot());
				else
					writer.println("-1,-1,-1");
			}
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
     * Print network in a file
     * @param attackerName name of the attacker
     */
	public void printHiddenNetwork(String attackerName)
	{
		PrintWriter writer;
		try {
			//writer = new PrintWriter(attackerName + "-" + name + ".visible", "UTF-8");
            writer = new PrintWriter(attackerName + "-" + fullGraphName + ".visible", "UTF-8");
			for (int i = 0; i < nodes.length; i++)
			{
				//Node node = getNode(i);
				writer.println("-1");
				
			}
			for (int i = 0; i < nodes.length; i++)
			{
				writer.println("-1,-1,-1");
			}
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
     * Print network in a file
     */
	public void printNetwork()
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter(fullGraphName + ".graph", "UTF-8");
			for (int i = 0; i < nodes.length; i++)
			{
				Node node = getNode(i);
				int neighborSize = node.neighbor.size();
				int neighborCounter = 0;

				if (node.neighbor.get(0) == null)
				{
					writer.print("-1");
				}
				else
				{
					for(Node neighbor: node.neighbor)
					{
						if(neighbor.getNodeID()!=node.getNodeID())
						{
							if(neighborCounter==neighborSize-1)
								writer.print(neighbor.getNodeID());
							else 
								writer.print(neighbor.getNodeID()+",");
						}
						neighborCounter++;
					}
				}
				writer.println();
			}
			for (int i = 0; i < nodes.length; i++)
			{
				Node node = getNode(i);
				//writer.println(node.getPv()+","+node.getSv()+","+node.getHoneyPot());
                writer.println(node.getPv()+","+node.getSv()+",-1");
			}
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
     * Print visible network in a file
     */
	public void printVisibleNetwork()
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter(fullGraphName + ".visible", "UTF-8");
			for (int i = 0; i < nodes.length; i++){
				Node node = getNode(i);
				
				if (node.neighbor.size() == 0)
					writer.print("-1");
				else{
					int j;
					for(j = 0; j < node.neighbor.size() - 1; j++){
						Node neighbor = node.neighbor.get(j);
						writer.print(neighbor.getNodeID() + ",");
					}
					writer.print(node.neighbor.get(j).getNodeID());
				}
				writer.println();
			}
			for (int i = 0; i < nodes.length; i++){
				Node node = getNode(i);
				writer.println(node.getPv()+","+node.getSv()+","+node.getHoneyPot());
			}
			writer.close();
		} catch(Exception e) { e.printStackTrace(); }
	}


	/**
     * Returns all the values in a string
     * @return all the values in a string
     */
	public String toString()
	{
		String s = "";
		for (int i = 0; i < nodes.length; i++)
		{
			Node node = getNode(i);
			int neighborSize = node.neighbor.size();
			int neighborCounter = 0;
			for(Node neighbor: node.neighbor)
			{
				if(neighbor.getNodeID()!=node.getNodeID())
				{
					if(neighborCounter==neighborSize-1)
						s+=neighbor.getNodeID();
					else
						s+=neighbor.getNodeID()+",";
				}
				neighborCounter++;
			}
			s+="\n";
		}
		for (int i = 0; i < nodes.length; i++)
		{
			Node node = getNode(i);
			s+=node.getPv()+","+node.getSv()+","+node.isPublic()+"\n";
		}
		return s;
	}
	
	/**
     * Shuffles all the nodes in the network
     */
	public void shuffleNetwork()
	{
		ArrayList<Integer> assigned = new ArrayList<Integer>();
		Random rand = new Random();
		for(int i = 0; i< this.nodes.length; i++)
		{
			
			while(true)
			{
				int id = rand.nextInt(nodes.length);
				if((assigned.size()==0) || (!assigned.contains(id)))
				{
					this.nodes[i].setNodeID(id);
					assigned.add(id);
					break;
				}
				
			}
		}
	}

	/**
     * Generates a random network based on the parameter class and prints it in a file
     */
	public void generateNetwork()
	{
		//Network network = new Network(networkName, numNodes);
		Random r = new Random(name);
		int [][] adjacencyMatrix = new int[Parameters.NUMBER_OF_NODES][Parameters.NUMBER_OF_NODES];
		for(int i =0; i<nodes.length; i++)
			Arrays.fill(adjacencyMatrix[i], 0);
		ArrayList<Integer> completedNodes = new ArrayList<Integer>();
		ArrayList<Integer> tmpNodeStack = new ArrayList<Integer>();
		int currentIndex = 0;
		for (int i = 0; i < nodes.length; i++)
		{
			int localMax = r.nextInt(Parameters.MAX_NEIGHBORS - Parameters.MIN_NEIGHBORS) + Parameters.MIN_NEIGHBORS;
			int neighborCounter = 0;
			ArrayList<Integer> tmpNeighbors = new ArrayList<Integer>();
			ArrayList<Integer> rejectedNeighbors = new ArrayList<Integer>();
			while(true)
			{
				int nodeIndex= r.nextInt(nodes.length);
				int totalNeighbors = 0;
				for(int k=0; k<nodes.length; k++)
					if(adjacencyMatrix[currentIndex][k]==1)
						totalNeighbors++;
				if(totalNeighbors==Parameters.MAX_NEIGHBORS)
					break;
				if(rejectedNeighbors.size()>0)
					if(rejectedNeighbors.size()==(nodes.length-tmpNeighbors.size()-1))
						break;

				if (isAllowedToBeNeighbor(currentIndex, nodeIndex, adjacencyMatrix))
				{
					if((tmpNeighbors.size()>0 && !tmpNeighbors.contains(nodeIndex) && tmpNeighbors.size() < Parameters.MAX_NEIGHBORS) || tmpNeighbors.size()==0)
					{
						//System.out.println("Current Index  " + currentIndex + "neighbor counter "+ neighborCounter + " Num Neighbors " + localMax);
						//System.out.println("Got Neighbor " + nodeIndex);
						adjacencyMatrix[currentIndex][nodeIndex] = 1;
						adjacencyMatrix[nodeIndex][currentIndex] = 1;
						tmpNeighbors.add(nodeIndex);
						if (!tmpNodeStack.contains(nodeIndex)){
							tmpNodeStack.add(nodeIndex);
							//System.out.println("Adding to stack: " + nodeIndex);
						}
						neighborCounter++;
						if(neighborCounter==localMax)
						{	
							//System.out.println("Neighbour Count for " + currentIndex + " : " +neighborCounter);
							break;
						}
					}
					else
						if(rejectedNeighbors.size()>=0 && !rejectedNeighbors.contains(nodeIndex))
							rejectedNeighbors.add(nodeIndex);
				}
				else
					if(rejectedNeighbors.size()>=0 && !rejectedNeighbors.contains(nodeIndex))
						rejectedNeighbors.add(nodeIndex);
			}
			completedNodes.add(currentIndex);
			while(true)
			{
				if (tmpNodeStack.size() == 0)
					break;
				// pick a node from the stack
				currentIndex = tmpNodeStack.get(0);
				//System.out.println("Current index: " + currentIndex);
				tmpNodeStack.remove(0);
				if (!completedNodes.contains(currentIndex))
					break;
			}
		}
		ArrayList<Integer> tmpPublicNodes = new ArrayList<Integer>();
		int publicNodeCounter = 0;
		while(true)
		{
			int nodeIndex= r.nextInt(nodes.length);
			if((tmpPublicNodes.size()>0 && !tmpPublicNodes.contains(nodeIndex)) || tmpPublicNodes.size()==0)
			{
				tmpPublicNodes.add(nodeIndex);
				publicNodeCounter++;
				if(publicNodeCounter==Parameters.NUMBER_OF_PUBLIC_NODES)
					break;
			}
		}

		ArrayList<Integer> tmpRouterNodes = new ArrayList<Integer>();
		int routerNodeCounter = 0;
		while(true)
		{
			int routerNodeIndex= r.nextInt(nodes.length);
			if (!tmpPublicNodes.contains(routerNodeIndex))
			{
				if((tmpRouterNodes.size()>0 && !tmpRouterNodes.contains(routerNodeIndex)) || tmpRouterNodes.size()==0)
				{
					tmpRouterNodes.add(routerNodeIndex);
					routerNodeCounter++;
					if(routerNodeCounter==Parameters.NUMBER_OF_ROUTER_NODES)
						break;
				}
			}
		}

		for(int i = 0; i < nodes.length; ++i)
		{
			Node tempNode = getNode(i);
			tempNode.setNodeID(i);
			tempNode.setHoneyPot(false);
			
			if(tmpPublicNodes.contains(i))
			{
				tempNode.setPv(0);
				tempNode.setSv(0);
			}
			else if(tmpRouterNodes.contains(i))
			{
				// add extra edges to the adjacency matrix
				//System.out.println("Router node : " + i);
				addMoreEdges(i, adjacencyMatrix);
				tempNode.setPv(0);
				int nodeMinSecurityValue= r.nextInt(Parameters.MAX_POINT_VALUE - 1) + 1;
				tempNode.setSv(nodeMinSecurityValue);
			}
			else 
			{
				int nodePointValue= r.nextInt(Parameters.MAX_POINT_VALUE - 1) + 1;
				System.out.println("Setting point value " + nodePointValue + "for node id " + i);
				tempNode.setPv(nodePointValue);
				int randSecurity= r.nextInt(5 - 1) + 1;
				int maxSecurityValue = nodePointValue + randSecurity;
				if (maxSecurityValue > Parameters.MAX_POINT_VALUE)
					maxSecurityValue = Parameters.MAX_POINT_VALUE;
				int minSecurityValue = nodePointValue - randSecurity;
				if (minSecurityValue < 0)
					minSecurityValue = 0;
				int securityValue= r.nextInt(maxSecurityValue - minSecurityValue) + minSecurityValue;
				tempNode.setSv(securityValue);
			}
		}
		for(int i = 0; i < nodes.length; ++i)
			adjacencyMatrix[i][i] = 0;

		for (int i = 0; i < nodes.length; ++i)
		{
			Node tempNode = getNode(i);
			for(int j = 0; j < nodes.length; ++j)
			{
				if (adjacencyMatrix[i][j] == 1)
				{
					Node tempNeighbor = getNode(j);
					tempNode.addNeighbor(tempNeighbor);
				}
			}
		}
		for(int i=0; i<nodes.length; i++)
		{
			if(nodes[i].neighbor.size()==0)
			{
				//add some random neighbor
				int neighborcounter = 0;
				System.out.println("Node "+ i +" has no neighbor");
				Random rand = new Random();
				while(true)
				{
					int nodeid = rand.nextInt(nodes.length-1);
					if(i!=nodeid)
					{

						if(neighborcounter==2)
						{
							break;
						}
						if(nodes[i].neighbor.size()==0)
						{
							nodes[i].neighbor.add(nodes[nodeid]);
							nodes[nodeid].neighbor.add(nodes[i]);
							neighborcounter++;
						}
						else if((nodes[i].neighbor.size()>0) && !(nodes[i].neighbor.contains(nodes[nodeid])))
						{
							nodes[i].neighbor.add(nodes[nodeid]);
							nodes[nodeid].neighbor.add(nodes[i]);
							neighborcounter++;
						}

					}
				}

			}
		}
		//printNetwork(network);
	}

	/*public static void main(String [ ] args)
    {
		//Network nt = Parser.parseGraph("securitygame.graph");
		//Network.printNetwork(nt);
		//Network network = new Network();
		generateNetwork("securitygame");
		//Random r = new Random();
		//for (int i = 0; i < 10; i++)
		//{
		//	int nodepointvalue= r.nextInt(Network.MAX_POINT_VALUE - 1) + 1;
			//System.out.println(nodepointvalue);
		//}
	}*/
}
