package securitygame;

public class GameMaster
{
    public static void main(String[] args)
    {
        //generateGraphs(10);
        //Defender d = new Defender("shield", "3");
        DefenderHelper dh = new DefenderHelper("shield","3");

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
