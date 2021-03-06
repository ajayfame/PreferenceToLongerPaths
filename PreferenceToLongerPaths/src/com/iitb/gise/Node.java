package com.iitb.gise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.javacodegeeks.concurrent.ConcurrentLinkedHashMap;

public class Node implements Comparable<Node> {

	public static final int SOURCE = 0;
	public static final int DESTINATION = 1;
	public static final int NORMAL = 2;
	
	private String nodeId;
	private String nodeName;
	private double x;
	private double y;
	private Map<Node, Integer> arrivalTimeMap = new LinkedHashMap<Node, Integer>();   
	//Integer will store arrival time from adjacent nodes

	private Map<Node, Integer> delayAtSrc;
	private Map<Node, Map<Node,Integer>> departureMatrix = new LinkedHashMap<Node, Map<Node,Integer>>();
	private Map<Node, Node> parentArray = new LinkedHashMap<Node, Node>();
	
	private Route routeUptoNode;
	private ArrayList<Edge> adjacencies;
	private ArrayList<Node> adjacentNodes = new ArrayList<Node>();
	private Set<Node> childList;
	private int travelTime;
	private Node parent;
	private PathUptoNode pathUptoPreviousNode;
	private Map<Node,Integer> adjacentScannedList;
	private ArrayList<Integer> nodeCapacityAtTime;
	private ArrayList<Integer> tempNodeCapacityAtTime;
	private int maxCapacity;
	private int initialOccupancy;
	private int currentOccupancy;
	private boolean scanned;
	private int noOfPathsThroughThisNode;
	private int noOfPeopleThroughThisNode;
	private int waitingTimeAtThisNode;
	private int nodeType;				//SOURCE/DESTINATION/NORMAL
	private Edge edgeTaken = null;
	private Map<String,IntPair> compareRuns=null;
	private int occupancyBefore;
	
	public ArrayList<Node> getAdjacentNodes() {
		return adjacentNodes;
	}

	public void setAdjacentNodes(ArrayList<Node> adjacentNodes) {
		this.adjacentNodes = adjacentNodes;
	}

	public Map<Node, Integer> getDelayAtSrc() {
		return delayAtSrc;
	}

	public void setDelayAtSrc(Map<Node, Integer> delayAtSrc) {
		this.delayAtSrc = delayAtSrc;
	}

	public Route getRouteUptoNode() {
		return routeUptoNode;
	}

	public void setRouteUptoNode(Route routeUptoNode) {
		this.routeUptoNode = routeUptoNode;
	}

	private void initializeNode()
	{ 
		List<Node> list = new ArrayList<Node>(arrivalTimeMap.keySet());
		for(int i=0; i< list.size() ; i++)
		{
			Map<Node,Integer> innerMap = departureMatrix.get(list.get(i));
			for(int j=0;j<list.size();j++)
			{
				if(i==j)
					continue;
				innerMap.put(list.get(j), -1);
			}
		}
	}
	
	public Map<Node, Integer> getArrivalTimeMap() {
		return arrivalTimeMap;
	}

	public void setArrivalTimeMap(Map<Node, Integer> adjacentNodes) {
		this.arrivalTimeMap = adjacentNodes;
	}

	public Map<Node, Map<Node, Integer>> getDepartureMatrix() {
		return departureMatrix;
	}

	public void setDepartureMatrix(Map<Node, Map<Node, Integer>> departureMatrix) {
		this.departureMatrix = departureMatrix;
	}

	public Map<Node, Node> getParentArray() {
		return parentArray;
	}

	public void setParentArray(Map<Node, Node> parentArray) {
		this.parentArray = parentArray;
	}

	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}

	public int getTravelTime() {
		return  this.travelTime;
	}
	
	public int getOccupancyBefore() {
		return occupancyBefore;
	}

	public void setOccupancyBefore(int occupancyBefore) {
		this.occupancyBefore = occupancyBefore;
	}

	public Map<String, IntPair> getCompareRuns() {
		return this.compareRuns;
	}

	public void setCompareRuns(Map<String, IntPair> compareRuns) {
		this.compareRuns = compareRuns;
	}

	public Edge getEdgeTaken() {
		return edgeTaken;
	}

	public void setEdgeTaken(Edge edgeTaken) {
		this.edgeTaken = edgeTaken;
	}

	public ArrayList<Edge> getAdjacencies() {
		return adjacencies;
	}

	public void setAdjacencies(ArrayList<Edge> adjacencies) {
		this.adjacencies = adjacencies;
	}

	public Set<Node> getChildList() {
		return childList;
	}

	public int getWaitingTimeAtThisNode() {
		return waitingTimeAtThisNode;
	}

	public void setWaitingTimeAtThisNode(int waitingTimeAtThisNode) {
		this.waitingTimeAtThisNode = waitingTimeAtThisNode;
	}

	public int getNoOfPathsThroughThisNode() {
		return noOfPathsThroughThisNode;
	}

	public void setNoOfPathsThroughThisNode(int noOfPathsThroughThisNode) {
		this.noOfPathsThroughThisNode = noOfPathsThroughThisNode;
	}

	public int getNoOfPeopleThroughThisNode() {
		return noOfPeopleThroughThisNode;
	}

	public void setNoOfPeopleThroughThisNode(int noOfPeopleThroughThisNode) {
		this.noOfPeopleThroughThisNode = noOfPeopleThroughThisNode;
	}

	public void setChildList(Set<Node> childList) {
		this.childList = childList;
	}

	public ArrayList<Integer> getNodeCapacityAtTime() {
		return nodeCapacityAtTime;
	}

	public void setTempNodeCapacityAtTime(ArrayList<Integer> nodeCapacityAtTime) {
		this.tempNodeCapacityAtTime = nodeCapacityAtTime;
	}

	public ArrayList<Integer> getTempNodeCapacityAtTime() {
		return tempNodeCapacityAtTime;
	}

	public void setNodeCapacityAtTime(ArrayList<Integer> nodeCapacityAtTime) {
		this.nodeCapacityAtTime = nodeCapacityAtTime;
	}
	
	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	
	public int getInitialOccupancy() {
		return initialOccupancy;
	}

	public void setInitialOccupancy(int initialOccupancy) {
		this.initialOccupancy = initialOccupancy;
	}

	public int getCurrentOccupancy() {
		return currentOccupancy;
	}

	public void setCurrentOccupancy(int currentOccupancy) {
		this.currentOccupancy = currentOccupancy;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public PathUptoNode getPathUptoPreviousNode() {
		return pathUptoPreviousNode;
	}

	public void setPathUptoPreviousNode(PathUptoNode pathUptoPreviousNode) {
		this.pathUptoPreviousNode = pathUptoPreviousNode;
	}

	public boolean isScanned() {
		return scanned;
	}

	public void setScanned(boolean scanned) {
		this.scanned = scanned;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public Map<Node,Integer> getAdjacentScannedList() {
		return adjacentScannedList;
	}

	public void setAdjacentScannedList(Map<Node,Integer> adjacentScannedList) {
		this.adjacentScannedList = adjacentScannedList;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int compareTo(Node otherNode) 
	{
		//Returns whether this has minDistance or other(Vertex) has minDistance
		if(this.getTravelTime() < otherNode.getTravelTime())
		{
			return -1;
		}
		else if(this.getTravelTime() > otherNode.getTravelTime())
		{
			return 1;
		}
		else
		{
			//EQUAL -- CHECK NODE TYPE
			if(otherNode.getNodeType()==1) 	//If other node is destination
			{
				return 1;
			}
			else if(this.getNodeType()==1)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
	}
	
	public static double calculateDistance(Node n1, Node n2)
	{
		double x1 = n1.getX();
		double x2 = n2.getX();
		double y1 = n1.getY();
		double y2 = n2.getY();
		
		return (Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));
	}
	
	public void addAdjacentEdge(Edge edge)
	{
		if(this.getAdjacencies() == null)
		{
			this.setAdjacencies(new ArrayList<Edge>());
		}
		this.getAdjacencies().add(edge);
	}
	
	public void addAdjacentScannedEdge(Node node)
	{
		if(this.adjacentScannedList == null)
		{
			this.adjacentScannedList = new ConcurrentLinkedHashMap<Node,Integer>();
		}
		this.adjacentScannedList.put(node,0);
	}
	
	public void addChild(Node node)
	{
		if(this.childList == null)
		{
			this.childList = new LinkedHashSet<Node>();
		}
		this.childList.add(node);
	}
	
	public int getMinimumArrivalTime()
	{
		int min = Integer.MAX_VALUE;
		List<Integer> arrivalTime = new ArrayList<Integer>(this.getArrivalTimeMap().values());
		for(int i=0;i<arrivalTime.size();i++)
		{
			if(arrivalTime.get(i) < min)
			{
				min = arrivalTime.get(i);
			}
		}
		return min;
	}
	public void removeChild(Node node)
	{
		if(this.childList.contains(node))
		{
			this.childList.remove(node);
		}
	}
}