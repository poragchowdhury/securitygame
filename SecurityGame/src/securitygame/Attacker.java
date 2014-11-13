package securitygame;

import java.util.ArrayList;
import java.util.Random;

/**
 * Attacker agent. The actions for the attacker in this game include attacking a node, super attacking a node, 
 * probing for security values of a node, probing for the point value of a node, probing number of connections, and probing for honey pots.
 * All logic/equations/formulas/etc for how your attacker decides to select actions should be included in run()
 */
public abstract class Attacker implements Runnable
{
    protected ArrayList<Node> visibleNodes;
    protected AttackerHelper ah;
    protected String attackerName;
    private String graph;
    private Network netVisible;
    protected ArrayList<Node> capturedNodes;
    protected ArrayList<Node> availableNodes;
    protected int budget;
    private volatile boolean isAlive = true;
    /**
     * Constructor.
     * Parses Network stored in graphFile.
     * Performs Attacker logic to select actions.
     * Outputs [agentName]-[graphFile].attack with selected actions
     * @param agentName Attacker agent's name i.e. "Sharks"
     * @param graphFile String containing number of visibility network i.e. "1914"
     */
    public Attacker(String agentName, String graphFile)
    {
        attackerName = agentName;
        graph = graphFile;
        netVisible = Parser.parseGraph(agentName + "-" + graph+".visible");
        capturedNodes = netVisible.getCapturedNodes();
        availableNodes = netVisible.getAvailableNodes();
        ah = new AttackerHelper(netVisible, graph, agentName);
        budget = Parser.parseAttackerHistory(attackerName + "-" + graph + ".history");
        
        int i;
		System.out.print("Available Nodes: ");
		if(availableNodes.size() > 1){
			for(i = 0; i < availableNodes.size() - 1; i++)
				System.out.print(availableNodes.get(i).getNodeID() + ",");
			System.out.println(availableNodes.get(i).getNodeID());
		} else if(availableNodes.size() == 1) {
			System.out.println(availableNodes.get(0).getNodeID());
		} else {
			System.out.println(-1);
		}
		
		int j;
		System.out.print("Captured Nodes: ");
		if(availableNodes.size() > 1){
		for(j = 0; j < capturedNodes.size() - 1; j++)
			System.out.print(capturedNodes.get(j).getNodeID() + ",");
		System.out.println(capturedNodes.get(j).getNodeID());
		} else if(capturedNodes.size() == 1) {
			System.out.println(capturedNodes.get(0).getNodeID());
		} else {
			System.out.println(-1);
		}
    }

    /**
     * Attacker selects to perform a regular attack on a node.
     *
     * @param id Node's ID number
     */
    protected final void attack(int id)
    {
        ah.attack(id);
    }
    
    /**
     * Attacker selects to perform a strong attack on a node.
     *
     * @param id Node's ID number
     */
    protected final void superAttack(int id)
    {
        ah.superAttack(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its security value.
     *
     * @param id Node's ID number
     */
    protected final void probeSecurity(int id)
    {
        ah.probeSecurity(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its point value.
     *
     * @param id Node's ID number
     */
    protected void probePoints(int id)
    {
        ah.probePoint(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its number of connections.
     *
     * @param id Node's ID number
     */
    protected final void probeConnections(int id)
    {
        ah.probeConnections(id);
    }
    
    /**
     * Attacker selects to probe a node to learn if the node is a honey pot.
     *
     * @param id Node's ID number
     */
    protected final void probeHoneypot(int id)
    {
        ah.probeHoney(id);
    }

    /**
     * Add your decision logic here in your subclass
     */
    public final void run()
    {
    	while(isAlive)
        {
            makeMove();
            isAlive = false;
        }
        ah.close();
    }
    
    public abstract void makeMove();

    /**
     * Get Agent Name used by GameMaster.
     * @return Name of defender
     */
    public final String getName()
    {
        return attackerName;
    }

    /**
     * Get Game used by GameMaster
     * @return graph number
     */
    public final String getGraph()
    {
        return graph;
    }
    /**
     * kills long running defenders
     */
    public final void kill()
    {
        isAlive = false;
    }
}
