package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import com.google.gson.JsonObject;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarException;

/**
 * myPanel class extends JPanel and implements the GUI of the game.
 *
 */
public class MyPanel  extends JPanel {
    private int _ind;
    private Arena _ar;
    private gameClient.util.Range2Range _w2f;

    MyPanel(){


    }


    public void update(Arena ar) {
        this._ar = ar;
        updateFrame();
    }

    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-20);
        Range ry = new Range(this.getHeight()-10,150);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
    }

    /**
     * Paint component of this panel.
     * @param g
     */
    public void paint(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        updateFrame();
        drawPokemons(g);
        drawGraph(g);
        drawAgants(g);
        drawInfo(g);
        draw_gameDetails(g);
    }

    /**
     * NEED TO CHECK!
     * @param g
     */
    private void drawInfo(Graphics g) {
        java.util.List<String> str = _ar.get_info();
        String dt = "none";
        for(int i=0;i<str.size();i++) {
            g.drawString(str.get(i)+" dt: "+dt,100,60+i*20);
        }

    }

    /**
     * Draw the graph on the panel. the drawing is based on the current graph that the arena holds.
     * @param g
     */
    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while(iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n,5,g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while(itr.hasNext()) {
                edge_data e = itr.next();
                g.setColor(Color.gray);
                drawEdge(e, g);
            }
        }
    }

    /**
     * Draww the pokemon's on the graph. Based on the location they currently have.
     * @param g
     */
    private void drawPokemons(Graphics g) {
        java.util.List<CL_Pokemon> fs = _ar.getPokemons();
        if(fs!=null) {
            Iterator<CL_Pokemon> itr = fs.iterator();

            while(itr.hasNext()) {

                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r=10;
                g.setColor(Color.green);
                if(f.getType()<0) {g.setColor(Color.orange);}
                if(c!=null) {

                    geo_location fp = this._w2f.world2frame(c);
                    g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                    g.drawString(""+f.getValue(), (int)fp.x(), (int)fp.y()-4*r);

                }
            }
        }
    }

    /**
     * Draw the agent's on the graph. Based on their current location.
     * @param g
     */
    private void drawAgants(Graphics g) {
        List<CL_Agent> rs = _ar.getAgents();
        //	Iterator<OOP_Point3D> itr = rs.iterator();
        g.setColor(Color.red);
        int i=0;
        while(rs!=null && i<rs.size()) {
            CL_Agent myAgent = rs.get(i);
            geo_location c = myAgent.getLocation();
            int r=8;
            i++;
            if(c!=null) {

                geo_location fp = this._w2f.world2frame(c);
                g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                g.drawString(myAgent.getID() + " to -> " + myAgent.getGointTo() + " from "+ myAgent.getSrcNode(), (int)fp.x(), (int)fp.y()-4*r);
            }
        }
    }

    /**
     * Draw the graph nodes. this method draw the nodes one by one.
     *
     * @param n, node to draw.
     * @param r, radius of the node size.
     * @param g
     */
    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*r);
    }

    /**
     * Draw edge on the graph. This method draw the edges one by one.
     * @param e, edge to draw.
     * @param g
     */
    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());

        //draw weight:
        g.setColor(Color.gray);
        DecimalFormat df = new DecimalFormat("#.#");
        g.drawString("w: " + df.format(e.getWeight()), ((int)d0.x()+(int)s0.x())/2, ((int)d0.y()+(int)s0.y())/2);
    }

    /**
     * Draw at top left corner the details of this game now playing.
     * @param g
     */
    private void draw_gameDetails (Graphics g) {
        Font font = g.getFont().deriveFont( 20.0f );
        g.setFont( font );
        long time = _ar.getGame().timeToEnd();
        time = time/1000;
        String  timeStr = String.valueOf(time);
        g.drawString("time left: "+timeStr, 40,20);

        String gameDetails = _ar.getGame().toString();
        JSONObject jsonGame;
        try {
            jsonGame = new JSONObject(gameDetails);
            JSONObject info = jsonGame.getJSONObject("GameServer");
            g.drawString("Level: " +info.getInt("game_level"), 40, 40 );

            int numOf_pokemons = info.getInt("pokemons");
            int numOf_agents = info.getInt("agents");
            int numOf_moves = info.getInt("moves");
            int grade = info.getInt("grade");
            g.drawString("number of pokemons: " + numOf_pokemons, 40, 60);
            g.drawString("\nnumber of moves: " + numOf_moves, 40, 80);
            g.drawString("grade: " + grade, 40, 100);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
}
