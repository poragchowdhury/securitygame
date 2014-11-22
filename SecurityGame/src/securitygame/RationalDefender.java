package securitygame;

import java.util.ArrayList;
import java.util.Random;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Parameter;


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
	    int nh = (Parameters.DEFENDER_BUDGET / 2) / Parameters.HONEYPOT_RATE;
	    int nominator = Math.abs(Parameters.DEFENDER_BUDGET - (nh*Parameters.HONEYPOT_RATE) - (Parameters.STRENGTHEN_RATE*net.getSize()));
	    int denominator = Math.abs(Parameters.FIREWALL_RATE - Parameters.STRENGTHEN_RATE);
	    
	    nf = (int)Math.floor(nominator/denominator);
	    ns = net.getSize()- nf - Parameters.NUMBER_OF_PUBLIC_NODES;
	    int i = 0;
	    int j = 0;
	    int inc = 0;
	    double defbudget2 = dh.getBudget();
	    for(i=0; i< (nf+inc); i++)
	    {
	    	int nodeid = i % net.getSize();
	    	int nodeneighbourid = (i+1) % net.getSize();
        	if(net.getNode(nodeid).getPv() > 0 && net.getNode(nodeid).getPv() > 0)
        		firewall(nodeid, nodeneighbourid);
	    	else 
	    		inc++;
	    	defbudget2 = dh.getBudget();
	    }

	    inc = 0;
	    
        for(j=i; j<(i+ns+inc); j++)
	    {
        	int nodeid = j % net.getSize();
        	if(net.getNode(nodeid).getPv() > 0 && net.getNode(nodeid).getPv() > 0)
        		strengthen(nodeid);
	    	else 
	    		inc++;
	    	defbudget2 = dh.getBudget();	  	   
	    }
        double defbudget1 = dh.getBudget();

	    Random r = new Random();
	    while (nh > 0)
	    {
		    int sv = r.nextInt(19)+1;
	        int pv = r.nextInt(20);
		    int honepotsneighbornumber = (int)(net.getSize()*0.25);
		   // ArrayList<Integer> listtocheck =  new ArrayList<Integer>();
		    int[] list =  new int[honepotsneighbornumber];
		    int nn = r.nextInt(net.getSize()); 
	        for(int k = 0; k < list.length; k++)
	        {
	        	list[k] = nn % net.getSize();
	        	nn++;
	        }
	        honeypot(sv, pv, list);
	        nh--;
	    }
        
        while(dh.getBudget() > 0)
        {
        	int nodeid = r.nextInt(net.getSize());
        	
        	if(Parameters.FIREWALL_RATE > Parameters.STRENGTHEN_RATE && Parameters.FIREWALL_RATE > Parameters.HONEYPOT_RATE)
        	{
        		int nodeneighbourid = (nodeid+1) % net.getSize();
        		firewall(nodeid, nodeneighbourid);
        	}
        	else{
        		strengthen(nodeid);
        	}
        }
        
        double defbudget = dh.getBudget();
                
        dh.close();
	    	
	    	
	 }

}
