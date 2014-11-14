package securitygame;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameMaster
{
    public static void main(String[] args)
    {
        int numGames = 3;
        //generateGraphs(numGames);

        ArrayList<Defender> defenders = new ArrayList<Defender>();
        int numDefenders = 0;
        int numAttackers = 0;
        for(int i = 0; i < numGames; i++)
        {
            defenders.add(new WhatDoesThisButtonDoDefender(Integer.toString(i)));
        }

        for(int i = 0; i < defenders.size(); i++)
        {
            Defender d = defenders.get(i);
            new Thread(d).start();
            try{Thread.sleep(2000);}catch (Exception e){e.printStackTrace();}
            d.kill();
            new DefenderHelper(d.getName(), d.getGraph());
        }

        ArrayList<Attacker> attackers = new ArrayList<Attacker>();
        for(int i = 0; i < defenders.size(); i++)
            for(int j = 0; j < numGames; j++)
            {
                attackers.add(new Blitzkrieg(defenders.get(i).getName(),defenders.get(i).getGraph()));
            }
        for(int i = 0; i < defenders.size(); i++)
        {
            String defenderName = defenders.get(i).getName();
            String graphName = defenders.get(i).getGraph();
            Attacker a = new Blitzkrieg(defenderName, graphName);
            AttackerMonitor am = new AttackerMonitor(Blitzkrieg.getName(), defenderName, graphName);

    	    while(am.getBudget() > 0)
            {
                a = new Blitzkrieg(defenderName, graphName);
                new Thread(a).start();
                try{Thread.sleep(500);}catch(Exception ex){ex.printStackTrace();}
                a.kill();
                am.readMove();
                System.out.println("Budget after move: " + am.getBudget());
                System.out.println();
    	    }
            //attackers.add(a);
            am.close();
        }

    }

    public static void generateGraphs(int numGraphs)
    {
        for(int i = 0; i < numGraphs; i++)
        {
            Network n = new Network(i);
            
           // n.shuffleNetwork();
            n.printNetwork();
            
            //n.printHiddenNetwork();
            /*Network nt = Parser.parseGraph("0-hidden.graph");
    		nt.printNetwork();*/
        }
    }
    private static void resetAttackerBudget(String attackerName, String defenseGraph){
    	try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(attackerName + "-" + defenseGraph + ".history", false)));
			pw.print("");
			pw.close();
		} catch (IOException e) {e.printStackTrace();}
    }
}
