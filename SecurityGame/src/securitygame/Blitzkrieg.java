package securitygame;

import java.util.Random;

public class Blitzkrieg extends Attacker {

	public static final String attackerName = "Blitzkrieg";
	
	public Blitzkrieg(String defenderName, String graphFile) {
		super(attackerName, defenderName, graphFile);
	}

	@Override
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
	
	public static String getName(){
		return attackerName;
	}

}