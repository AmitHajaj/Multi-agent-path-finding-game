package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a multi Agents Arena which move on a graph - eat as many pokemoon as possible!
 * @author boaz.benmoshe and amit.hajaj
 */
public class Arena {
	public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS=EPS2;
	private directed_weighted_graph _gg;
	private List<CL_Agent> _agents;
	private List<CL_Pokemon> _pokemons;
	private List<String> _info;
	private game_service game;
	private static Point3D MIN = new Point3D(0, 100,0);
	private static Point3D MAX = new Point3D(0, 100,0);

	/**
	 * Empty constructor
	 */
	public Arena() {;
		_info = new ArrayList<String>();
	}

	/**
	 * Constructor that get graph to set, agent's and pokemon's.
	 * @param g, graph to work on
	 * @param r, list of agent's.
	 * @param p, list of pokemon's.
	 */
	private Arena(directed_weighted_graph g, List<CL_Agent> r, List<CL_Pokemon> p) {
		_gg = g;
		this.setAgents(r);
		this.setPokemons(p);
	}

	/**
	 * set given pokemon's on the game arena.
	 * @param f, list of pokemon's.
	 */
	public void setPokemons(List<CL_Pokemon> f) {
		if(this._pokemons != null) {
			for (CL_Pokemon p : this._pokemons) {
				int i = f.indexOf(p);
				if (i != -1) {
					f.get(i).setIsEaten(p.getIsEaten());
					f.get(i).setEatenBy(p.getEatenBy());
				}
			}
		}

		this._pokemons = f;
	}

	/**
	 * set given agent's on the game arena.
	 * @param f, list of agent's.
	 */
	public void setAgents(List<CL_Agent> f) {
		if(this._agents != null){
			for(CL_Agent a : this._agents){
				int i = f.indexOf(a);
				if(i != -1){
					f.get(i).setGointTo(a.getGointTo());
					f.get(i).set_sg_dt(a.get_sg_dt());
				}
			}
		}
		this._agents = f;
	}

	/**
	 * set a given graph on the game arena to play on.
	 * @param g, directed weighted graph.
	 */
	public void setGraph(directed_weighted_graph g) {this._gg =g;}//init();}

	private void init( ) {
		MIN=null; MAX=null;
		double x0=0,x1=0,y0=0,y1=0;
		Iterator<node_data> iter = _gg.getV().iterator();
		while(iter.hasNext()) {
			geo_location c = iter.next().getLocation();
			if(MIN==null) {x0 = c.x(); y0=c.y(); x1=x0;y1=y0;MIN = new Point3D(x0,y0);}
			if(c.x() < x0) {x0=c.x();}
			if(c.y() < y0) {y0=c.y();}
			if(c.x() > x1) {x1=c.x();}
			if(c.y() > y1) {y1=c.y();}
		}
		double dx = x1-x0, dy = y1-y0;
		MIN = new Point3D(x0-dx/10,y0-dy/10);
		MAX = new Point3D(x1+dx/10,y1+dy/10);
		
	}

	/**
	 * return's the agent that currently play in this arena.
	 * @return list of agent's.
	 */
	public List<CL_Agent> getAgents() {return _agents;}

	/**
	 * return's the pokemon's that currently can be eaten in this arena.
	 * @return list of pokemon's.
	 */
	public List<CL_Pokemon> getPokemons() {return _pokemons;}

	/**
	 * return's the graph that we currently play on.
	 * @return directed weighted graph.
	 */
	public directed_weighted_graph getGraph() {
		return _gg;
	}

	/**
	 * return's information about current game played.
	 * @return list of String's.
	 */
	public List<String> get_info() {
		return _info;
	}

	/**
	 * set information about current game played.
	 * @param _info
	 */
	public void set_info(List<String> _info) {
		this._info = _info;
	}

	////////////////////////////////////////////////////

	/**
	 * return's the agent who currently playing on the arena.
	 * @param aa, Json string of the agent's.
	 * @param gg, directed weighted graph we play on.
	 * @return list of agent's.
	 */
	public static List<CL_Agent> getAgents(String aa, directed_weighted_graph gg) {
		ArrayList<CL_Agent> ans = new ArrayList<CL_Agent>();
		try {
			JSONObject ttt = new JSONObject(aa);
			JSONArray ags = ttt.getJSONArray("Agents");
			for(int i=0;i<ags.length();i++) {
				CL_Agent c = new CL_Agent(gg,0);
				c.update(ags.get(i).toString());
				ans.add(c);
			}
			//= getJSONArray("Agents");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * return's all pokemon's currently on the arena from a Json string.
	 * @param fs, Json string list of pokemon's.
	 * @return List of pokemon's.
	 */
	public static ArrayList<CL_Pokemon> json2Pokemons(String fs) {
		ArrayList<CL_Pokemon> ans = new  ArrayList<CL_Pokemon>();
		try {
			JSONObject ttt = new JSONObject(fs);
			JSONArray ags = ttt.getJSONArray("Pokemons");
			for(int i=0;i<ags.length();i++) {
				JSONObject pp = ags.getJSONObject(i);
				JSONObject pk = pp.getJSONObject("Pokemon");
				int t = pk.getInt("type");
				double v = pk.getDouble("value");
				//double s = 0;//pk.getDouble("speed");
				String p = pk.getString("pos");
				CL_Pokemon f = new CL_Pokemon(new Point3D(p), t, v, 0, null);
				ans.add(f);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		return ans;
	}

	/**
	 *check's on what edge a given pokemon is, and update the pokemon edge.
	 * @param fr, pokemon that we want to check.
	 * @param g, directed weighted graph that we work on.
	 */
	public static void updateEdge(CL_Pokemon fr, directed_weighted_graph g) {
		Iterator<node_data> itr = g.getV().iterator();
		while(itr.hasNext()) {
			node_data v = itr.next();
			Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
			while(iter.hasNext()) {
				edge_data e = iter.next();
				boolean f = isOnEdge(fr.getLocation(), e,fr.getType(), g);
				if(f) {fr.set_edge(e);}
			}
		}
	}

	/**
	 * check's if a given pokemon is on a given edge.
	 * @param p, geo_location of the pokemon we want to check.
	 * @param src, geo_location of the edge source node.
	 * @param dest, geo_location of the edge destination node.
	 * @return true if p is on the edge. false if p is not.
	 */
	public static boolean isOnEdge(geo_location p, geo_location src, geo_location dest ) {

		boolean ans = false;
		double dist = src.distance(dest);
		double d1 = src.distance(p) + p.distance(dest);
		if(dist>d1-EPS2) {ans = true;}
		return ans;
	}

	/**
	 * check's if a given pokemon is on a given edge.
	 * @param p, geo_location of the pokemon we want to check.
	 * @param s, edge source node.
	 * @param d, edge destination node.
	 * @param g, directed weighted graph we check on.
	 * @return true if p is on the edge. false if p is not.
	 */
	public static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
		geo_location src = g.getNode(s).getLocation();
		geo_location dest = g.getNode(d).getLocation();
		return isOnEdge(p,src,dest);
	}

	/**
	 * check's if a given pokemon is on a given edge.
	 * @param p, geo_location of the pokemon we want to check.
	 * @param e, the edge we want to check on.
	 * @param type, the type of the pokemon.(high->low or low->high)
	 * @param g, directed weighted graph we check on.
	 * @returntrue if p is on the edge. false if p is not.
	 */
	public static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
		int src = g.getNode(e.getSrc()).getKey();
		int dest = g.getNode(e.getDest()).getKey();
		if(type<0 && dest>src) {return false;}
		if(type>0 && src>dest) {return false;}
		return isOnEdge(p,src, dest, g);
	}

	/**
	 * when the game is playing we show it in frame.
	 * from this reason this method convert's the graph information to  x&y axis.
	 * @param g, directed weighted graph.
	 * @return a range in the frame that we can draw our graph in.
	 */
	private static Range2D GraphRange(directed_weighted_graph g) {
		Iterator<node_data> itr = g.getV().iterator();
		double x0=0,x1=0,y0=0,y1=0;
		boolean first = true;
		while(itr.hasNext()) {
			geo_location p = itr.next().getLocation();
			if(first) {
				x0=p.x(); x1=x0;
				y0=p.y(); y1=y0;
				first = false;
			}
			else {
				if(p.x()<x0) {x0=p.x();}
				if(p.x()>x1) {x1=p.x();}
				if(p.y()<y0) {y0=p.y();}
				if(p.y()>y1) {y1=p.y();}
			}
		}
		Range xr = new Range(x0,x1);
		Range yr = new Range(y0,y1);
		return new Range2D(xr,yr);
	}

	/**
	 * this method take a directed weighted graph and adapt it to a given range.
	 * @param g, directed weighted graph we work on.
	 * @param frame, frame range we work on..
	 * @return the graph range after the adaption.
	 */
	public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
		Range2D world = GraphRange(g);
		Range2Range ans = new Range2Range(world, frame);
		return ans;
	}

	/**
	 * return's the game we currently playing, with all of his info.
	 * @return game_service
	 */
	public game_service getGame() {
		return game;
	}

	/**
	 * set to this arena a game to play on it.
	 * @param game, the game we want to play on the arena.
	 */
	public void setGame(game_service game) {
		this.game = game;
	}
}
