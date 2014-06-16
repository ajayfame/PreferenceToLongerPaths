package com.iitb.gise;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class PreferenceToLongerPaths {

	private Graph graph = new Graph();
	private Set<Node> PQAdditionList = new HashSet<Node>();
	private static double humanWalkingSpeed = 1.5;			//in m/s
	private int pathId = 1;
	private ArrayList<Route> pathList = new ArrayList<Route>();
	private Set<String> distinctRoutes = new LinkedHashSet<String>();
	List<Node> srcList = new ArrayList<Node>();
	int evacuationTime = 0;
	
	public Set<String> getDistinctRoutes() {
		return distinctRoutes;
	}

	public void setDistinctRoutes(Set<String> distinctRoutes) {
		this.distinctRoutes = distinctRoutes;
	}

	public int getPathId() {
		return pathId;
	}

	public void setPathId(int pathId) {
		this.pathId = pathId;
	}

	public ArrayList<Route> getPathList() {
		return pathList;
	}

	public void setPathList(ArrayList<Route> pathList) {
		this.pathList = pathList;
	}

	public Set<Node> getPQAdditionList() {
		return PQAdditionList;
	}

	public void setPQAdditionList(Set<Node> pQAdditionList) {
		PQAdditionList = pQAdditionList;
	}

	public static double getHumanWalkingSpeed() {
		return humanWalkingSpeed;
	}

	public static void setHumanWalkingSpeed(double humanWalkingSpeed) {
		PreferenceToLongerPaths.humanWalkingSpeed = humanWalkingSpeed;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	//finding the vertex by name
	public Node getNode(String nodeName) {
		Iterator<Node> it = this.getGraph().getNodeList().iterator();
		while (it.hasNext()) 
		{
			// System.out.println("getNode : 1");
			Node temp = it.next();
			if (temp.getNodeName().equals(nodeName))
				return temp;
		}
		return null;
	}
	
	public void addNodeToGraph(String nodeId, String nodeName, double x, double y, 
			int maxCapacity, int initialOccupancy, int nodeType)
	{
		Node node = new Node();
		node.setNodeId(nodeId);
		node.setNodeName(nodeName);
		node.setX(x);
		node.setY(y);
		node.setMaxCapacity(maxCapacity);
		node.setInitialOccupancy(initialOccupancy);
		node.setCurrentOccupancy(initialOccupancy);
		node.setNodeType(nodeType);
		node.setAdjacencies(null);
		node.setAdjacentScannedList(null);
		node.setParent(null);
		node.setPathUptoPreviousNode(null);
		node.setScanned(false);
		node.setChildList(null);
		if(nodeType == Node.DESTINATION)
			node.setMaxCapacity(Integer.MAX_VALUE);
		if(nodeType == Node.SOURCE)
		{
			node.setTravelTime(0);
			srcList.add(node);
		}
		else
			node.setTravelTime(Integer.MAX_VALUE);
		ArrayList<Integer> nodeCapacityAtTime = new ArrayList<Integer>();
		nodeCapacityAtTime.add(node.getMaxCapacity());
		node.setNodeCapacityAtTime(nodeCapacityAtTime);
		this.getGraph().addNode(node);
	}
	
	public void addNodeToGraph(String nodeId, String nodeName, 
			int maxCapacity, int initialOccupancy, int nodeType)
	{
		Node node = new Node();
		node.setNodeId(nodeId);
		node.setNodeName(nodeName);
		node.setMaxCapacity(maxCapacity);
		node.setInitialOccupancy(initialOccupancy);
		node.setCurrentOccupancy(initialOccupancy);
		node.setNodeType(nodeType);
		node.setAdjacencies(null);
		node.setAdjacentScannedList(null);
		node.setParent(null);
		node.setPathUptoPreviousNode(null);
		node.setScanned(false);
		node.setChildList(null);
		if(nodeType == Node.DESTINATION)
			node.setMaxCapacity(Integer.MAX_VALUE);
		if(nodeType == Node.SOURCE)
		{
			node.setTravelTime(0);
			srcList.add(node);
		}
		else
			node.setTravelTime(Integer.MAX_VALUE);
		ArrayList<Integer> nodeCapacityAtTime = new ArrayList<Integer>();
		nodeCapacityAtTime.add(node.getMaxCapacity());
		node.setNodeCapacityAtTime(nodeCapacityAtTime);
		this.getGraph().addNode(node);
	}
	
	public void addEdgeToGraph(int edgeID, String edgeName, String sourceName, 
			String targetName, int maxIntakeCapacity)
	{
		Edge edge = new Edge();
		edge.setEdgeID(edgeID);
		edge.setEdgeName(edgeName);
		edge.setMaxIntakeCapacity(maxIntakeCapacity);
		Node src = this.getNode(sourceName);
		Node target = this.getNode(targetName);
		int travelTime = (int) Math.ceil(Node.calculateDistance(src, target)/humanWalkingSpeed);
		edge.setTravelTime(travelTime);
		edge.setSource(src);
		edge.setTarget(target);
		
		src.getAdjacentNodes().add(target);
		target.getAdjacentNodes().add(src);
		
		EdgeCapacityAtThisTime edgeCapacityAtThisTime = new EdgeCapacityAtThisTime();
		edge.setEdgeCapacityAtSrc(new ArrayList<Integer>());
		edge.setEdgeCapacityAtTarget(new ArrayList<Integer>());

		//For each section of edge, initially(t=0) capacity is its maximum
		for (int i = 0; i < travelTime; i++)
		{
			edgeCapacityAtThisTime.getCapacityOfSection().add(maxIntakeCapacity);
			edge.getEdgeCapacityAtSrc().add(maxIntakeCapacity);
			edge.getEdgeCapacityAtTarget().add(maxIntakeCapacity);
		}
		
		edge.addEdgeCapacityAtNextTimeInstance(edgeCapacityAtThisTime);
		EdgeCapacityAtThisTime edgeMaxCapacityTime = new EdgeCapacityAtThisTime(edgeCapacityAtThisTime);
		edge.setEdgeMaxCapacityTimeInstance(edgeMaxCapacityTime);
		graph.addEdge(edge);
		
		src.addAdjacentEdge(edge);
		target.addAdjacentEdge(edge);
	}
	
	public void addEdgeToGraph(int edgeID, String edgeName, String sourceName, 
			String targetName, int maxIntakeCapacity, int travelTime)
	{
		Edge edge = new Edge();
		edge.setEdgeID(edgeID);
		edge.setEdgeName(edgeName);
		edge.setMaxIntakeCapacity(maxIntakeCapacity);
		Node src = this.getNode(sourceName);
		Node target = this.getNode(targetName);
		edge.setTravelTime(travelTime);
		edge.setSource(src);
		edge.setTarget(target);
	
		src.getArrivalTimeMap().put(target, -1);
		target.getArrivalTimeMap().put(src, -1);
		
		src.getParentArray().put(target, null );
		target.getParentArray().put(src, null);
		
		src.getAdjacentNodes().add(target);
		target.getAdjacentNodes().add(src);
		
		Map<Node,Integer> m1 = new LinkedHashMap<Node,Integer>();
		Map<Node,Integer> m2 = new LinkedHashMap<Node,Integer>();
		src.getDepartureMatrix().put(target, m1);
		target.getDepartureMatrix().put(src, m2);
		
		
		EdgeCapacityAtThisTime edgeCapacityAtThisTime = new EdgeCapacityAtThisTime();
		edge.setEdgeCapacityAtSrc(new ArrayList<Integer>());
		edge.setEdgeCapacityAtTarget(new ArrayList<Integer>());

		//For each section of edge, initially(t=0) capacity is its maximum
		for (int i = 0; i < travelTime; i++)
		{
			edgeCapacityAtThisTime.getCapacityOfSection().add(maxIntakeCapacity);
			edge.getEdgeCapacityAtSrc().add(maxIntakeCapacity);
			edge.getEdgeCapacityAtTarget().add(maxIntakeCapacity);
		}
		
		edge.addEdgeCapacityAtNextTimeInstance(edgeCapacityAtThisTime);
		EdgeCapacityAtThisTime edgeMaxCapacityTime = new EdgeCapacityAtThisTime(edgeCapacityAtThisTime);
		edge.setEdgeMaxCapacityTimeInstance(edgeMaxCapacityTime);
		graph.addEdge(edge);
		//System.out.println(src.getNodeName());
		src.addAdjacentEdge(edge);
		target.addAdjacentEdge(edge);
	}
	
	public void addEdgeToGraph(int edgeID, String edgeName, String sourceName, 
			String targetName, int maxIntakeCapacity, int travelTime, int x)
	{
		Edge edge = new Edge();
		edge.setEdgeID(edgeID);
		edge.setEdgeName(edgeName);
		edge.setMaxIntakeCapacity(maxIntakeCapacity);
		Node src = this.getNode(sourceName);
		Node target = this.getNode(targetName);
		edge.setTravelTime(travelTime);
		edge.setSource(src);
		edge.setTarget(target);
		
		src.getAdjacentNodes().add(target);
		target.getAdjacentNodes().add(src);
		
		src.getArrivalTimeMap().put(target, -1);
		target.getArrivalTimeMap().put(src, -1);
		
		src.getParentArray().put(target, null );
		target.getParentArray().put(src, null);
		
		Map<Node,Integer> m1 = new LinkedHashMap<Node,Integer>();
		Map<Node,Integer> m2 = new LinkedHashMap<Node,Integer>();
		src.getDepartureMatrix().put(target, m1);
		target.getDepartureMatrix().put(src, m2);
		
		EdgeCapacityAtThisTime edgeCapacityAtThisTime = new EdgeCapacityAtThisTime();
		edge.setEdgeCapacityAtSrc(new ArrayList<Integer>());
		edge.setEdgeCapacityAtTarget(new ArrayList<Integer>());

		//For each section of edge, initially(t=0) capacity is its maximum
		for (int i = 0; i < travelTime; i++)
		{
			edgeCapacityAtThisTime.getCapacityOfSection().add(maxIntakeCapacity);
			edge.getEdgeCapacityAtSrc().add(maxIntakeCapacity);
			edge.getEdgeCapacityAtTarget().add(maxIntakeCapacity);
		}
		
		edge.addEdgeCapacityAtNextTimeInstance(edgeCapacityAtThisTime);
		EdgeCapacityAtThisTime edgeMaxCapacityTime = new EdgeCapacityAtThisTime(edgeCapacityAtThisTime);
		edge.setEdgeMaxCapacityTimeInstance(edgeMaxCapacityTime);
		graph.addEdge(edge);
		
		src.addAdjacentEdge(edge);
		//target.addAdjacentEdge(edge);
	}
	
	public void preferenceToLongerPaths()
	{
		Comparator<Route> comp = new Comparator<Route>() {

			@Override
			public int compare(Route o1, Route o2) {
				// TODO Auto-generated method stub
				if(o1.getRouteTravelTime() > o2.getRouteTravelTime())
				{
					return -1;
				}
				return 1;
			}
		};
		Route.setTotalHops(0);
		//For each source find the shortest available path to a destination.

		PriorityQueue<Route> preRQ = new PriorityQueue<Route>(srcList.size(), comp);
		Map<Node, Route> map = new HashMap<Node, Route>();
		int totalEvacuees = 0;
		System.out.println("A1");
		for(int i=0;i<srcList.size();i++)
		{
			Node src = srcList.get(i);
			totalEvacuees += src.getInitialOccupancy();
			clearNodeData(src);
			Route r = shortestPath(src);
			preRQ.add(r);
			map.put(src, r);
		}
		System.out.println("A2");

		double totalEgressTime = 0;
		while(totalEvacuees > 0)
		{
			Route r = preRQ.poll();
			int assignedPath = findMinimumCapacity(r, map);
			//totalEgressTime = reserveAllPaths(r);
			Node src = r.getRouteNodeList().get(0);
			if(src.getCurrentOccupancy() == 0)
			{
				src.setNodeType(Node.NORMAL);
				srcList.remove(src);
			}
			totalEvacuees -= assignedPath;
			if(totalEvacuees == 0)
				break;
			
			preRQ.clear();
			map.clear();
			for(int i=0;i<srcList.size();i++)
			{
				src = srcList.get(i);
				clearNodeData(src);
				Route rr = shortestPath(src);
				preRQ.add(rr);
				map.put(src, rr);
			}
		}
		
		System.out.println("Egress Time : " + totalEgressTime);
		System.out.println("RouteList Size : " + pathList.size());
		//System.out.println("Average Evacuation Time : " + 1.0*evacuationTime/count);
		System.out.println("Avg Hops : " + 1.0*Route.getTotalHops()/pathList.size());
		System.out.println("Max Hops : " + Route.getMaxHops());
		System.out.println("No of Distinct Routes : " + this.getDistinctRoutes().size());
		int maxWaitingTimeAtANode = 0;
		String nodeName;
		double totalWaitingTime = 0;
		for(int i=0;i<graph.getNodeList().size();i++)
		{
			totalWaitingTime += graph.getNodeList().get(i).getWaitingTimeAtThisNode();
			if(graph.getNodeList().get(i).getWaitingTimeAtThisNode() > maxWaitingTimeAtANode)
			{
				maxWaitingTimeAtANode = graph.getNodeList().get(i).getWaitingTimeAtThisNode();
				nodeName = graph.getNodeList().get(i).getNodeName();
			}
		}
		System.out.println("Max. Waiting Time at a node : " + maxWaitingTimeAtANode);
		System.out.println("Average. Waiting Time at a node : " + totalWaitingTime/graph.getNodeList().size());
		//System.out.println("Count:" + count);
	}
	
	public void reservePath(Route route, int groupSize)
	{
		//PathUptoNode pathUptoNode = destination.getPathUptoPreviousNode();
		int noOfNodes = route.getRouteNodeList().size()-1;
		for(int n=0; n<noOfNodes; n++)
		{
			Node tempNode = route.getRouteNodeList().get(n);
			Edge tempEdge = route.getRouteEdgeList().get(n);
			
			int depart = route.getDepartureTime().get(n);
			int travelTime = tempEdge.getTravelTime();
			boolean sameFlowDirection = false;
			
			int arrival = route.getArrivalTime().get(n);
			
			if(n!=0)
			{
				for(int i=arrival; i<depart; i++)
				{
					int x = tempNode.getNodeCapacityAtTime().get(i) - groupSize;
					tempNode.getNodeCapacityAtTime().set(i, x);
				}
			}
			else
			{
				for(int i=depart; i< tempNode.getNodeCapacityAtTime().size();i++)
				{
					int x = tempNode.getNodeCapacityAtTime().get(i) + groupSize;
					tempNode.getNodeCapacityAtTime().set(i, x);
				}
			}
			if(tempNode == tempEdge.getSource())
				sameFlowDirection = true;
			//Edge reservation
			int newCapacity = 0;
			
			if(sameFlowDirection)
			{
				newCapacity = tempEdge.getEdgeCapacityAtSrc().get(depart) - groupSize;
				tempEdge.getEdgeCapacityAtSrc().set(depart, newCapacity);
				
				int i=(depart - travelTime + 1);
				if(i<0)
					i=0;
				for(;i< (depart + travelTime);i++)
				{
					tempEdge.getEdgeCapacityAtTarget().set(i, 0);
				}
			}
			else
			{
				newCapacity = tempEdge.getEdgeCapacityAtTarget().get(depart) - groupSize;
				tempEdge.getEdgeCapacityAtTarget().set(depart, newCapacity);
				
				int i=(depart - travelTime + 1);
				if(i<0)
					i=0;
				for(;i< (depart + travelTime);i++)
				{
					tempEdge.getEdgeCapacityAtSrc().set(i, 0);
				}
			}

			Node currNode = route.getRouteNodeList().get(n);
			currNode.setNoOfPathsThroughThisNode(currNode.getNoOfPathsThroughThisNode() + 1);
			currNode.setNoOfPeopleThroughThisNode(currNode.getNoOfPeopleThroughThisNode() + groupSize);
		}
		
		Node src = route.getRouteNodeList().get(0);
		src.setCurrentOccupancy(src.getCurrentOccupancy() - groupSize);
		
		//Add path to pathList
		this.pathList.add(route);
		
		Route.addTotalHops(route.getNodeList().size());
		if(Route.getMaxHops() < route.getNodeList().size())
			Route.setMaxHops(route.getNodeList().size());
		
		if(Route.getMaxLength() < route.getRouteTravelTime())
			Route.setMaxLength(route.getRouteTravelTime());
		distinctRoutes.add(route.getRouteString());
		this.pathId++;
	}
	
	public int findMinimumCapacity(Route route, Map<Node, Route> map)
	{
		int noOfNodes = route.getRouteNodeList().size()-1;
		int minCapacity = route.getRouteNodeList().get(0).getCurrentOccupancy();
		//without destination
		Node minCapNode = null;
		for(int n=0; n<noOfNodes; n++)
		{
			Node tempNode = route.getRouteNodeList().get(n);
			Edge tempEdge = route.getRouteEdgeList().get(n);
			int depart = route.getDepartureTime().get(n);
			int edgeCapacity = Integer.MAX_VALUE;
			int edgeTravelTime = (int)Math.ceil(tempEdge.getTravelTime());
			int nodeCapacity = Integer.MAX_VALUE;
			boolean sameFlowDirection = false;
			if(tempNode==tempEdge.getSource())
				sameFlowDirection = true;
			if(n!=0) 	//Not source
			{
				for(int i=route.getArrivalTime().get(n); i<route.getDepartureTime().get(n);i++)
				{
					if(tempNode.getNodeCapacityAtTime().get(i) < nodeCapacity)
					{
						nodeCapacity = tempNode.getNodeCapacityAtTime().get(i);
					}
					
					if(nodeCapacity <=0)
					{
						System.out.println("Node Cap less than 0 -- Error");
						System.exit(0);
					}
				}
			}
			
			int newCapacity = 0;
			if(sameFlowDirection)
			{
				newCapacity = tempEdge.getEdgeCapacityAtSrc().get(depart);
			}
			else
			{
				newCapacity = tempEdge.getEdgeCapacityAtTarget().get(depart);
			}
			
			if(newCapacity < edgeCapacity)
			{
				edgeCapacity = newCapacity;
			}
			
			if(edgeCapacity < minCapacity && edgeCapacity <= nodeCapacity)
			{
				minCapacity = edgeCapacity;
				minCapNode = null;
			}
			else if(nodeCapacity < minCapacity && nodeCapacity < edgeCapacity)
			{
				minCapacity = nodeCapacity;
				minCapNode = tempNode;
			}
		}
		if(minCapNode != null && minCapNode.getCurrentOccupancy() > 0)
		{
			//is source node and has minimum capacity
			//schedule shortest route of this node
			return findMinimumCapacity(map.get(minCapNode), map);
		}
		route.setGroupSize(minCapacity);
		reservePath(route, minCapacity);
		return minCapacity;
	}
	
	
	public boolean check(Route p, Route q)
	{
		if(p.getRouteTravelTime() != q.getRouteTravelTime())
			return false;
		
		if(p.getRouteNodeList().size() != q.getRouteNodeList().size())
			return false;
		
		for(int i=0;i<p.getRouteNodeList().size();i++)
		{
			if(p.getRouteNodeList().get(i) != q.getRouteNodeList().get(i))
				return false;
		}
		return true;
	}
	public void clearNodeData(Node src)
	{
		for(int i=0;i<graph.getNodeList().size();i++)
		{
			
			Node n = graph.getNodeList().get(i);
			n.setChildList(null);
			n.setParent(null);
			n.setPathUptoPreviousNode(null);
			n.setScanned(false);
			n.setAdjacentScannedList(null);
			if(n == src)
				n.setTravelTime(0);
			else
				n.setTravelTime(Integer.MAX_VALUE);
		}
	}
	
	public Route shortestPath(Node src)
	{
		PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>();
		Route r = new Route();
		r.setDelayAtSrc(0);
		r.getArrivalTime().add(0);
		r.getRouteNodeList().add(src);
		r.setRouteTravelTime(0);
		src.setRouteUptoNode(r);
		priorityQueue.add(src);
		Node u = src;
		while(u.getNodeType() != Node.DESTINATION)
		{
			//for all adjacent vertices of u
			boolean reset = false;
			for(int index = 0;index<u.getAdjacencies().size();index++)
			{
				Node v;
				Edge edge = u.getAdjacencies().get(index);
				if(edge.getSource() == u)
				{
					//forward along the edge
					v = edge.getTarget();
					if(!v.isScanned())
					{
						if(!simpleDijkstra1(u, v, edge, priorityQueue))
						{
							reset = true;
							break;
						}
					}
				}
				else
				{
					v = edge.getSource();
					if(!v.isScanned())
					{
						if(!simpleDijkstra2(u, v, edge, priorityQueue))
						{
							reset = true;
							break;
						}
					}
				}
			}
			if(!reset)
				u.setScanned(true);
			u = priorityQueue.poll();
		}
		//got a destination node, trace the path back up to form the route.
		return u.getRouteUptoNode();
	}
	
	public boolean simpleDijkstra1(Node u, Node v, Edge edge, PriorityQueue<Node> priorityQueue)
	{
		int departureTimeFromU = u.getMinimumArrivalTime();
		int arrivalAtV = departureTimeFromU + edge.getTravelTime();
		int timeInstancesToAddAtSrc = departureTimeFromU - edge.getEdgeCapacityAtSrc().size() + 2;
		int timeInstancesToAddAtTarget = arrivalAtV - edge.getEdgeCapacityAtTarget().size() + 2;
		
		for(int i = 0; i < timeInstancesToAddAtSrc; i++)
			edge.addEdgeCapacityAtSrc();
		
		for(int i = 0; i < timeInstancesToAddAtTarget; i++)
			edge.addEdgeCapacityAtTarget();
		
		//while the capacity in first section of this edge is not available
		//we will wait here only
		int delay = 0;
		boolean change = true;
		while(change)
		{
			change = false;
			
			while(u.getNodeCapacityAtTime().size() < (departureTimeFromU + 1))
				u.getNodeCapacityAtTime().add(u.getMaxCapacity()-u.getCurrentOccupancy());
			
			if(u.getNodeCapacityAtTime().get(departureTimeFromU) <= 0)
			{
				//I cannot wait here at this moment
				//I need to backtrack and come at a time when I can move 
				//ahead on this route
				Route pathUptoNode = u.getRouteUptoNode();
				if(u.getNodeType() == Node.SOURCE)
				{
					System.out.println("Error");
					System.exit(0);
				}
				if(pathUptoNode == null)
				{
					System.out.println("Error2");
					System.exit(0);
				}
				
				int del = pathUptoNode.getDelayAtSrc();
				Node src = pathUptoNode.getRouteNodeList().get(0);
				for(int i=pathUptoNode.getRouteNodeList().size() - 1; i>=0; i--)
				{
					Node n = pathUptoNode.getRouteNodeList().get(i);
					for(int j=0;j<n.getAdjacentNodes().size();j++)
					{
						Node adj = n.getAdjacentNodes().get(j);
						if(adj.isScanned() && !priorityQueue.contains(adj))
						{
							//add to priority queue
							//adj.setScanned(false);
							priorityQueue.add(adj);
						}
					}
					n.setScanned(false);
					n.setTravelTime(Integer.MAX_VALUE);
					n.setParent(null);
					n.setRouteUptoNode(null);
					if(priorityQueue.contains(n))
						priorityQueue.remove(n);
				}
				del++;
				src.setTravelTime(del);
				src.setParent(null);
				Route r = new Route();
				r.setDelayAtSrc(del);
				r.getArrivalTime().add(0);
				r.getRouteNodeList().add(src);
				r.setRouteTravelTime(del);
				src.setRouteUptoNode(r);
				priorityQueue.add(src);
				return false;
			}
			if(edge.getEdgeCapacityAtSrc().get(departureTimeFromU) <=0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
				
				timeInstancesToAddAtSrc = departureTimeFromU - edge.getEdgeCapacityAtSrc().size() + 2;
				timeInstancesToAddAtTarget = arrivalAtV - edge.getEdgeCapacityAtTarget().size() + 2;
				
				for(int i = 0; i < timeInstancesToAddAtSrc; i++)
					edge.addEdgeCapacityAtSrc();
				
				for(int i = 0; i < timeInstancesToAddAtTarget; i++)
					edge.addEdgeCapacityAtTarget();
				
				continue;
			}
			while(v.getNodeCapacityAtTime().size() < (arrivalAtV + 1))
				v.getNodeCapacityAtTime().add(v.getMaxCapacity()-v.getCurrentOccupancy());
			
			if(v.getNodeCapacityAtTime().get(arrivalAtV) <=0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
				continue;
			}
		}

	/*	if(arrivalAtV < v.getMinimumArrivalTime())
		{
			//becomes candidate for re-evaluation
		}
		else if(arrivalAtV < v.getArrivalTimeMap().get(u))
		{
			//just update the time of arrival
			v.getArrivalTimeMap().put(u, arrivalAtV);
		}*/
		
		if(arrivalAtV < v.getTravelTime())
		{

			if(v.getParent() != null)
			{
				//v has Parent
				v.getParent().removeChild(v);
			}
			v.setParent(u);
			u.addChild(v);

			Route pathUptoPreviousNode = null;
			if(u.getRouteUptoNode() == null)
			{
				//u is source node
				pathUptoPreviousNode = new Route();
			}
			else
			{
				pathUptoPreviousNode = Route.createCopy(u.getRouteUptoNode());
			}
			pathUptoPreviousNode.getRouteNodeList().add(v); //prev node
			pathUptoPreviousNode.getRouteEdgeList().add(edge);
			pathUptoPreviousNode.getDepartureTime().add(departureTimeFromU);
			pathUptoPreviousNode.getArrivalTime().add(arrivalAtV);
			pathUptoPreviousNode.setRouteTravelTime(arrivalAtV);
			v.setRouteUptoNode(pathUptoPreviousNode);
			v.setTravelTime(arrivalAtV);
			if(priorityQueue.contains(v))
				priorityQueue.remove(v);
			priorityQueue.add(v);
		}
		return true;
	}
	
	public boolean simpleDijkstra2(Node u, Node v, Edge edge, PriorityQueue<Node> priorityQueue)
	{
		int departureTimeFromU = u.getMinimumArrivalTime();
		int arrivalAtV = departureTimeFromU + edge.getTravelTime();
		int timeInstancesToAddAtTarget = departureTimeFromU - edge.getEdgeCapacityAtSrc().size() + 2;
		int timeInstancesToAddAtSrc = arrivalAtV - edge.getEdgeCapacityAtTarget().size() + 2;
		
		for(int i = 0; i < timeInstancesToAddAtSrc; i++)
			edge.addEdgeCapacityAtSrc();
		
		for(int i = 0; i < timeInstancesToAddAtTarget; i++)
			edge.addEdgeCapacityAtTarget();
		
		//while the capacity in first section of this edge is not available
		//we will wait here only
		int delay = 0;
		boolean change = true;
		while(change)
		{
			change = false;
			
			while(u.getNodeCapacityAtTime().size() < (departureTimeFromU + 1))
				u.getNodeCapacityAtTime().add(u.getMaxCapacity()-u.getCurrentOccupancy());
			
			if(u.getNodeCapacityAtTime().get(departureTimeFromU) <= 0)
			{
				//I cannot wait here at this moment
				//I need to backtrack and come at a time when I can move 
				//ahead on this route
				Route pathUptoNode = u.getRouteUptoNode();
				if(u.getNodeType() == Node.SOURCE)
				{
					System.out.println("Error");
					System.exit(0);
				}
				if(pathUptoNode == null)
				{
					System.out.println("Error2");
					System.exit(0);
				}
				
				int del = pathUptoNode.getDelayAtSrc();
				Node src = pathUptoNode.getRouteNodeList().get(0);
				for(int i=pathUptoNode.getRouteNodeList().size() - 1; i>=0; i--)
				{
					Node n = pathUptoNode.getRouteNodeList().get(i);
					for(int j=0;j<n.getAdjacentNodes().size();j++)
					{
						Node adj = n.getAdjacentNodes().get(j);
						if(adj.isScanned() && !priorityQueue.contains(adj))
						{
							//add to priority queue
							//adj.setScanned(false);
							priorityQueue.add(adj);
						}
					}
					n.setScanned(false);
					n.setTravelTime(Integer.MAX_VALUE);
					n.setParent(null);
					n.setRouteUptoNode(null);
					if(priorityQueue.contains(n))
						priorityQueue.remove(n);
				}
				del++;
				src.setTravelTime(del);
				src.setParent(null);
				Route r = new Route();
				r.setDelayAtSrc(del);
				r.getArrivalTime().add(0);
				r.setRouteTravelTime(del);
				r.getRouteNodeList().add(src);
				src.setRouteUptoNode(r);
				priorityQueue.add(src);
				return false;
			}
			if(edge.getEdgeCapacityAtTarget().get(departureTimeFromU) <=0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
				
				timeInstancesToAddAtTarget = departureTimeFromU - edge.getEdgeCapacityAtSrc().size() + 2;
				timeInstancesToAddAtSrc = arrivalAtV - edge.getEdgeCapacityAtTarget().size() + 2;
				
				for(int i = 0; i < timeInstancesToAddAtSrc; i++)
					edge.addEdgeCapacityAtSrc();
				
				for(int i = 0; i < timeInstancesToAddAtTarget; i++)
					edge.addEdgeCapacityAtTarget();
				
				continue;
			}
			while(v.getNodeCapacityAtTime().size() < (arrivalAtV + 1))
				v.getNodeCapacityAtTime().add(v.getMaxCapacity()-v.getCurrentOccupancy());
			
			if(v.getNodeCapacityAtTime().get(arrivalAtV) <=0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
				continue;
			}
		}
		
		if(arrivalAtV < v.getTravelTime())
		{

			if(v.getParent() != null)
			{
				//v has Parent
				v.getParent().removeChild(v);
			}
			v.setParent(u);
			u.addChild(v);

			Route pathUptoPreviousNode = null;
			if(u.getRouteUptoNode() == null)
			{
				//u is source node
				pathUptoPreviousNode = new Route();
			}
			else
			{
				pathUptoPreviousNode = Route.createCopy(u.getRouteUptoNode());
			}
			pathUptoPreviousNode.getRouteNodeList().add(v); //prev node
			pathUptoPreviousNode.getRouteEdgeList().add(edge);
			pathUptoPreviousNode.getDepartureTime().add(departureTimeFromU);
			pathUptoPreviousNode.getArrivalTime().add(arrivalAtV);
			pathUptoPreviousNode.setRouteTravelTime(arrivalAtV);
			v.setRouteUptoNode(pathUptoPreviousNode);
			v.setTravelTime(arrivalAtV);
			if(priorityQueue.contains(v))
				priorityQueue.remove(v);
			priorityQueue.add(v);
		}
		return true;
	}
	
	public Route findRoute(Node destination)
	{
		PathUptoNode pathUptoNode = destination.getPathUptoPreviousNode();
		
		Route route = new Route();
		route.setRouteId(pathId);
		route.getArrivalTime().add(0);	//Source node arrival time
		for(int index = 0; index < pathUptoNode.getNodeList().size(); index++)
		{
			Node temp = pathUptoNode.getNodeList().get(index);
			route.getNodeList().add(temp.getNodeName());
			route.getRouteNodeList().add(temp);
			if(index != 0)
				route.getArrivalTime().add((int)Math.ceil(temp.getTravelTime()));
			
			Edge tempEdge = pathUptoNode.getEdgeList().get(index);
			route.getEdgeList().add(tempEdge.getEdgeName());
			route.getRouteEdgeList().add(tempEdge);
			route.getDepartureTime().add(pathUptoNode.getDepartureTime().get(index));
		}
		route.getNodeList().add(destination.getNodeName());
		route.getRouteNodeList().add(destination);
		route.getArrivalTime().add((int)Math.ceil(destination.getTravelTime()));
		route.getDepartureTime().add(-1);  //Destination departure time
		route.setRouteTravelTime(destination.getTravelTime());
		return route;
	}
	
	public int findTimeForDepartureFromU(Node u, Node v, Edge edge, int departureTimeFromU, int arrivalAtV)
	{
		//find time at which people can go from u to v
		int delay = 0;
		boolean change = true;
		while(change)
		{
			change = false;
			
			while(u.getNodeCapacityAtTime().size() < (departureTimeFromU + 1))
				u.getNodeCapacityAtTime().add(u.getMaxCapacity()-u.getCurrentOccupancy());
			
			if(u.getNodeCapacityAtTime().get(departureTimeFromU) <= 0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
			}
			if(edge.getEdgeCapacityAtSrc().get(departureTimeFromU) <=0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
				
				int timeInstancesToAddAtSrc = departureTimeFromU - edge.getEdgeCapacityAtSrc().size() + 2;
				int timeInstancesToAddAtTarget = arrivalAtV - edge.getEdgeCapacityAtTarget().size() + 2;
				
				for(int i = 0; i < timeInstancesToAddAtSrc; i++)
					edge.addEdgeCapacityAtSrc();
				
				for(int i = 0; i < timeInstancesToAddAtTarget; i++)
					edge.addEdgeCapacityAtTarget();
				
				continue;
			}
			while(v.getNodeCapacityAtTime().size() < (arrivalAtV + 1))
				v.getNodeCapacityAtTime().add(v.getMaxCapacity()-v.getCurrentOccupancy());
			
			if(v.getNodeCapacityAtTime().get(arrivalAtV) <=0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
				continue;
			}
		}
		return departureTimeFromU;
	}
	
	public void backtrack(Node u, Node v, Edge edge, int departureTimeFromU, int arrivalAtV)
	{
		//find time at which people can go from u to v
		int delay = 0;
		boolean change = true;
		while(change)
		{
			change = false;
			
			while(u.getNodeCapacityAtTime().size() < (departureTimeFromU + 1))
				u.getNodeCapacityAtTime().add(u.getMaxCapacity()-u.getCurrentOccupancy());
			
			if(u.getNodeCapacityAtTime().get(departureTimeFromU) <= 0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
			}
			if(edge.getEdgeCapacityAtSrc().get(departureTimeFromU) <=0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
				
				int timeInstancesToAddAtSrc = departureTimeFromU - edge.getEdgeCapacityAtSrc().size() + 2;
				int timeInstancesToAddAtTarget = arrivalAtV - edge.getEdgeCapacityAtTarget().size() + 2;
				
				for(int i = 0; i < timeInstancesToAddAtSrc; i++)
					edge.addEdgeCapacityAtSrc();
				
				for(int i = 0; i < timeInstancesToAddAtTarget; i++)
					edge.addEdgeCapacityAtTarget();
				
				continue;
			}
			while(v.getNodeCapacityAtTime().size() < (arrivalAtV + 1))
				v.getNodeCapacityAtTime().add(v.getMaxCapacity()-v.getCurrentOccupancy());
			
			if(v.getNodeCapacityAtTime().get(arrivalAtV) <=0)
			{
				change=true;
				delay++;
				departureTimeFromU++;
				arrivalAtV++;
				continue;
			}
		}
	}
	public void displayNodeEdgeStats()
	{
		FileWriter writer = null;
		try
		{
		    writer = new FileWriter("/Users/MLGupta/Documents/PlusPlusNodeData101.csv");
		    writer.append("NodeName,NoOfPaths,NoOfPeople,WaitingTime\n");
			for(int i=0;i<graph.getNodeList().size();i++)
			{
				Node n = graph.getNodeList().get(i);
				String str = "";
				str+=n.getNodeName() + "," + n.getNoOfPathsThroughThisNode() + "," + n.getNoOfPeopleThroughThisNode() + "," + n.getWaitingTimeAtThisNode() + "\n";
				writer.write(str);
				//System.out.println(str);
			}
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		try
		{
		    writer = new FileWriter("/Users/MLGupta/Documents/PlusPlusRouteList101.csv");
		    writer.append("RouteList\n");
			for (Iterator<String> iterator = this.distinctRoutes.iterator(); iterator.hasNext();) {
				String route = (String) iterator.next();
				writer.write(route + "\n");
			}
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
}
