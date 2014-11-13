package securitygame;

import java.util.ArrayList;

/**
 * Node class is used for creating nodes for the network.
 * Network class will use this class to generate nodes. 
 *
 * @author      Porag Chowdhury, Anjon Basak
 * @version     2014/11/01
 */

public class Node 
{
	private int nodeID;
	private int sv;
	private int pv;
	private int isHoneyPot;
	ArrayList<Node> neighbor = new ArrayList<Node>();
    
	/**
     * Empty Constructor.
     */
	public Node(){
		neighbor = new ArrayList<Node>();
	}

	/**
     * Constructor.
     * @param nodeID An integer indicates nodeId
     * @param sv An integer indicates security value
     * @param pv An integer indicates point value
     * @param isHoneyPot An integer indicates HoneyPot
     */
	public Node(int nodeID, int sv, int pv, int isHoneyPot) {
		super();
		this.nodeID = nodeID;
		this.sv = sv;
		this.pv = pv;
		this.isHoneyPot = isHoneyPot;
		this.neighbor = new ArrayList<Node>();
	}

	/**
     * Add Neighbor to the current node
     * @param neighborNode neighbor node which will the added as a neighbor to the current node
     */
	public void addNeighbor(Node neighborNode)
	{
		this.neighbor.add(neighborNode);
	}

	/**
     * Returns all the neighbor
     * @return arraylist of all the neighbors
     */
	private ArrayList<Node> getNeighbors()
	{
		return this.neighbor;
	}

	/**
     * Deletes neighbor from the current node
     * @param id An integer indicates the nodeId to be removed
     * @return boolean i.e. True/False is the delete operation successful or not
     */
	private boolean deleteNeighbor(int id)
	{
		if(this.neighbor.size() == 1)
			return false;

		for(Node d: this.neighbor)
		{
			if(d.nodeID == id && d.neighbor.size() > 1)
			{
				this.neighbor.remove(this.neighbor.indexOf(d));
				return true;
			}
		}
		return false;
	}

	/**
     * Returns the nodeId
     * @return the nodeId
     */
	public int getNodeID()
	{
		return nodeID;
	}

	/**
     * Sets the nodeId
     */
	public void setNodeID(int nodeID)
	{
		this.nodeID = nodeID;
	}

	/**
     * Returns the security value of the node
     * @return the security value of the node
     */
	public int getSv()
	{
		return sv;
	}
	
	/**
     * Sets the security value of the node
     */
	public void setSv(int sv)
	{
		this.sv = sv;
	}

	/**
     * Returns the point value of the node
     * @return the point value of the node
     */
	public int getPv()
	{
		return pv;
	}
	
	/**
     * Sets the point value of the node
     */
	public void setPv(int pv)
	{
		this.pv = pv;
	}

	/**
     * Returns a node is honeypot or not
     * @return boolean i.e. returns true if a node is honeypot; false if not 
     */
	public boolean isHoneyPot()
	{
		if(isHoneyPot == 1)
			return true;
		return false;
	}

	/**
     * Returns the honeypot value
     * @return integer i.e. returns the integer honeypot value of the node 
     */	
	public int getHoneyPot(){
		return isHoneyPot;
	}
	
	/**
	 * Returns boolean if a node's honeypot status is known
	*/
	public boolean knowsHoneyPot(){
		if(isHoneyPot == -1)
			return false;
		return true;
	}

	/**
     * Sets the node as a honeypot
     */
	public void setHoneyPot(boolean honeyPot)
	{
		if(honeyPot)
			this.isHoneyPot = 1;
		else
			this.isHoneyPot = 0;
	}
	
	public void setHoneyPot(int honeyPot)
	{
		isHoneyPot = honeyPot;
	}

	/**
     * Returns boolean if a node is public or not
     * @return boolean i.e. return true a node is public; False if not
     */
	public boolean isPublic()
	{
		if (this.getSv() == 0 && this.getPv() == 0)
			return true;
		
		return false;
	}
}
