package securitygame;

import java.util.ArrayList;

/**
 * Node class is used for creating nodes for the network.
 * Network class will use this class to generate nodes. 
 *
 * @author      Porag Chowdhury, Anjon Basak
 * @version     2014/11/12
 */

public class Node 
{
	private int nodeID;
	private int sv;
	private int pv;
	private int isHoneyPot; //-1 means honeypot is unknown, 0 means false, 1 means true
	private boolean captured;
	private int bestRoll = -1;
	private int neighborAmount = -1;
	public ArrayList<Node> neighbor = new ArrayList<Node>();
    
	/**
     * Empty Constructor.
     */
	public Node(){}
	
	/**
     * used for comparison purposes.
     */
	public Node(int id){
		nodeID = id;
	}

	/**
     * Constructor.
     * @param nodeID An integer indicates nodeId
     * @param sv An integer indicates security value
     * @param pv An integer indicates point value
     * @param isHoneyPot A boolean indicates HoneyPot
     */
	public Node(int nodeID, int sv, int pv, boolean isHoneyPot) {
		super();
		this.nodeID = nodeID;
		this.sv = sv;
		this.pv = pv;
		if(isHoneyPot)
			this.isHoneyPot = 1;
		else
			this.isHoneyPot = 0;
		if(sv == 0 && pv == 0)
			captured = true;
		else
			captured = false;
	}
	
	/**
     * Constructor.
     * @param nodeID An integer indicates nodeId
     * @param sv An integer indicates security value
     * @param pv An integer indicates point value
     * @param isHoneyPot indicates HoneyPot status
     */
	public Node(int nodeID, int sv, int pv, int isHoneyPot) {
		super();
		this.nodeID = nodeID;
		this.sv = sv;
		this.pv = pv;
		this.isHoneyPot = isHoneyPot;
		if(sv == 0 && pv == 0)
			captured = true;
		else
			captured = false;
	}
	
	/**
     * Constructor.
     * @param nodeID An integer indicates nodeId
     * @param sv An integer indicates security value
     * @param pv An integer indicates point value
     * @param isHoneyPot indicates HoneyPot status
     * @param captured indicates if a node has been captured
     */
	public Node(int nodeID, int sv, int pv, int isHoneyPot, boolean captured) {
		super();
		this.nodeID = nodeID;
		this.sv = sv;
		this.pv = pv;
		this.isHoneyPot = isHoneyPot;
		this.captured = captured;
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
		if(sv == 0)
			captured = true;
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
	 * Sends the known honeypot status of this node. If the honeypot status is unknown (-1),
	 * false will be returned. knowsHoneyPot() should be used in conjunction with this
	 * method or use getHoneyPot() to find the specific status of a honeypot.
	 * @Marcus Gutierrez
	 * @return if the node is a known honey pot, returns false if -1
	 */
	public boolean isHoneyPot()
	{
		if(isHoneyPot == 1)
			return true;
		return false;
	}
	
	/**
	 * Returns the current status of a honey pot.
	 * -1 -> true honeypot status is unknown
	 * 0 -> this node is not a honeypot
	 * 1 -> this node is a honeypot
	 * @author Marcus Gutierrez
	 * @return honeypot's status
	 */
	public int getHoneyPot(){
		return isHoneyPot;
	}
	
	/**
	 * Returns the knowledge of this node's honeypot status.
	 * If isHoneyPot is 0 or 1, this method returns true, because these
	 * values represent node status with certainty.
	 * If isHoneyPot is -1, this method returns false, because the true
	 * honeypot status of this node is unknown.
	 * @author Marcus Gutierrez
	 * @return the visibility of the honeypot status of this node
	 */
	public boolean knowsHoneyPot(){
		if(isHoneyPot == -1)
			return false;
		return true;
	}

	/**
	 * Sets the honeypot status of this node
	 * @author Marcus Gutierrez
	 * @param honeyPot sets the isHoneyPot to a known value (not -1)
	 */
	public void setHoneyPot(boolean honeyPot)
	{
		if(honeyPot)
			isHoneyPot = 1;
		else
			isHoneyPot = 0;
	}
	
	/**
	 * Sets the honeypot status of this node
	 * -1 -> true honeypot status is unknown
	 * 0 -> this node is not a honeypot
	 * 1 -> this node is a honeypot
	 * @author Marcus Gutierrez
	 * @param honeyPot sets the isHoneyPot field variable
	 */
	public void setHoneyPot(int honeyPot)
	{
		isHoneyPot = honeyPot;
	}
	
	/**
	 * Returns captured
	 * @author Marcus Gutierrez
	 * @return captured
	 */
	public boolean isCaptured(){
		return captured;
	}
	
	/**
	 * sets this.captured attribute
	 * @author Marcus Gutierrez
	 * @param captured value to set this.captured to
	 */
	public void setCaptured(boolean captured){
		this.captured = captured;
	}
	
	public int getBestRoll(){
		return bestRoll;
	}
	
	public void setBestRoll(int bestRoll){
		this.bestRoll = bestRoll;
	}
	
	public int getNeighborAmount(){
		return neighborAmount;
	}
	
	public void setNeighborAmount(int amt){
		neighborAmount = amt;
	}
	
	/**
     * Add Neighbor to the current node
     * @param neighborNode neighbor node which will the added as a neighbor to the current node
     */
	public void addNeighbor(Node neighborNode)
	{
		neighborAmount++;
		this.neighbor.add(neighborNode);
	}

	/**
     * Returns the neighbor
     * @return Node of idx
     */
	public Node getNeighbor(int idx)
	{
		return neighbor.get(idx);
	}
	
	/**
     * Returns the neighbor
     * @return Node of idx
     */
	public ArrayList<Node> getNeighborList()
	{
		return neighbor;
	}
	
	/**
	 * Overridden equals method that just compares NodeID
	 */
	public final boolean equals(Object o){
		Node n = (Node)o;
		if(n.getNodeID() == nodeID)
			return true;
		return false;
	}
}