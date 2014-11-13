package securitygame;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AttackerGameMaster
{
    public static void main(String[] args)
    {
        /*int numGames = 1;
        //generateGraphs(numGames);
        
        ExecutorService exec = Executors.newFixedThreadPool(1);
        ArrayList<Defender> defenders = new ArrayList<Defender>();
        for(int i = 0; i < numGames;i++)
        {
            defenders.add(new WhatDoesThisButtonDoDefender(i+""));
            exec.execute(defenders.get(defenders.size()-1));
        }
		exec.shutdown();
		try {//wait for it to finish
          exec.awaitTermination(5*numGames, TimeUnit.SECONDS);
        } catch (InterruptedException e) {e.printStackTrace();}

        for (Defender defender : defenders)
            new DefenderHelper(defender.getName(), defender.getGraph());*/
    	
    	Attacker a;
    	AttackerHelper ah;
    	Network net = Parser.parseGraph("4.graph");
    	net.setName(4);
    	net.printHiddenNetwork("Blitzkrieg");
    	for(int i = 0; i < 3; i++){
    		//a = new Blitzkrieg("Blitzkrieg", "4");
            a = new Blitzkrieg("4");
    		a.run();
    		ah = new AttackerHelper(a.getName(), "4");
    	}
    }

    public static void generateGraphs(int numGraphs)
    {
        for(int i = 0; i < numGraphs; i++)
        {
            Network n = new Network(i);
            n.printNetwork();
            n.shuffleNetwork();
            
            n.printHiddenNetwork();
            Network nt = Parser.parseGraph("0-hidden.graph");
    		nt.printNetwork();
        }
    }
}
