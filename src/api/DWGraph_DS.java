package api;

import com.google.gson.*;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.*;

public class DWGraph_DS implements directed_weighted_graph {
    private int numOfEdges;
    private int MC;
    private HashMap<Integer, HashMap<Integer, edge_data>> map;
    private HashMap<Integer, node_data> nodes;

    public DWGraph_DS(){
        this.numOfEdges = 0;
        this.MC = 0;
        this.map = new HashMap<>();
        this.nodes = new HashMap<>();
    }

    /**
     * this is copy constructor which used only in the copy function in DWGraph_Algo.
     * it construct a deep copy of a given graph.
     * @param g
     */
    public DWGraph_DS(DWGraph_DS g) {
        this.numOfEdges = 0;
        this.MC = 0;
        this.map = new HashMap<>();
        this.nodes = new HashMap<>();

        //Adding each node from the given graph to the new graph.Without the neighbors.
        for(node_data n : g.getV()){
            NodeData temp = new NodeData(n);
            this.addNode(n);

            //Here we will deep copy each node neighbors.
            for(node_data ne: ((NodeData)n).Ni){
                this.connect(n.getKey(), ne.getKey(), g.getEdge(n.getKey(), ne.getKey()).getWeight());
            }
        }
    }

    @Override
    public node_data getNode(int key) {
        return this.nodes.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        return this.map.get(src).get(dest);
    }

    @Override
    public void addNode(node_data n) {
        if(n!=null || !this.map.containsValue(n)){
            this.nodes.put(n.getKey(), n);
            this.map.put(n.getKey(), ((NodeData)n).getNi());
        }
        MC++;
    }

    @Override
    public void connect(int src, int dest, double w) {
        if(this.map.containsKey(src) && this.map.containsKey(dest)) {
            if (src != dest && w >= 0) {
                //If this edge already exists, we don't touch edge size.
                if(!this.map.get(src).containsKey(dest)) {
                    edge_data ed = new EdgeData(src, dest, w);
                    this.map.get(src).put(dest, ed);
                    ((NodeData)this.nodes.get(src)).addNi(this.nodes.get(dest), ed);
                    ((NodeData)this.nodes.get(dest)).pointAtMe.add(src);
                    numOfEdges++;
                }
                else {
                    ((EdgeData) this.map.get(src).get(dest)).weight = w;
                }
                MC++;
            }
        }
    }

    @Override
    public Collection<node_data> getV() {
        return this.nodes.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        return this.map.get(node_id).values();
    }

    @Override
    public node_data removeNode(int key) {
        if (this.nodes.containsKey(key) && key >= 0) {
            node_data n = this.nodes.get(key);
            //Remove all edges with nodes who has n as neighbor.
            for(int p:((NodeData) n).pointAtMe){
                removeEdge(p, n.getKey());
            }
            //remove all edges from n to his neighbors.
            for(int p:((NodeData) n).neighbor.keySet()){
                removeEdge(n.getKey(), p);

            }
            this.map.remove(key);
            MC++;
            return this.nodes.remove(key);
        }
        else{
            return null;
        }
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        if(this.getEdge(src, dest) != null){
            numOfEdges--;
            MC++;
            ((NodeData)this.nodes.get(src)).Ni.remove(this.nodes.get(dest));
            ((NodeData)this.nodes.get(src)).neighbor.remove(dest);
            ((NodeData)this.nodes.get(dest)).pointAtMe.remove(src);
            return this.map.get(src).remove(dest);
        }
        else{
            return null;
        }
    }

    @Override
    public int nodeSize() {
        return this.nodes.size();
    }

    @Override
    public int edgeSize() {
        return this.numOfEdges;
    }

    @Override
    public int getMC() {
        return this.MC;
    }

    /**
     * This is a normal equals function for a weighted a graph.
     * return true if both graph's has the same nodes and same edges.
     * @param g
     * @return boolean
     */
    public boolean equals (Object g){
        if(!(g instanceof directed_weighted_graph)){ return false;}
        if(g == null) return false;
        DWGraph_DS g1 = (DWGraph_DS) g;
        //If they don't have same #nodes and #edges.
        if(this.nodeSize() != g1.nodeSize() && this.edgeSize() != g1.edgeSize()){
            return false;
        }

        for(int k : this.map.keySet()){
            //If g1 don't contains this key.
            if(g1.getNode(k) == null){
                return false;
            }
            //If they don't have the same #neighbors at current node.
            if(this.map.get(k).keySet().size() != ((DWGraph_DS) g1).map.get(k).keySet().size()){
                return false;
            }
            //Iterate over all of current node neighbor.
            for(int l : this.map.get(k).keySet()){
                if(g1.getEdge(k, l) == null){
                    return false;
                }
                if(this.getEdge(k, l).getWeight() != g1.getEdge(k, l).getWeight()){
                    return false;
                }
            }
        }
        return true;
    }

    public DWGraph_DS reverse() {
        DWGraph_DS reversed_graph = new DWGraph_DS();
        for (node_data n : this.getV()) {
            node_data temp = new DWGraph_DS.NodeData(n.getKey());
            reversed_graph.addNode(temp);
        }
            //Here we will deep copy each node neighbors.
        for(node_data n: this.getV()) {
            for (node_data ne : ((NodeData) n).getNeighbor()) {
                reversed_graph.connect(ne.getKey(), n.getKey(), this.getEdge(n.getKey(), ne.getKey()).getWeight());
            }
        }
        return reversed_graph;
    }

    /**
     * methos which convert a Json string to graph.
     * Note: made only for OOP course style of Json.
     * @param JsonGraph
     * @return
     */
    public DWGraph_DS fromJson (String JsonGraph) {

       GsonBuilder builder = new GsonBuilder();
       builder.registerTypeAdapter(DWGraph_DS.class, new graphJsonDeserializer2());
       Gson gson = builder.create();
       DWGraph_DS graph;
       graph = gson.fromJson(JsonGraph, DWGraph_DS.class);
       return graph;
    }


    /**
     * Inner class which imlements node_data.
     * This method represent a node in a directed weighted graph.
     */
    public static class NodeData implements node_data, Comparable<node_data>{
        private int key;
        private String info;
        private int tag;
        private double tempLength;
        private double weight;
        private transient HashMap<Integer, edge_data> neighbor;
        private transient HashSet<node_data> Ni;
        private HashSet<Integer> pointAtMe;
        private geo_location location;
        public static int index = 0;


        /**
         * constructor.
         * @param k
         */
        public NodeData (int k){
            this.key = k;
            this.info="";
            this.tag=0;
            this.tempLength=0;
            this.weight=0;
            this.location = new geoLocation();
            this.Ni = new HashSet<>();
            this.neighbor = new HashMap<>();
            this.pointAtMe = new HashSet<>();
        }

        /**
         * copy constructor
         * @param n
         */
        public NodeData(node_data n){
            this.key = n.getKey();
            this.info=n.getInfo();
            this.tag=n.getTag();
            this.tempLength=((NodeData)n).getTempLength();
            this.weight=n.getWeight();
            this.Ni = new HashSet<>();
            this.location =  n.getLocation();
            this.neighbor = new HashMap<>();
            this.pointAtMe = new HashSet<>();
        }

        @Override
        public int getKey() {
            return this.key;
        }

        /**
         * in use only for dijkstra algorithm at DWGraph_Algo
         * @param w
         */
        public void setTempLength(double w){
            this.tempLength = w;
        }
        /**
         * in use only for dijkstra algorithm at DWGraph_Algo
         * @return double
         */
        public double getTempLength(){
            return this.tempLength;
        }

        @Override
        public geo_location getLocation() {
            return this.location;
        }

        @Override
        public void setLocation(geo_location p) {
            this.location = p;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public void setWeight(double w) {
            this.weight = w;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public int getTag() {
            return this.tag;
        }

        @Override
        public void setTag(int t) {
            this.tag = t;
        }

        /**
         * add this node a neighbor with the edge between them.
         * @param ni, the neighnor node.
         * @param ed, the edgr between them.
         */
        public void addNi(node_data ni, edge_data ed){
            this.Ni.add(ni);
            this.neighbor.put(ni.getKey(), ed);

        }

        /**
         * return a map of this node neighbors.
         * Example: {neighbor node, edge between themn}
         * @return
         */
        public HashMap<Integer, edge_data> getNi(){
            return this.neighbor;
        }

        /**
         * return a set of this node neighbors.
         * @return HashSet<node_data></node_data>
         */
        public HashSet<node_data> getNeighbor(){
            return this.Ni;
        }

        @Override
        public int compareTo(node_data n) {

            if(this.getTempLength() < ((NodeData)n).getTempLength()){
                return -1;
            }
            else if(this.getTempLength() == ((NodeData)n).getTempLength()){
                return 0;
            }
            else{
                return 1;
            }
        }

        @Override
        public String toString() {
            return "[" + this.getKey() + ']';
        }
    }

    /**
     * Inner class whick implements edge_data.
     * represent an edge in a directed weighted graph.
     */
    private static class EdgeData implements edge_data {
        private double weight;
        private String info;
        private int tag;
        private int dest;
        private int src;

        /**
         * constructor
         * @param src
         * @param dest
         * @param w
         */
        public EdgeData(int src, int dest, double w) {
            this.weight = w;
            this.src = src;
            this.dest = dest;
        }

        @Override
        public int getSrc() {
            return this.src;
        }

        @Override
        public int getDest() {
            return this.dest;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public int getTag() {
            return this.tag;
        }

        @Override
        public void setTag(int t) {
            this.tag = t;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof edge_data)) {
                return false;
            }
            if (o == null) return false;
            EdgeData e = (EdgeData) o;
            //If they don't have same #nodes and #edges.
            if (this.getSrc() != e.getSrc()
                    && this.getDest() != e.getDest()
                    && this.getWeight() != e.getWeight()) {
                return false;
            }
            return true;
        }
    }

    /**
     * Inner class implements geo_location.
     * represent a geo location on the graph.
     */
    static class geoLocation implements geo_location{
        private double x, y, z;

        /**
         * Empty constructor
         */
        public geoLocation(){
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }

        /**
         * constructor
         * @param x
         * @param y
         * @param z
         */
        public geoLocation(double x, double y, double z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * copy constructor
         * @param g
         */
        public geoLocation(geo_location g){
            this.x = g.x();
            this.y = g.y();
            this.z = g.z();
        }

        @Override
        public double x() {
            return this.x;
        }

        @Override
        public double y() {
            return this.y;
        }

        @Override
        public double z() {
            return this.z;
        }

        @Override
        public double distance(geo_location g) {
            double dx = this.x - g.x();
            double dy = this.y - g.y();
            double dz = this.z - g.z();
            return Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2)+Math.pow(dz,2));
        }

        /**
         * toString method.
         * @return
         */
        public String toString(){
            return this.x+","+this.y+","+this.z;
        }
    }

    /**
     * inner class which deserialize a Json to a graph.
     */
    public static class graphJsonDeserializer implements JsonDeserializer<DWGraph_DS>
    {
        public DWGraph_DS deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            DWGraph_DS graph = new DWGraph_DS();
            JsonObject dwgraphJsonObj = jsonObject.get("map").getAsJsonObject();

            for (Map.Entry<String, JsonElement> set : dwgraphJsonObj.entrySet()) {
                int nodeKey = Integer.parseInt(set.getKey());
                JsonElement jsonValueElement = set.getValue();
                NodeData node = new NodeData(nodeKey);
                graph.addNode(node);

                for (Map.Entry<String, JsonElement> set1 : jsonValueElement.getAsJsonObject().entrySet()) {
                    int neighborKey = Integer.parseInt(set.getKey());
                    double neighborWeight = set1.getValue().getAsJsonObject().get("weight").getAsDouble();
                    graph.connect(nodeKey, neighborKey, neighborWeight);
                }
            }
            return graph;
        }
    }
    /**
     * inner class which deserialize a Json to a graph.
     * this deserializer is for the OOP course json graph's.
     */
    public static class graphJsonDeserializer2 implements JsonDeserializer<DWGraph_DS>
    {
        public DWGraph_DS deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            DWGraph_DS graph = new DWGraph_DS();

            JsonArray nodesArr = jsonObject.get("Nodes").getAsJsonArray();
            JsonArray edgesArr = jsonObject.get("Edges").getAsJsonArray();

            for (JsonElement obj : nodesArr) {
                double x, y, z;

                if(obj.getAsJsonObject().get("pos") != null) {
                    String pos = obj.getAsJsonObject().get("pos").getAsString();
                    String[] cord = pos.split(",");
                    x = Double.parseDouble(cord[0]);
                    y = Double.parseDouble(cord[1]);
                    z = Double.parseDouble(cord[2]);
                }
                else{
                    x = Math.random();
                    y = Math.random();
                    z = Math.random();
                }

                int k = obj.getAsJsonObject().get("id").getAsInt();
                NodeData curr = new NodeData(k);
                curr.setLocation(new geoLocation(x, y, z));
                graph.addNode(curr);
            }

            for(JsonElement obj : edgesArr) {
                int src = obj.getAsJsonObject().get("src").getAsInt();
                int dest = obj.getAsJsonObject().get("dest").getAsInt();
                double w = obj.getAsJsonObject().get("w").getAsDouble();

                graph.connect(src, dest, w);
            }
            return graph;
        }
    }
}
