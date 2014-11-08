package securitygame;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Parser class is used for parsing the graph file.
 * Defender agents, Game master will use this class to parse a graph file. 
 *
 * @author      Porag Chowdhury, Anjon Basak
 * @version     2014/11/01
 */

public class Parser
{
	/**
	 * Returns a network object by parsing a provided graph file as a parameter
	 * @param filename A string indicates the filename of the graph
	 * @return network returns the network object of the corresponding graph file
	 */
	public static Network parseGraph(String filename)
	{
		File csvTrainData = new File(filename);
		try 
		{
			CSVParser parser = CSVParser.parse(csvTrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			CSVParser parseRecords= CSVParser.parse(csvTrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			int neighborsCounter = 0;
			int valueCounter = 0;
			int numNodes = parseRecords.getRecords().size()/2;
			//Network network = new Network(filename+"1",numNodes);
            Network network = new Network(0,numNodes);
			boolean flag = false;
			for (CSVRecord csvRecord : parser) 
			{
				Iterator<String> itr = csvRecord.iterator();
				if((neighborsCounter<numNodes) && flag==false)
				{
					Node node = network.getNode(neighborsCounter);
					while(itr.hasNext())
					{
						int x = Integer.parseInt(itr.next());
						Node neighbor = network.getNode(x);
						node.addNeighbor(neighbor);
					}
					if(neighborsCounter==numNodes-1)
					{
						flag = true;
						neighborsCounter=0;
					}
					else
						neighborsCounter++;
				}
				else if(flag && (neighborsCounter<numNodes))
				{
					int counter = 0;
					Node node = network.getNode(neighborsCounter);
					while(itr.hasNext())
					{
						if(counter==2)
						{
							String x = itr.next();
							node.setHoneyPot(Boolean.parseBoolean(x));
						}
						else if(counter<2)
						{
							int x  =  Integer.parseInt(itr.next());
							node.setPv(x);
							int y = Integer.parseInt(itr.next());
							node.setSv(y);
						}
						counter+=2;
					}
					neighborsCounter++;
				}
			}
			return network;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}
