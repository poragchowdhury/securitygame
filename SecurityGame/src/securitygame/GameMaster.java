package securitygame;

import java.util.ArrayList;

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
        generateGraphs(numGames);

        //add Defenders here
        ArrayList<Defender> defenders = new ArrayList<Defender>();
        defenders.add(new WhatDoesThisButtonDoDefender("0"));
        defenders.add(new WhatDoesThisButtonDoDefenderV2("0"));

        //get names of defenders
        String[] defenderNames = new String[defenders.size()];
        for(int i = 0; i < defenders.size(); i++)
            defenderNames[i] = defenders.get(i).getName();
        int numDefenders = defenderNames.length;
        //execute defenders
        for(int d = 0; d < numDefenders; d++)
        {
            for(int g = 0; g < numGames; g++)
            {
                Defender defender = getDefender(defenderNames[d],g+"");
                new Thread(defender).start();
                try{Thread.sleep(2000);}catch (Exception e){e.printStackTrace();}
                defender.kill();
                new DefenderHelper(defender.getName(), defender.getGraph());
            }
        }

        //add Attackers here
        ArrayList<Attacker> attackers = new ArrayList<Attacker>();
        attackers.add(new Blitzkrieg());
        attackers.add(new BlitzkriegV2());
        
        //get names of attackers
        String[] attackerNames = new String[attackers.size()];
        for(int i = 0; i < attackers.size(); i++)
            attackerNames[i] = attackers.get(i).getName();
        int numAttackers = attackerNames.length;
        //initialize point matrix
        int[][] points = new int[numDefenders][numAttackers];

        //execute attackers
        for(int d = 0; d < numDefenders; d++)
        {
            String defenderName = defenderNames[d];
            for(int a = 0; a < numAttackers; a++)
            {
                String attackerName = attackerNames[a];
                for(int g = 0; g < numGames; g++)
                {
                    String graphName=g+"";
                    AttackerMonitor am = new AttackerMonitor(attackerName, defenderName, graphName);
                    while(am.getBudget() > 0)
                    {
                        Attacker attacker = getAttacker(defenderName,attackerName,graphName);
                        new Thread(attacker).start();
                        try{Thread.sleep(500);}catch(Exception ex){ex.printStackTrace();}
                        attacker.kill();
                        am.readMove();
                        System.out.println("Budget after move: " + am.getBudget());
                        System.out.println();
                    }
                    am.close();
                    points[d][a]+=am.getPoints();
                }
            }
        }
        //perform analysis
        Analyzer analyzer = new Analyzer(points,attackerNames,defenderNames);
    }

    public static void generateGraphs(int numGraphs)
    {
        for(int i = 0; i < numGraphs; i++)
        {
            Network n = new Network(i);
            n.printNetwork();
        }
    }

    public static Defender getDefender(String name, String file)
    {
        Defender d;
        if(name.equalsIgnoreCase("WhatDoesThisButtonDo")){
            return new WhatDoesThisButtonDoDefender(file);
        }
        if(name.equalsIgnoreCase("WhatDoesThisButtonDoV2")){
            return new WhatDoesThisButtonDoDefenderV2(file);
        }
        return new Defender("","") {
            @Override
            public void makeMoves() {
                System.out.print("check name");
            }
        };
    }
    public static Attacker getAttacker(String defName, String atName, String file)
    {
        Attacker a;
        if(atName.equalsIgnoreCase("Blitzkrieg"))
            return new Blitzkrieg(defName,file);
        if(atName.equalsIgnoreCase("BlitzkriegV2"))
            return new BlitzkriegV2(defName,file);
        return new Attacker("","",""){

			@Override
			public AttackerAction makeSingleAction() {
				System.out.println("check attacker name");
				return null;
			}
			@Override
			protected void initialize() {}
        };

    }
}

