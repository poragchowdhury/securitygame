package securitygame;

import java.util.ArrayList;
import java.util.Random;


/**
 * BenchMark Defender Agent that takes rational decision based on the defender action cost
 * @author Porag Chowdhury, Anjan Basak
 */

public class RationalDefender extends Defender{
	
	public RationalDefender(String graphFile)
    {
        super("RationalDefender",graphFile);
    }
	
	 public void makeMoves()
	 {
	    	
	    int ns = -1;
	    int nf = -1;
	    int nh = 1;
	    
	    int nominator = Math.abs(Parameters.DEFENDER_BUDGET - (nh*Parameters.HONEYPOT_RATE) - (Parameters.STRENGTHEN_RATE*net.getSize()));
	    int denominator = Math.abs(Parameters.FIREWALL_RATE - Parameters.STRENGTHEN_RATE);
	    
	    nf = (int)Math.floor(nominator/denominator);
	    ns = net.getSize()- nf ;
	    int i=0;
	    for(i=0; i<nf; i++)
	    {
	    	firewall(i, i+1);
	    }
	    for(int j=i; j<(i+ns); j++)
	    {
	    	strengthen(j);
	    }
	    Random r = new Random();
	    int sv = r.nextInt(19)+1;
        int pv = r.nextInt(20);
	    int honepotsneighbornumber = (int)(net.getSize()*0.25);
	   // ArrayList<Integer> listtocheck =  new ArrayList<Integer>();
	    int[] list =  new int[honepotsneighbornumber];
	    int nn = r.nextInt(net.getSize()); 
        for(int k = 0; k < list.length; k++)
        {
        	list[k] = nn % 20;
        	nn++;
        }
        honeypot(sv, pv, list);
        
        dh.close();
	    	
	    	
	 }

}
