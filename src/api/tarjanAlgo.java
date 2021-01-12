//package api;
//
//import java.util.*;
//
//public class tarjanAlgo {
//    /**
//     * number of vertices
//     **/
//    private int V;
//    /**
//     * preorder number counter
//     **/
//    private int preCount;
//    /**
//     * low number of v
//     **/
//    private HashMap<node_data, Integer> low;
//    /**
//     * to check if v is visited
//     **/
//    private HashMap<node_data, Boolean> visited;
//    /**
//     * to store given graph
//     **/
//    private DWGraph_DS graph;
//    /**
//     * to store all scc
//     **/
//    private List<List<node_data>> sccComp;
//    private Stack<node_data> stack;
//
//    /**
//     * function to get all strongly connected components
//     **/
//    public List<List<node_data>> getSCComponents(DWGraph_DS graph) {
//        V = graph.getV().size();
//        this.graph = graph;
//        low = new HashMap<>();
//        visited = new HashMap<>();
//        for(node_data node: graph.getV()){
//            visited.put(node, false);
//        }
//        stack = new Stack<>();
//        sccComp = new ArrayList<>();
//
//        for (node_data node : graph.getV())
//            if (!visited.get(node))
//                dfs(node);
//
//        return sccComp;
//    }
//
//    /**
//     * function dfs
//     **/
//    public void dfs(node_data v) {
//        low.put(v, preCount++);
//        visited.put(v, true);
//        stack.push(v);
//        int min = low.get(v);
//        DWGraph_DS.NodeData n = (DWGraph_DS.NodeData) v;
//        for (node_data w : n.getNeighbor()) {
//            if (!visited.get(w))
//                dfs(w);
//            if (low.get(w) < min)
//                min = low.get(w);
//        }
//        if (min < low.get(v)) {
//            low.put(v, min);
//            return;
//        }
//        List<node_data> component = new ArrayList<>();
//        node_data w;
//        do {
//            w = stack.pop();
//            component.add(w);
//            low.put(w, V);
//        } while (w != v);
//        sccComp.add(component);
//    }
//
//    void DFS(node_data s)
//    {
//        // Initially mark all vertices as not visited
//        HashMap<node_data, Boolean> visited = new HashMap<>();
//        DWGraph_DS.NodeData n = (DWGraph_DS.NodeData)s;
//        for (node_data node: n.getNeighbor()) {
//            visited.put(node, false);
//        }
//
//
//        // Create a stack for DFS
//        Stack<node_data> stack = new Stack<>();
//
//        // Push the current source node
//        stack.push(s);
//
//        while(stack.empty() == false)
//        {
//            // Pop a vertex from stack and print it
//            s = stack.peek();
//            stack.pop();
//
//            // Stack may contain same vertex twice. So
//            // we need to print the popped item only
//            // if it is not visited.
//            if(visited.get(s) == false)
//            {
////                System.out.print(s + " ");
//                visited.put(s, true);
//            }
//
//            // Get all adjacent vertices of the popped vertex s
//            // If a adjacent has not been visited, then push it
//            // to the stack.
//            Iterator<node_data> itr = n.getNeighbor().iterator();
//
//            while (itr.hasNext())
//            {
//                node_data v = itr.next();
//                if(!visited.get(v))
//                    stack.push(v);
//            }
//
//        }
//    }
//}
////
////    public static void main(String[] args){
////        DWGraph_DS graph = new DWGraph_DS();
////
////        for(int i=0; i<8; i++){
////            graph.addNode(new DWGraph_DS.NodeData(i));
////        }
////
////        graph.connect(0, 1, 1);
////        graph.connect(4, 0, 1);
////        graph.connect(1, 4, 1);
////        graph.connect(4, 5, 1);
////        graph.connect(1, 2, 1);
////        graph.connect(1, 5, 1);
////        graph.connect(5, 6, 1);
////        graph.connect(6, 5, 1);
////        graph.connect(2, 6, 1);
////        graph.connect(2, 3, 1);
////        graph.connect(3, 2, 1);
////        graph.connect(3, 7, 1);
////        graph.connect(7, 3, 1);
////        graph.connect(7, 6, 1);
////
////        tarjanAlgo ta = new tarjanAlgo();
////        List<List<node_data>> scComponents = ta.getSCComponents(graph);
////        System.out.println("SCC: " + scComponents);
////    }
//}
