package securitygame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Pits Attacker and Defender agents against one another in the name of Science!
 *
 * @author Oscar Veliz, Porag Chowdhury, Anjon Basak, Marcus Gutierrez
 * @version 2014/11/14
 */
public class GameMaster
{
    public static void main(String[] args)
    {
        int numGames = 1;
        //generateGraphs(numGames);

        ArrayList<Defender> defenders = new ArrayList<Defender>();
        for(int i = 0; i < numGames; i++)
        {
            //add defenders in this loop
            defenders.add(new WhatDoesThisButtonDoDefender(Integer.toString(i)));
        }
        //execute defenders
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
        {
            for(int j = 0; j < numGames; j++)
            {
                //add attackers in this loop
                attackers.add(new Blitzkrieg(defenders.get(i).getName(),defenders.get(i).getGraph()));
            }
        }
        //get names of attackers and defenders for future analysis
        //and to find out how many attackers and defenders there are
        Set<String> defenderSet = new HashSet<String>();
        Set<String> attackerSet = new HashSet<String>();
        for(int i = 0; i < defenders.size(); i++)
            defenderSet.add(defenders.get(i).getName());
        for(int i = 0; i < attackers.size(); i++)
            attackerSet.add(attackers.get(i).getName());
        int numDefenders = defenderSet.size();
        int numAttackers = attackerSet.size();
        int[][] points = new int[numDefenders][numAttackers];

        //run attackers here
        /*for(int i = 0; i < defenders.size(); i++)
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
        }*/

        for(int d = 0; d < numDefenders; d++)
        {
            for(int a = 0; a < numAttackers; a++)
            {
                for(int g = 0; g < numGames; g++)
                {
                    int j = d*numDefenders+g;
                    String defenderName = defenders.get(j).getName();
                    String graphName = defenders.get(j).getGraph();
                    int i = d *numAttackers*numDefenders + (a*numAttackers)+g;
                    Attacker attacker = attackers.get(i);
                    AttackerMonitor am = new AttackerMonitor(attacker.getName(), defenderName, graphName);
                    while(am.getBudget() > 0)
                    {
                        attacker = attackers.get(i);
                        new Thread(attacker).start();
                        try{Thread.sleep(500);}catch(Exception ex){ex.printStackTrace();}
                        attacker.kill();
                        am.readMove();
                        System.out.println("Budget after move: " + am.getBudget());
                        System.out.println();
                    }
                    am.close();
                    points[i][j]+=am.getPoints();
                }
            }
        }
        //perform analysis
        Analyzer analyzer = new Analyzer(points,
                                        attackerSet.toArray(new String[attackerSet.size()]),
                                        defenderSet.toArray(new String[defenderSet.size()]));
    }

    public static void generateGraphs(int numGraphs)
    {
        for(int i = 0; i < numGraphs; i++)
        {
            Network n = new Network(i);
            n.printNetwork();
        }
    }
}
