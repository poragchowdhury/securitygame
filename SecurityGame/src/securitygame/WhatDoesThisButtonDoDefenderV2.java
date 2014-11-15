package securitygame;

import java.util.Random;

/**
 * Example Defender Agent that decides on actions at random
 * @author Oscar Veliz
 */
public class WhatDoesThisButtonDoDefenderV2 extends Defender
{
    public WhatDoesThisButtonDoDefenderV2(String graphFile)
    {
        super("WhatDoesThisButtonDoV2",graphFile);
    }

    /**
     * Defender's logic goes here. Remember to close the defence file when done by calling dh.close()
     */
    public void makeMoves()
    {
    	Random r = new Random();
        int tries = 0;
        int node = r.nextInt(net.getSize());
        while(!dh.isValidStrengthen(node) && tries++ < 10)
            node = r.nextInt();
        strengthen(node);
        dh.close();
    }
}
