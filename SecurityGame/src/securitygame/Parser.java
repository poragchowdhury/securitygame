package securitygame;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Parser {
	
	public static Network parseGraph(String filename)
	{
		File csvtrainData = new File(filename);
		
		try 
		{
			CSVParser parser = CSVParser.parse(csvtrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			CSVParser parserecords= CSVParser.parse(csvtrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			
			int neighborscounter = 0;
			int valuecounter = 0;
			int numberofnodes = parserecords.getRecords().size()/2;
			Network network = new Network(filename+"1",numberofnodes);
			boolean flag = false;
			
			for (CSVRecord csvRecord : parser) 
			{
				Iterator<String> itr = csvRecord.iterator();
				if((neighborscounter<numberofnodes) && flag==false)
				{
					Node node = network.getNode(neighborscounter);
					while(itr.hasNext())
					{
						int x = Integer.parseInt(itr.next());
						Node neighbor = network.getNode(x);
						node.addNeighbor(neighbor);
						
						
					}
					
					if(neighborscounter==numberofnodes-1)
					{
						flag= true;
						neighborscounter=0;
					}
					else
					{
						neighborscounter++;
					}
					
				}
				else if(flag==true && (neighborscounter<numberofnodes))
				{
					int counter = 0;
					Node node = network.getNode(neighborscounter);
					while(itr.hasNext())
					{
						if(counter==2)
						{
							String x = itr.next();
							node.setIspublic(Boolean.parseBoolean(x));
							
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
					neighborscounter++;
				}
			}
			
			return network;
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	
	

}
