package gameClient;

import api.*;
import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class which represent an agent. In our game the agent suppose to eat as many pokemon's as possible at a given time.
 * Here we implements some method that help the algorithm of the game to be efficient.
 */
public class CL_Agent extends Thread {
	public static final double EPS = 0.0001;
	private static int _count = 0;
	private static int _seed = 3331;
	private int _id;
	//	private long _key;
	private geo_location _pos;
	private double _speed;
	private edge_data _curr_edge;
	private node_data _curr_node;
	private directed_weighted_graph _gg;
	private CL_Pokemon _curr_fruit;
	private CL_Pokemon gointTo;
	private long _sg_dt;
	private double _value;

	/**
	 * Constructor.
	 * @param g, directed weighted graph
	 * @param start_node
	 */
	public CL_Agent(directed_weighted_graph g, int start_node) {
		_gg = g;
		setMoney(0);
		this._curr_node = _gg.getNode(start_node);
		_pos = _curr_node.getLocation();
		_id = -1;
		setSpeed(0);
	}

	/**
	 * Update properties of this agent based on a given Json.
	 * The Json comes from the move operation, which returns Json graph after the move.
	 * @param json, Json format of the graph.
	 */
	public void update(String json) {
		JSONObject line;
		try {
			// "GameServer":{"graph":"A0","pokemons":3,"agents":1}}
			line = new JSONObject(json);
			JSONObject ttt = line.getJSONObject("Agent");
			int id = ttt.getInt("id");
			if (id == this.getID() || this.getID() == -1) {
				if (this.getID() == -1) {
					_id = id;
				}
				double speed = ttt.getDouble("speed");
				String p = ttt.getString("pos");
				Point3D pp = new Point3D(p);
				int src = ttt.getInt("src");
				int dest = ttt.getInt("dest");
				double value = ttt.getDouble("value");
				this._pos = pp;
				this.setCurrNode(src);
				this.setSpeed(speed);
				this.setNextNode(dest);
				this.setMoney(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//@Override
	public int getSrcNode() {
		return this._curr_node.getKey();
	}

	/**
	 * Serialize the agent to Json.
	 * @return the agent as Json format.
	 */
	public String toJSON() {
		int d = this.getNextNode();
		String ans = "{\"Agent\":{"
				+ "\"id\":" + this._id + ","
				+ "\"value\":" + this._value + ","
				+ "\"src\":" + this._curr_node.getKey() + ","
				+ "\"dest\":" + d + ","
				+ "\"speed\":" + this.getSpeed() + ","
				+ "\"pos\":\"" + _pos.toString() + "\""
				+ "}"
				+ "}";
		return ans;
	}

	/**
	 * NOT USED...
	 * @param v
	 */
	private void setMoney(double v) {
		_value = v;
	}

	/**
	 * Check if this given next node is possible at this graph.
	 * return true if edst is neighbor of src,
	 * else, return false.
	 * @param dest, this agent next node.
	 * @return boolean
	 */
	public boolean setNextNode(int dest) {
		boolean ans = false;
		int src = this._curr_node.getKey();
		this._curr_edge = _gg.getEdge(src, dest);
		if (_curr_edge != null) {
			ans = true;
		}
		return ans;
	}

	/**
	 * Set this agent current node.
	 * @param src
	 */
	public void setCurrNode(int src) {
		this._curr_node = _gg.getNode(src);
	}

	/**
	 * Method to check if this agent is now moving.
	 * return true if he is,
	 * else, return false.
	 * @return boolean
	 */
	public boolean isMoving() {
		return this._curr_edge != null;
	}

	/**
	 * Return the agent as Json.
	 * @return String
	 */
	public String toString() {
		return toJSON();
	}

	/**
	 * Normal toString method. return the agent as a string.
	 * Example: "0", "1.22,2.33,0", false(isMoving), 5(value).
	 * @return ans
	 */
	public String toString1() {
		String ans = "" + this.getID() + "," + _pos + ", " + isMoving() + "," + this.getValue();
		return ans;
	}

	/**
	 * returns the ID of this agent.
	 * @return int
	 */
	public int getID() {
		// TODO Auto-generated method stub
		return this._id;
	}

	/**
	 * return this agent geo location.
	 * @return location
	 */
	public geo_location getLocation() {
		// TODO Auto-generated method stub
		return _pos;
	}

	/**
	 * return this agent value.
	 * @return value
	 */
	public double getValue() {
		// TODO Auto-generated method stub
		return this._value;
	}

	/**
	 * if this agent is moving this method returns his  next node.
	 * else, -1.
	 * @return int
	 */
	public int getNextNode() {
		int ans;
		if (this._curr_edge == null) {
			ans = -1;
		} else {
			ans = this._curr_edge.getDest();
		}
		return ans;
	}

	/**
	 * returns this agent speed.
	 * @return
	 */
	public double getSpeed() {
		return this._speed;
	}

	/**
	 * set this agent a new speed.
	 * Note: speed must be not negative.
	 * @param v
	 */
	public void setSpeed(double v) {
		this._speed = v;
	}

	/**
	 * return this agent current fruit.
	 * @return
	 */
	public CL_Pokemon get_curr_fruit() {
		return _curr_fruit;
	}

	/**
	 * NEED TO CHECK!!
	 * @param curr_fruit
	 */
	public void set_curr_fruit(CL_Pokemon curr_fruit) {
		this._curr_fruit = curr_fruit;
	}

	/**
	 * If this agent is moving on some edge, this method returns the differential time to make a change in the game.
	 * Note: change means, arrive to new node. eat pokemon.
	 * the differential time is calculated with the agent speed, the weight of the edge, and the distance need to cover.
	 * if agent is on a node, so no calculate is performed.
	 * @param ddtt
	 */
	public void set_SDT(long ddtt) {
		long ddt = ddtt;
		if (this._curr_edge != null) {
			double w = get_curr_edge().getWeight();
			geo_location dest = _gg.getNode(get_curr_edge().getDest()).getLocation();
			geo_location src = _gg.getNode(get_curr_edge().getSrc()).getLocation();
			double de = src.distance(dest);
			double dist = _pos.distance(dest);
			if (this.get_curr_fruit() != null && this.get_curr_fruit().get_edge() == this.get_curr_edge()) {
				dist = _curr_fruit.getLocation().distance(this._pos);
			}
			double norm = dist / de;
			double dt =norm  * w/ this.getSpeed();
			ddt = (long) (100.0 * dt);
		}
		this.set_sg_dt(ddt);
	}

	/**
	 * if the agent is moving, the method returns the edge he is on.
	 * @return
	 */
	public edge_data get_curr_edge() {
		return this._curr_edge;
	}

	/**
	 * return the differential time that takes to this agent to make a change in the graph.
	 * Note: change means, eat pokemon, get to new node.
	 * @return
	 */
	public long get_sg_dt() {
		return _sg_dt;
	}

	/**
	 * see setSDT.
	 * @param _sg_dt
	 */
	public void set_sg_dt(long _sg_dt) {
		this._sg_dt = _sg_dt;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CL_Agent)) {
			return false;
		}
		if (other == null) {
			return false;
		}
		CL_Agent a = (CL_Agent) other;
		return this.getID() == a.getID();
	}

	/**
	 * choose the current target pokemon of this agent.
	 * @param p
	 */
	public void setGointTo (CL_Pokemon p ){
		this.gointTo = p;
	}

	/**
	 * return this agent current target pokemon.
	 * @return
	 */
	public CL_Pokemon getGointTo() {
		return this.gointTo;
	}

	/**
	 * This is the main game algorithm.
	 * Here we choose for this agent the closest pokemon to go to. (closest, means weight of the path)
	 * this calculation performed with the help of the dijkstra's algorith, for short path finding.
	 * also we consider the geo location to make an educated choice.
	 * @param poks, list of the current pokemon at the game
	 * @param ga, directed weighted graph algorithms.
	 * @return nextNode
	 */
	public int findNearPokpath(List<CL_Pokemon> poks, DWGraph_Algo ga) {
		CL_Pokemon chosen = poks.contains(this.gointTo) ? this.gointTo : null;
		int tempNextNode = 0;
		int nextNode;
		double minEff = Double.MAX_VALUE;
		double tempEff;
		double combinedValue = 0;
		double currDist;
		int nn;

		List<CL_Pokemon> filteredList;
		filteredList = poks.stream().filter(p -> !p.getIsEaten()).collect(Collectors.toList());

		if(this.gointTo != null && poks.contains(this.gointTo)){
			filteredList.add(this.gointTo);
		}


		for (int i = 0; i <filteredList.size(); i++) {

			nn = filteredList.get(i).get_edge().getSrc();
			combinedValue = filteredList.get(i).getValue();
			if (filteredList.get(i).get_edge().getSrc() == this.getSrcNode()) {
				nn = filteredList.get(i).get_edge().getDest();

				//If we see some consecutive edges that on each one there is a pokemon that on our opposite way.
				//In this case we run to the last edge and then run backwards.
				for(int j=0; j<poks.size(); j++) {
					if (poks.get(j).get_edge().getDest() == filteredList.get(i).get_edge().getSrc()) {
						this.setGointTo(poks.get(j));
						poks.get(j).setEatenBy(this);
						return poks.get(j).get_edge().getSrc();
					}
				}
				this.set_curr_fruit(filteredList.get(i));
				this.setGointTo(filteredList.get(i));
				this.setNextNode(nn);
				return nn;
			}


			//If there is two ot more pokemon's on the same edge, the trip will be more valuable.
			//moreover, we will update flags if there is another node at this edge.
			for (int j = 0; j< poks.size(); j++) {
				if (!poks.get(j).equals(filteredList.get(i))) {
					if (poks.get(j).get_edge().getSrc() == filteredList.get(i).get_edge().getSrc()
							&& poks.get(j).get_edge().getDest() == filteredList.get(i).get_edge().getDest()) {
						combinedValue += poks.get(j).getValue();
						if (poks.get(j).getIsEaten()) {
							filteredList.get(i).setEatenBy(this);
						}
						else {
							poks.get(j).setIsEaten(true);
						}
					}
				}
			}
			List<node_data> path = ga.shortestPath(this.getSrcNode(), nn);

			currDist = ((DWGraph_DS.NodeData) ga.getGraph().getNode(nn)).getTempLength() + filteredList.get(i).get_edge().getWeight();


			tempEff = (currDist/combinedValue) ;
			if (tempEff < minEff) {
				minEff = tempEff;
				chosen = filteredList.get(i);
				tempNextNode = path.get(1).getKey();
			}
		}

		int chosenIndex = poks.indexOf(chosen);

		if(chosenIndex == -1){
			chosenIndex =  0;
		}

		//If this agent has a target but he find a different one.
		//We need to change flags.
		if (!poks.get(chosenIndex).equals(this.gointTo)) {
			if (this.gointTo != null && poks.contains(this.gointTo)) {
				int l = poks.indexOf(this.gointTo);
				poks.get(l).setIsEaten(false);
				poks.get(l).setEatenBy(null);
			}
		}
		//Update flags.
		poks.get(chosenIndex).setIsEaten(true);
		poks.get(chosenIndex).setEatenBy(this);
		this.setGointTo(poks.get(chosenIndex));
		this.gointTo = poks.get(chosenIndex);

		nextNode = tempNextNode;
		this.setNextNode(nextNode);
		if(nextNode == 0 || nextNode == 22){
			System.out.println(this.get_sg_dt());
		}

		return nextNode;
	}

}
