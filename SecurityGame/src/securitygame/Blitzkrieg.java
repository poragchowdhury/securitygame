package securitygame;

import java.util.Random;

/**
 * Example attacker agent.
 * @author Marcus Gutierrez
 * @version 2014/11/14
 */
public class Blitzkrieg extends Attacker {
    /**
     * Constructor
     * @param defenderName defender's name
     * @param graphFile graph to read
     */
	public Blitzkrieg(String defenderName, String graphFile) {
		super("Blitzkrieg", defenderName, graphFile);
	}

    /**
     * Example Logic
     */
	public void makeMove() {
		Random r = new Random();
		int nodeID = r.nextInt(10);
		int move = r.nextInt(6);
		if(move == 0)
			attack(nodeID);
		else if(move == 1)
			superAttack(nodeID);
		else if(move == 2)
			probeSecurity(nodeID);
		else if(move == 3)
			probePoints(nodeID);
		else if(move == 4)
			probeConnections(nodeID);
		else if(move == 5)
			probeHoneypot(nodeID);
	}
}