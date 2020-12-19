package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.json.JSONException;

import java.io.*;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {
    DWGraph_DS g;

    /**
     * Empty constructor.
     */
    public DWGraph_Algo (){
        this.g = new DWGraph_DS();
    }

    @Override
    public void init(directed_weighted_graph g) {
        this.g = (DWGraph_DS) g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return this.g;
    }

    @Override
    public directed_weighted_graph copy() {
        directed_weighted_graph ans = new DWGraph_DS(this.g);
        return ans;
    }

    @Override
    public boolean isConnected() {
        if(this.g.nodeSize() == 0){return true;}

        boolean ans = true;
        for(node_data n : this.g.getV()){
            ans = DFS(n);
            //first time we get false we can say that the graph is not connected.
            if(!ans){
                break;
            }
        }
        return ans;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        if(this.g.getNode(src) == null || this.g.getNode(dest) == null){
            return -1;
        }
        if(src == dest){return 0;}

        node_data start = this.g.getNode(src);
        node_data target = this.g.getNode(dest);

        if(dijkstra(start, target) != null){
            DWGraph_DS.NodeData temp = new DWGraph_DS.NodeData(this.g.getNode(dest));
            return temp.getTempLength();
        }
        return -1;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if(this.g.getNode(src) == null || this.g.getNode(dest) == null){ return null;}
        node_data start = g.getNode(src);
        node_data target = g.getNode(dest);

        return dijkstra(start, target);
    }

    @Override
    public boolean save(String file) {
        boolean ans = false;
        ObjectOutputStream oos;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try{
            String jsonGraph = gson.toJson(this.g);
            FileOutputStream fout = new FileOutputStream(file);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(jsonGraph);
            oos.close();
            ans = true;
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return ans;
    }

    @Override
    public boolean load(String file) {
        boolean ans = false;


        try{
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(DWGraph_DS.class, new DWGraph_DS.graphJsonDeserializer());
            Gson gson = builder.create();

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            String jsonGraph = (String) ois.readObject();
            DWGraph_DS readGson = gson.fromJson(jsonGraph, DWGraph_DS.class);
            this.init(readGson);
            ois.close();
            ans = true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ans;
    }


    /**
     * This method check's if a given  directed graph is connected.(every node can reach to another)
     * This method based on DFS algo, as known.
     * It return true iff such path exists.
     *
     * @param start
     * @return boolean
     */
    private boolean DFS (node_data start){
        HashMap<node_data, Boolean> visited = new HashMap<>(this.g.nodeSize());
        //Initializing all nodes to unvisited.
        for (node_data n : this.g.getV()) {
            visited.put(n, false);
        }

        Stack<node_data> s = new Stack<>();

        s.push(start);
        visited.replace(start, true);

        while(!s.isEmpty()){
            DWGraph_DS.NodeData n = (DWGraph_DS.NodeData) s.pop();

            for(node_data neighbor : n.getNeighbor()){
                if(!visited.get(neighbor)){
                    s.push(neighbor);
                    visited.replace(neighbor,true);
                }
            }
        }
        return !(visited.containsValue(false));
    }

    /**
     * This is an implementation of Dijkstra's algorithm.
     * this algo's target is to find the shortest path between two given nodes.
     * Note: short mean's the WEIGHT of the path, not the #edges.
     * @param src
     * @param dest
     * @return List
     */
    private List<node_data> dijkstra(node_data src, node_data dest) {

        PriorityQueue<DWGraph_DS.NodeData> PQ = new PriorityQueue<DWGraph_DS.NodeData>();
        //this map represent the status of each node.
        //-1 - unvisited, 0 - visited (but not the shortest path), 1 - finished(we found the shortest path to it)
        HashMap<DWGraph_DS.NodeData, Integer> visited = new HashMap<DWGraph_DS.NodeData, Integer>();
        HashMap<DWGraph_DS.NodeData, DWGraph_DS.NodeData> parents = new HashMap<DWGraph_DS.NodeData, DWGraph_DS.NodeData>();
        LinkedList<node_data> path = new LinkedList<>();

        //Setting all distances to infinity, and all nodes to unvisited.
        for (node_data node : g.getV()) {
            ((DWGraph_DS.NodeData)node).setTempLength(Double.MAX_VALUE);
            visited.put((DWGraph_DS.NodeData) node, -1);
        }
        //Enqueue {src, 0} to PQ.
        ((DWGraph_DS.NodeData)src).setTempLength(0);
        PQ.add((DWGraph_DS.NodeData) src);
        parents.put((DWGraph_DS.NodeData) src, null);

        while (!PQ.isEmpty()) {
            DWGraph_DS.NodeData curr = PQ.remove();
            visited.put(curr, 1);
            if (curr == dest) {
                break;
            }
            for (node_data ne : curr.getNeighbor()) {
                if (visited.get(ne) != 1) {
                    if(visited.get(ne) == -1) {
                        ((DWGraph_DS.NodeData)ne).setTempLength(curr.getTempLength() + g.getEdge(curr.getKey(), ne.getKey()).getWeight());
                        PQ.add((DWGraph_DS.NodeData) ne);
                        parents.put((DWGraph_DS.NodeData) ne, curr);
                        visited.put((DWGraph_DS.NodeData) ne, 0);
                    }
                    else{// ne is visited.
                        if(curr.getTempLength() + g.getEdge(curr.getKey(), ne.getKey()).getWeight() < ((DWGraph_DS.NodeData)ne).getTempLength()){
                            //we found a shorter distance.
                            ((DWGraph_DS.NodeData)ne).setTempLength(curr.getWeight() + g.getEdge(curr.getKey(), ne.getKey()).getWeight());
                            //updating the PQ by removing and adding the node that changed.
                            parents.put((DWGraph_DS.NodeData) ne, curr);
                            PQ.remove(ne);
                            PQ.add((DWGraph_DS.NodeData) ne);
                        }
                    }
                }
            }
        }
        if(((DWGraph_DS.NodeData)dest).getTempLength() != Double.MAX_VALUE) {
            //build our path.
            node_data runner = dest;
            path.add(runner);
            while (parents.get(runner) != null) {
                path.addFirst(parents.get(runner));
                runner = parents.get(runner);
            }
            return path;
        }
        return null;
    }
}
