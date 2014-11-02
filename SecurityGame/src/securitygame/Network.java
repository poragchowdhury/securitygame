package securitygame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Network {
	private String name;
	public static int NUMBER_OF_NODES = 10;
	public static int NUMBER_OF_PUBLIC_NODES = 2;
	public static int NUMBER_OF_ROUTER_NODES = 2;
	public static int NUMBER_OF_PRIVATE_NODES = NUMBER_OF_NODES - NUMBER_OF_PUBLIC_NODES - NUMBER_OF_ROUTER_NODES;
	public static int MAX_NEIGHBORS = 3;
	public static int MIN_NEIGHBORS = 2;
	public static int MAX_POINT_VALUE = 20;
	public static int MAX_ROUTER_EDGES = 5;

	private Node[] nodes = new Node[NUMBER_OF_NODES];
	//private Node[] publicnodes = new Node[ NUMBER_OF_PUBLIC_NODES];
	//private Node[] privatenodes = new Node[NUMBER_OF_PRIVATE_NODES];


	public Network()
	{

	}
	
	public Network(String networkname, int numberofnodes)
	{
		this.name = networkname;
		Network.NUMBER_OF_NODES = numberofnodes;
		for(int i=0; i<Network.NUMBER_OF_NODES; i++)
		{
			this.nodes[i] = new Node();
			nodes[i].setNodeid(i);
		}
		
	}

	public Network(String network_name, int number_of_nodes, int max_neighbors, int min_neighbors)
	{
		this.name = network_name;
		Network.NUMBER_OF_NODES = number_of_nodes;
		Network.MAX_NEIGHBORS = max_neighbors;
		Network.MIN_NEIGHBORS = min_neighbors;
		for(int i=0; i<Network.NUMBER_OF_NODES; i++)
		{
			this.nodes[i] = new Node();
			
		}
	}

	public Node getNode(int nodeindex)
	{
		return this.nodes[nodeindex];
	}

	public void addMoreEdges(int routerindex, int [][] adjacencymatrix)
	{
		ArrayList<Integer> routerneighbors = new ArrayList<Integer>();
		Random r = new Random();
		int neighbourcount = 0;

		for (int i = 0; i < Network.NUMBER_OF_NODES; ++i)
		{
			if(adjacencymatrix[routerindex][i] == 1){
				routerneighbors.add(i);
				neighbourcount++;
			}
		}

		if(neighbourcount >= Network.MAX_ROUTER_EDGES)
			return;



		while(neighbourcount < Network.MAX_ROUTER_EDGES)
		{
			int neighborindex= r.nextInt(Network.NUMBER_OF_NODES);
			if(neighborindex != routerindex){
				if(!routerneighbors.contains(neighborindex))
				{
					adjacencymatrix[routerindex][neighborindex]=1;
					adjacencymatrix[neighborindex][routerindex]=1;
					routerneighbors.add(neighborindex);
					neighbourcount++;
				}
			}
		}

	}

	public boolean isAllowedToBeNeighbor(int currentindex, int neighborindex, int [][] adjacencymatrix)
	{
		if (currentindex == neighborindex)
			return false;
		int neighborcount = 0;
		for(int i=0; i < adjacencymatrix[neighborindex].length; ++i)
		{
			if (adjacencymatrix[neighborindex][i] == 1)
				neighborcount++;
		}
		if(neighborcount < Network.MAX_NEIGHBORS)
			return true;
		else 
			return false;

	}

	public static void printNetwork(Network network)
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter(network.name + ".graph", "UTF-8");

			for (int i = 0; i < network.NUMBER_OF_NODES; ++i)
			{

				Node node = network.getNode(i);
				int neighborsize = node.neighbor.size();
				int neiborcounter = 0;
				for(Node neighbor: node.neighbor)
				{

					if(neighbor.getNodeid()!=node.getNodeid())
					{
						if(neiborcounter==neighborsize-1)
							writer.print(neighbor.getNodeid());
						else 
							writer.print(neighbor.getNodeid()+",");
					}

					//System.out.println(neighbor.getNodeid());
					++neiborcounter;
				}
				writer.println();

			}

			for (int i = 0; i < network.NUMBER_OF_NODES; ++i)
			{
				Node node = network.getNode(i);
				writer.println(node.getPv()+","+node.getSv()+","+node.isIspublic());

			}
			writer.close();
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	

	public static void generateNetwork(String network_name, int number_of_nodes, int max_neighbours, int min_neighbors)
	{
		Network network = new Network(network_name, number_of_nodes, max_neighbours, min_neighbors);
		int [][] adjacencymatrix = new int[NUMBER_OF_NODES][NUMBER_OF_NODES];

		for(int i =0; i<NUMBER_OF_NODES; i++)
		{
			Arrays.fill(adjacencymatrix[i], 0);
		}
		ArrayList<Integer> completednodes = new ArrayList<Integer>();
		ArrayList<Integer> tmpnodestack = new ArrayList<Integer>();
		int currentindex = 0;
		//System.out.println("Current index at first: " + currentindex);
		for (int i = 0; i < NUMBER_OF_NODES; ++i)
		{
			Random r = new Random();
			int localmax = r.nextInt(MAX_NEIGHBORS - MIN_NEIGHBORS) + MIN_NEIGHBORS;
			int neighborcounter = 0;
			ArrayList<Integer> tmpneighbors = new ArrayList<Integer>();
			ArrayList<Integer> rejectedneighbors = new ArrayList<Integer>();

			while(true)
			{
				int nodeindex= r.nextInt(Network.NUMBER_OF_NODES);
				int totalneighbors = 0;

				for(int k=0; k<Network.NUMBER_OF_NODES; k++)
				{
					if(adjacencymatrix[currentindex][k]==1)
					{
						totalneighbors++;
					}
				}

				if(totalneighbors==Network.MAX_NEIGHBORS)
				{
					break;
				}


				if(rejectedneighbors.size()>0)
				{
					if(rejectedneighbors.size()==(Network.NUMBER_OF_NODES-tmpneighbors.size()-1))
					{
						break;
					}
				}

				if (network.isAllowedToBeNeighbor(currentindex, nodeindex, adjacencymatrix) == true)
				{
					if((tmpneighbors.size()>0 && !tmpneighbors.contains(nodeindex) && tmpneighbors.size() < network.MAX_NEIGHBORS) || tmpneighbors.size()==0)
					{
						//System.out.println("Current Index  " + currentindex + "neighbor counter "+ neighborcounter + " Num Neighbors " + localmax);
						//System.out.println("Got Neighbor " + nodeindex);

						adjacencymatrix[currentindex][nodeindex] = 1;
						adjacencymatrix[nodeindex][currentindex] = 1;
						tmpneighbors.add(nodeindex);
						if (!tmpnodestack.contains(nodeindex)){
							tmpnodestack.add(nodeindex);
							//System.out.println("Adding to stack: " + nodeindex);
						}
						neighborcounter++;
						if(neighborcounter==localmax)
						{	
							//System.out.println("Neighbour Count for " + currentindex + " : " +neighborcounter);
							break;
						}
					}
					else
					{
						if(rejectedneighbors.size()>=0 && !rejectedneighbors.contains(nodeindex))
						{
							rejectedneighbors.add(nodeindex);
						}
					}
				}
				else
				{
					if(rejectedneighbors.size()>=0 && !rejectedneighbors.contains(nodeindex))
					{
						rejectedneighbors.add(nodeindex);
					}
				}

			}

			completednodes.add(currentindex);

			while(true)
			{
				if (tmpnodestack.size() == 0)
					break;

				// pick a node from the stack
				currentindex = tmpnodestack.get(0);
				//System.out.println("Current index: " + currentindex);
				tmpnodestack.remove(0);

				if (!completednodes.contains(currentindex))
				{					
					break;
				}
			}
		}

		ArrayList<Integer> tmppublicnodes = new ArrayList<Integer>();
		int publicnodecounter = 0;
		Random r = new Random();
		while(true)
		{
			int nodeindex= r.nextInt(Network.NUMBER_OF_NODES);
			if((tmppublicnodes.size()>0 && !tmppublicnodes.contains(nodeindex)) || tmppublicnodes.size()==0)
			{
				tmppublicnodes.add(nodeindex);
				publicnodecounter++;
				if(publicnodecounter==NUMBER_OF_PUBLIC_NODES)
					break;
			}
		}

		ArrayList<Integer> tmprouternodes = new ArrayList<Integer>();
		int routernodecounter = 0;
		while(true)
		{
			int routernodeindex= r.nextInt(Network.NUMBER_OF_NODES);
			if (!tmppublicnodes.contains(routernodeindex))
			{
				if((tmprouternodes.size()>0 && !tmprouternodes.contains(routernodeindex)) || tmprouternodes.size()==0)
				{
					tmprouternodes.add(routernodeindex);
					routernodecounter++;
					if(routernodecounter==NUMBER_OF_ROUTER_NODES)
						break;
				}
			}
		}

		for(int i = 0; i < NUMBER_OF_NODES; ++i)
		{
			Node tempnode = network.getNode(i);
			tempnode.setNodeid(i);
			tempnode.setIshoneypot(false);
			if(tmppublicnodes.contains(i))
			{
				tempnode.setIspublic(true);
			}
			else if(tmprouternodes.contains(i))
			{
				// add extra edges to the adjacency matrix
				//System.out.println("Router node : " + i);
				network.addMoreEdges(i, adjacencymatrix);
				tempnode.setIspublic(false);
				tempnode.setPv(0);
				int nodeminsecurityvalue= r.nextInt(Network.MAX_POINT_VALUE - 1) + 1;
				tempnode.setSv(nodeminsecurityvalue);
			}
			else 
			{
				tempnode.setIspublic(false);
				int nodepointvalue= r.nextInt(Network.MAX_POINT_VALUE - 1) + 1;
				System.out.println("Setting point value " + nodepointvalue + "for node id " + i);
				tempnode.setPv(nodepointvalue);

				int securityrandomiztion= r.nextInt(5 - 1) + 1;

				int nodemaxsecurityvalue = nodepointvalue + securityrandomiztion;
				if (nodemaxsecurityvalue > MAX_POINT_VALUE)
					nodemaxsecurityvalue = MAX_POINT_VALUE;
				int nodeminsecurityvalue = nodepointvalue - securityrandomiztion;
				if (nodeminsecurityvalue < 0)
					nodeminsecurityvalue = 0;

				int nodesecurityvalue= r.nextInt(nodemaxsecurityvalue - nodeminsecurityvalue) + nodeminsecurityvalue;

				tempnode.setSv(nodesecurityvalue);

			}

		}

		for(int i = 0; i < NUMBER_OF_NODES; ++i)
		{
			adjacencymatrix[i][i] = 0;
		}

		for (int i = 0; i < NUMBER_OF_NODES; ++i)
		{
			Node tempnode = network.getNode(i);

			for(int j = 0; j < NUMBER_OF_NODES; ++j)
			{
				if (adjacencymatrix[i][j] == 1)
				{
					Node tempneighbor = network.getNode(j);
					tempnode.addNeighbor(tempneighbor);
				}
			}

		}
		network.printNetwork(network);
	}

	public static void main(String [ ] args){
		
		Network nt = Parser.parseGraph("securitygame.graph");
		Network.printNetwork(nt);
		
		/*generateNetwork("securitygame", Network.NUMBER_OF_NODES, Network.MAX_NEIGHBORS, Network.MIN_NEIGHBORS);

		Random r = new Random();
		for (int i = 0; i < 10; i++)
		{
			int nodepointvalue= r.nextInt(Network.MAX_POINT_VALUE - 1) + 1;
			//System.out.println(nodepointvalue);
		}*/
	}

}


class Node 
{
	private int nodeid;
	private int sv;
	private int pv;
	boolean ishoneypot;
	ArrayList<Node> neighbor = new ArrayList<Node>();
	boolean ispublic;

	public Node()
	{

	}

	public Node(int nodeid, int sv, int pv, boolean ishoneypot, boolean ispublic) {
		super();
		this.nodeid = nodeid;
		this.sv = sv;
		this.pv = pv;
		this.ishoneypot = ishoneypot;
		this.ispublic = ispublic;
	}


	public void addNeighbor(Node neighbornode)
	{
		this.neighbor.add(neighbornode);
	}

	private ArrayList<Node> getNeighbors()
	{
		return this.neighbor;
	}

	private boolean deleteNeighbor(int nodeid)
	{
		if(this.neighbor.size() == 1)
			return false;

		for(Node d: this.neighbor)
		{
			if(d.nodeid == nodeid && d.neighbor.size() > 1)
			{
				this.neighbor.remove(this.neighbor.indexOf(d));
				return true;
			}
		}
		return false;
	}

	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public int getSv() {
		return sv;
	}

	public void setSv(int sv) {
		this.sv = sv;
	}

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public boolean isIshoneypot() {
		return ishoneypot;
	}

	public void setIshoneypot(boolean ishoneypot) {
		this.ishoneypot = ishoneypot;
	}

	public boolean isIspublic() {
		return ispublic;
	}

	public void setIspublic(boolean ispublic) {
		this.ispublic = ispublic;
	}


}