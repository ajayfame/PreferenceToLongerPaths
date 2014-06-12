package com.iitb.gise;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainProgram {
	
	private static PreferenceToLongerPaths prefToLongerPaths;
	
	static int sum = 0;
	static int avg = 0;
	static int count = 0;
	public static void readNodeAndEdgeFiles(String nodeFile, String edgeFile)
	{
		String csvFile = nodeFile;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try 
		{
	 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				String[] stringNode = line.split(cvsSplitBy);
				String nodeId = stringNode[0];
				String nodeName = stringNode[1];
				double x = Double.parseDouble(stringNode[2]);
				double y = Double.parseDouble(stringNode[3]);
				int maxCapacity = (int)Double.parseDouble(stringNode[4]);
				int initialOccupancy = (int)Double.parseDouble(stringNode[5]);
				String nodeTypeC = stringNode[6];
				int nodeType = 2;
				if(nodeTypeC.equals("S"))
					nodeType = Node.SOURCE;
				else if(nodeTypeC.equals("D"))
					nodeType = Node.DESTINATION;
				else
					nodeType = Node.NORMAL;
				prefToLongerPaths.addNodeToGraph(nodeId, nodeName, x, y, maxCapacity, initialOccupancy, nodeType);
				
			}
			csvFile = edgeFile;
			br.close();
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				count++;
				
				String[] stringEdge = line.split(cvsSplitBy);
				int edgeID = Integer.parseInt(stringEdge[0]);
				String edgeName = stringEdge[1];
				String sourceName = stringEdge[2];
				String targetName = stringEdge[3];
				int travelTime = (int)Double.parseDouble(stringEdge[4]);
				int maxCapacity = (int)Double.parseDouble(stringEdge[5]);
				sum+=maxCapacity;
				prefToLongerPaths.addEdgeToGraph(edgeID, edgeName, sourceName, targetName, maxCapacity, travelTime);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			if (br != null) {
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static void changeNodeCap(String nodeFile)
	{
		String csvFile = nodeFile;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try 
		{
	 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				String[] stringNode = line.split(cvsSplitBy);
				String nodeName = stringNode[1];
				int initialOccupancy = (int)Double.parseDouble(stringNode[3]);			
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			if (br != null) {
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void readNodeAndEdgeFiles(String nodeFile, String edgeFile, String a)
	{
		String csvFile = nodeFile;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try 
		{
	 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				String[] stringNode = line.split(cvsSplitBy);
				String nodeId = stringNode[0];
				String nodeName = stringNode[1];
				double x = Double.parseDouble(stringNode[2]);
				double y = Double.parseDouble(stringNode[3]);
				int maxCapacity = (int)Double.parseDouble(stringNode[4]);
				int initialOccupancy = (int)Double.parseDouble(stringNode[5]);
				String nodeTypeC = stringNode[6];
				int nodeType = 2;
				if(nodeTypeC.equals("S"))
					nodeType = Node.SOURCE;
				else if(nodeTypeC.equals("D"))
					nodeType = Node.DESTINATION;
				else
					nodeType = Node.NORMAL;
				prefToLongerPaths.addNodeToGraph(nodeId, nodeName, x, y, maxCapacity, initialOccupancy, nodeType);
			}
			csvFile = edgeFile;
			br.close();
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				count++;
				
				String[] stringEdge = line.split(cvsSplitBy);
				int edgeID = Integer.parseInt(stringEdge[0]);
				String edgeName = stringEdge[1];
				String sourceName = stringEdge[2];
				String targetName = stringEdge[3];
				int travelTime = (int)Double.parseDouble(stringEdge[4]);
				int maxCapacity = (int)Double.parseDouble(stringEdge[5]);
				sum+=maxCapacity;
				prefToLongerPaths.addEdgeToGraph(edgeID, edgeName, sourceName, targetName, maxCapacity, travelTime);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			if (br != null) {
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void readNodeAndEdgeFiles(String nodeFile, String edgeFile, int overload)
	{
		String csvFile = nodeFile;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try 
		{
	 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				//System.out.println(line);
				String[] stringNode = line.split(cvsSplitBy);
				String nodeId = stringNode[0];
				String nodeName = stringNode[1];
				int maxCapacity = (int)Double.parseDouble(stringNode[2]);
				int initialOccupancy = (int)Double.parseDouble(stringNode[3]);
				String nodeTypeC = stringNode[4];
				int nodeType = 2;
				if(nodeTypeC.equals("S"))
					nodeType = Node.SOURCE;
				else if(nodeTypeC.equals("D"))
					nodeType = Node.DESTINATION;
				else
					nodeType = Node.NORMAL;
				prefToLongerPaths.addNodeToGraph(nodeId, nodeName, maxCapacity, initialOccupancy, nodeType);
			}
			csvFile = edgeFile;
			br.close();
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				count++;
				//System.out.println(line);
				String[] stringEdge = line.split(cvsSplitBy);
				int edgeID = Integer.parseInt(stringEdge[0]);
				String edgeName = stringEdge[1];
				String sourceName = stringEdge[2];
				String targetName = stringEdge[3];
				int travelTime = (int)Double.parseDouble(stringEdge[4]);
				int maxCapacity = (int)Double.parseDouble(stringEdge[5]);
				sum+=maxCapacity;
				prefToLongerPaths.addEdgeToGraph(edgeID, edgeName, sourceName, targetName, maxCapacity, travelTime);
				
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			if (br != null) {
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String args[])
	{
		prefToLongerPaths = new PreferenceToLongerPaths();
		String nodeFile = "/Users/MLGupta/Documents/node42.csv";
		String edgeFile = "/Users/MLGupta/Documents/edge42.csv";

		readNodeAndEdgeFiles(nodeFile, edgeFile, 1);
		System.out.println("average Edge Cap :" + Math.round(1.0*sum/count));

		long startTime1 = System.currentTimeMillis();
		prefToLongerPaths.preferenceToLongerPaths();
		long endTime1 = System.currentTimeMillis();
		System.out.println("Running time :" + (endTime1-startTime1) );
	}
}
