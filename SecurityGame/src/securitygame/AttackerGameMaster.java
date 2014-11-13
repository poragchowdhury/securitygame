package securitygame;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AttackerGameMaster
{
    public static void main(String[] args)
    {	
    	String attackerName = "Blitzkrieg";
    	String defender = "WhatDoesThisButtonDo";
    	String defenseGraph = "4";
    	String fullGraphName = defender + "-" + defenseGraph;
    	Attacker a;
    	AttackerHelper ah;
    	Network net = Parser.parseGraph(defender + "-" + defenseGraph + ".defence");
    	net.setName(defender + "-" + defenseGraph);
    	net.printHiddenNetwork(attackerName);
    	
    	resetAttackerBudget(attackerName, fullGraphName);
    	int budget = Parameters.ATTACKER_BUDGET;
    	while(budget > 0){
    		a = new Blitzkrieg(fullGraphName);
    		a.run();
    		ah = new AttackerHelper(a.getName(), fullGraphName);
    		budget = ah.getBudget();
    		System.out.println("Budget after move: " + budget);
    		System.out.println();
    	}
    }
    
    private static void resetAttackerBudget(String attackerName, String defenseGraph){
    	try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(attackerName + "-" + defenseGraph + ".history", false)));
			pw.print("");
			pw.close();
		} catch (IOException e) {e.printStackTrace();}
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
