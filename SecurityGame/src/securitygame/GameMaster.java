package securitygame;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameMaster
{
    public static void main(String[] args)
    {
        int numGames = 5;
        generateGraphs(numGames);

        ArrayList<Defender> defenders = new ArrayList<Defender>();
        for(int i = 0; i < numGames; i++)
            defenders.add(new WhatDoesThisButtonDoDefender(i+""));
        for(int i = 0; i < defenders.size(); i++)
        {
            Defender d = defenders.get(i);
            /*new Thread(d).start();
            try{Thread.sleep(2000);}catch (Exception e){e.printStackTrace();}
            d.kill();
            new DefenderHelper(d.getName(), d.getGraph());*/
        }

        ArrayList<Attacker> attackers = new ArrayList<Attacker>();
        for(int i = 0; i < defenders.size(); i++)
        {
            AttackerHelper ah;
            String defName = defenders.get(i).getName();
            String name = defenders.get(i).getGraph();
            Network net = Parser.parseGraph(defName+"-"+name+".graph");
            Attacker a = new Blitzkrieg(defName+"-"+name);
            net.printHiddenNetwork(a.getName());
            new Thread(a).start();
            try{Thread.sleep(2000);}catch(Exception ex){ex.printStackTrace();}
            a.kill();
            //attackers.add(a);
        }

    }

    public static void generateGraphs(int numGraphs)
    {
        for(int i = 0; i < numGraphs; i++)
        {
            Network n = new Network(i);
            n.printNetwork();
            /*n.shuffleNetwork();
            
            n.printHiddenNetwork();
            Network nt = Parser.parseGraph("0-hidden.graph");
    		nt.printNetwork();*/
        }
    }
}
