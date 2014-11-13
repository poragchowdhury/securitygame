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
    //protected Network netVisible;
    protected ArrayList<Node> visibleNodes;
    protected AttackerHelper ah;
    protected String attackerName;
    protected String graph;
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
        Network netVisible = Parser.parseGraph(graphFile+".graph");
        
        ah = new AttackerHelper(netVisible, graphFile, agentName);
    }

    /**
     * Attacker selects to perform a regular attack on a node.
     *
     * @param id Node's ID number
     */
    public void attack(int id)
    {
        ah.attack(id);
    }
    
    /**
     * Attacker selects to perform a strong attack on a node.
     *
     * @param id Node's ID number
     */
    public void superAttack(int id)
    {
        ah.superAttack(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its security value.
     *
     * @param id Node's ID number
     */
    public void probeSecurity(int id)
    {
        ah.probeSecurity(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its point value.
     *
     * @param id Node's ID number
     */
    public void probePoints(int id)
    {
        ah.probePoint(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its number of connections.
     *
     * @param id Node's ID number
     */
    public void probeConnections(int id)
    {
        ah.probeConnections(id);
    }
    
    /**
     * Attacker selects to probe a node to learn if the node is a honey pot.
     *
     * @param id Node's ID number
     */
    public void probeHoneypot(int id)
    {
        ah.probeHoney(id);
    }

    /**
     * Add your decision logic here in your subclass
     */
    public final void run()
    {
    	makeMove();
        ah.close();
    }
    
    abstract void makeMove();

    /**
     * Get Agent Name used by GameMaster.
     * @return Name of defender
     */
    public String getName()
    {
        return attackerName;
    }

    /**
     * Get Game used by GameMaster
     * @return graph number
     */
    public String getGraph()
    {
        return graph;
    }
}
