package securitygame;

import java.security.PublicKey;

public class GameMaster
{
    public static void main(String[] args)
    {
        generateGraphs(10);
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
