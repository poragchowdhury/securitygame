package securitygame;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Parser
{
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
            network.setName(filename.substring(0,filename.indexOf(".")));
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
						if(x >= 0){
							Node neighbor = network.getNode(x);
							node.addNeighbor(neighbor);
						}
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
					Node node = network.getNode(neighborsCounter);
					while(itr.hasNext())
					{
						int x  =  Integer.parseInt(itr.next());
						node.setPv(x);
						int y = Integer.parseInt(itr.next());
						node.setSv(y);
						int z = Integer.parseInt(itr.next());
						node.setHoneyPot(z);
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
    /**
	 * Parses attacker's .history file and calculates and returns the attacker's current budget
	 * @author Marcus Gutierrez
	 * @param filename
	 * @return attacker's current budget
	 */
	public static int parseAttackerHistory(String filename)
	{
		File csvTrainData = new File(filename);
		try
		{
			CSVParser parser = CSVParser.parse(csvTrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			CSVParser parseRecords= CSVParser.parse(csvTrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			int budget = Parameters.ATTACKER_BUDGET;
			for (CSVRecord csvRecord : parser)
			{
				Iterator<String> itr = csvRecord.iterator();
				int move = Integer.parseInt(itr.next());
				switch(move){
				case 0:
					budget -= Parameters.ATTACK_RATE;
					break;
				case 1:
					budget -= Parameters.SUPERATTACK_RATE;
					break;
				case 2:
					budget -= Parameters.PROBE_SECURITY_RATE;
					break;
				case 3:
					budget -= Parameters.PROBE_POINT_RATE;
					break;
				case 4:
					budget -= Parameters.PROBE_CONNECTIONS_RATE;
					break;
				case 5:
					budget -= Parameters.PROBE_HONEY_RATE;
					break;
				case -1: default:
					budget -= Parameters.INVALID_RATE;
					break;
				}
			}
			return budget;
		}
		catch(NumberFormatException nfe){
			return Parameters.ATTACKER_BUDGET;
		}
		catch (IOException e) { e.printStackTrace();}
		return Parameters.ATTACKER_BUDGET;
	}
}
