package gameClient;
import api.edge_data;
import api.node_data;
import gameClient.util.Point3D;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ObjectOutputStream;

public class CL_Pokemon implements Comparable<CL_Pokemon>{
	private edge_data _edge;
	private double _value;
	private int _type;
	private Point3D _pos;
	private double min_dist;
	private int min_ro;
	private boolean isEaten;
	private CL_Agent eatenBy;

	/**
	 * Constructor.
	 * @param p, Point 3D
	 * @param t, type
	 * @param v, value
	 * @param s, speed
	 * @param e, edge_data
	 */
	public CL_Pokemon (Point3D p, int t, double v, double s, edge_data e) {
		_type = t;
	//	_speed = s;
		_value = v;
		set_edge(e);
		_pos = p;
		min_dist = -1;
		min_ro = -1;
		isEaten = false;
	}

	/**
	 * constructor, from Json format.
	 *
	 * @param json, Json format of list of pokemon.
	 * @return ans
	 */
	public static CL_Pokemon init_from_json(String json) {
		CL_Pokemon ans = null;
		try {
			JSONObject p = new JSONObject(json);
			int id = p.getInt("id");

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ans;
	}

	public String toString() {return "F:{v="+_value+", t="+_type+"}";}

	/**
	 * returns the edge the pokemon sit on.
	 * @return edge_data
	 */
	public edge_data get_edge() {
		return _edge;
	}

	/**
	 * Set this pokemon an edge to sit on.
	 * @param _edge, edge_data we want to set.
	 */
	public void set_edge(edge_data _edge) {
		this._edge = _edge;
	}

	/**
	 * This pokemon geo location.
	 * @return ge_location
	 */
	public Point3D getLocation() {
		return _pos;
	}

	/**
	 * return this pokemon type,
	 * 1 if src<dest
	 * -1 if dest<src
	 * @return int
	 */
	public int getType() {return _type;}

	/**
	 * reuturn this pokemon value.
	 * @return value
	 */
	public double getValue() {return _value;}

	/**
	 * Need to check
	 * @return double
	 */
	public double getMin_dist() {
		return min_dist;
	}

	/**
	 * also need to check
	 * @param mid_dist
	 */
	public void setMin_dist(double mid_dist) {
		this.min_dist = mid_dist;
	}

	/**
	 * also need to check.
	 * @return int
	 */
	public int getMin_ro() {
		return min_ro;
	}

	/**
	 * also need to check.
	 * @param min_ro
	 */
	public void setMin_ro(int min_ro) {
		this.min_ro = min_ro;
	}

	/**
	 * Set this pokemon status.
	 * set true if this is an agent target
	 * initialized to false.
	 * @param isIt, boolean
	 */
	public void setIsEaten(boolean isIt) {
		this.isEaten = isIt;
	}

	/**
	 * return this pokemon status
	 * return true if this is an agent target
	 * false if his free from agents.
	 * @return boolean
	 */
	public boolean getIsEaten() {
		return isEaten;
	}

	/**
	 * if this pokemon is an agents target, it will return the agent.
	 * else, return null.
	 * @return CL_Agent
	 */
	public CL_Agent getEatenBy() {
		return eatenBy;
	}

	/**
	 * assign this pokemon an agent which want to eat him.
	 * @param eatenBy, CL_Agent
	 */
	public void setEatenBy(CL_Agent eatenBy) {
		this.eatenBy = eatenBy;
	}

	@Override
	public boolean equals(Object other){
		if (!(other instanceof CL_Pokemon)) {
			return false;
		}
		if(other == null){return false;}
		CL_Pokemon p = (CL_Pokemon) other;
		return  this._type == (p.getType())
				&& this._value == p.getValue()
//				&& this._pos == p.getLocation()
				&&this.min_dist == p.getMin_dist()
				&& this.min_ro == p.getMin_ro();
	}

	@Override
	public int compareTo(CL_Pokemon o) {
		if(this.getValue() == o.getValue()){
			return 0;
		}
		if (this.getValue()<o.getValue()){
			return -1;
		}
		else{
			return 1;
		}
	}

}
