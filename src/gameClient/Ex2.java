package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Ex2 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;
    private static long dt = 100;
    private static int level = -1;
    private static int id = -1;


    public static void main(String[] args) {
        Thread client = new Thread(new Ex2());
        if(args.length != 0){
            id = Integer.parseInt(args[0]);
            level = Integer.parseInt(args[1]);
        }
        client.start();
    }

    @Override
    public void run() {
        boolean validID = true;
        if(id == -1) {
            String idNum = JOptionPane.showInputDialog("Please enter id: ");
            while (validID) {
                if (idNum.length() != 9) {
                    idNum = JOptionPane.showInputDialog("Invalid length!\n Please enter id: ");
                } else {
                    validID = false;
                    int id = Integer.parseInt(idNum);
                }
            }
            validID = true;
        }
        if(level == -1) {
            String level_number = JOptionPane.showInputDialog("Please enter level: ");
            while (validID) {
                if (level_number.length() == 0) {
                    level_number = JOptionPane.showInputDialog("Invalid input!\n Please enter level: ");
                } else {
                    validID = false;
                    level = Integer.parseInt(level_number);
                }
            }
        }

        game_service game = Game_Server_Ex2.getServer(level);

        game.login(id);
        String g = game.getGraph();
        String pks = game.getPokemons();
        String ags = game.getAgents();
        DWGraph_DS gg = new DWGraph_DS();
        gg = gg.fromJson(g);
        init(game);
        _win.setTitle("Ex2 - OOP: (AMIT's Solution) "+game.toString());

        game.startGame();
        long startTime = game.timeToEnd();
        System.out.println(startTime/1000 + "sec");
        int ind=0;


        while(game.isRunning()) {
            moveAgants(game, gg);
            try {
                if(ind%1==0) { _win.repaint(); }
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }

    /**
     * Moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge) is chosen with a method in CL_Agent.
     * @param game, the game we currently playing.
     * @param gg, directed weighted graph that we play on.
     */
    synchronized private static void moveAgants(game_service game, directed_weighted_graph gg) {
        String lg = game.move();
        List<CL_Agent> log = Arena.getAgents(lg, gg);
        _ar.setAgents(log);
        DWGraph_Algo ga = new DWGraph_Algo();
        ga.init(gg);
        //ArrayList<OOP_Point3D> rs = new ArrayList<OOP_Point3D>();
        String fs =  game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        for(int a = 0;a<ffs.size();a++) { Arena.updateEdge(ffs.get(a),gg);}
        _ar.setPokemons(ffs);
        long arr[] = new long[log.size()];
        long minDT = Long.MAX_VALUE;

        for(int i=0;i<log.size();i++) {
            CL_Agent ag = log.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();
            if(dest==-1) {
                dest = ag.findNearPokpath(ffs, ga);
                game.chooseNextEdge(ag.getID(), dest);
                System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
            }
            ag.set_SDT(dt);

            arr[i] =ag.get_sg_dt();
        }
        for(int i=0; i < arr.length; i++){
            if(arr[i]<minDT){
                minDT = arr[i];
            }
        }
        System.out.println(minDT);
    }

    /**
    When starting the game we initialize the settings of the game,
    and put our agent's next to the most valuable pokemon's of the game.
    @param game, the game we currently playing.
     */
    private void init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        DWGraph_DS gg = new DWGraph_DS();
        gg = gg.fromJson(g);
        _ar = new Arena();
        _ar.setGraph(gg);
        _ar.setGame(game);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new MyFrame("test Ex2");
        _win.setSize(1000, 700);
        _win.panel.update(_ar);

        _win.setVisible(true);
        _win.panel.setVisible(true);
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            ArrayList<CL_Pokemon> cl_fs = Arena.json2Pokemons(game.getPokemons());
            cl_fs.sort(CL_Pokemon::compareTo);

            for(int a = 0;a<cl_fs.size();a++) { Arena.updateEdge(cl_fs.get(a),gg);}
            double maxTemp = Double.MIN_VALUE;

            for(int a = 0;a<rs || a<cl_fs.size();a++) {
                game.addAgent(cl_fs.get(a).get_edge().getSrc());
//                if(true){
//                    System.out.println("175");
//                }
            }
        }
        catch (JSONException e) {e.printStackTrace();}
    }
}
