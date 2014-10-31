package securitygame;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Network {
	private String name;
	public static int NUMBER_OF_NODES = 20;
	public static int NUMBER_OF_PUBLIC_NODES = 3;
	public static int NUMBER_OF_PRIVATE_NODES = NUMBER_OF_NODES- NUMBER_OF_PUBLIC_NODES;
	public static int MAX_NEIGHBORS = 5;
	public static int MAX_POINT_VALUE = 20;
	
	private Node[] nodes = new Node[NUMBER_OF_NODES];
	private Node[] publicnodes = new Node[ NUMBER_OF_PUBLIC_NODES];
	private Node[] privatenodes = new Node[NUMBER_OF_PRIVATE_NODES];
	
	
	public Network()
	{
		
	}
	
	public Network(String network_name, int number_of_nodes, int max_neighbors)
	{
		this.name = network_name;
		Network.NUMBER_OF_NODES = number_of_nodes;
		Network.MAX_NEIGHBORS = max_neighbors;
		for(int i=0; i<Network.NUMBER_OF_NODES; i++)
		{
			this.nodes[i] = new Node();
		}
	}
	
	public Node getNode(int nodeindex)
	{
		return this.nodes[nodeindex];
	}
	
	public void printNetwork(Network network)
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
					if(neiborcounter==neighborsize-1)
						writer.print(neighbor.getNodeid());
					else 
						writer.print(neighbor.getNodeid()+",");
					++neiborcounter;
				}
				writer.println();
				
			}
			
			for (int i = 0; i < network.NUMBER_OF_NODES; ++i)
			{
				Node node = network.getNode(i);
				writer.println(node.getPv()+","+node.getSv());
				
			}
			writer.close();
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void generateNetwork(String network_name, int number_of_nodes, int max_neighbours)
	{
		Network network = new Network(network_name, number_of_nodes, max_neighbours);
		int [][] adjacencymatrix = new int[NUMBER_OF_NODES][NUMBER_OF_NODES];
		
		for(int i =0; i<NUMBER_OF_NODES; i++)
		{
			Arrays.fill(adjacencymatrix[i], 0);
		}
		
		for (int i = 0; i < NUMBER_OF_NODES; ++i)
		{
			Random r = new Random();
			int localmax = r.nextInt(MAX_NEIGHBORS - 1) + 1;
			int neighborcounter = 0;
			ArrayList<Integer> tmpneighbors = new ArrayList<Integer>();
			while(true)
			{
				int nodeindex= r.nextInt(Network.NUMBER_OF_NODES);
				if((tmpneighbors.size()>0 && !tmpneighbors.contains(nodeindex)) || tmpneighbors.size()==0)
				{
					adjacencymatrix[i][nodeindex] = 1;
					tmpneighbors.add(nodeindex);
					neighborcounter++;
					if(neighborcounter==localmax)
						break;
				}
				
			}
		}
		
		ArrayList<Integer> tmppublicnodes = new ArrayList<Integer>();
		int publicnodecounter = 0;
		Random r = new Random();
		while(true)
		{
			int nodeindex= r.nextInt(Network.NUMBER_OF_PUBLIC_NODES);
			if((tmppublicnodes.size()>0 && !tmppublicnodes.contains(nodeindex)) || tmppublicnodes.size()==0)
			{
				tmppublicnodes.add(nodeindex);
				publicnodecounter++;
				if(publicnodecounter==NUMBER_OF_PUBLIC_NODES)
					break;
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
			else 
			{
				tempnode.setIspublic(false);
				int nodepointvalue= r.nextInt(Network.MAX_POINT_VALUE - 1) + 1;
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
		generateNetwork("securitygame", 20, 5);
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