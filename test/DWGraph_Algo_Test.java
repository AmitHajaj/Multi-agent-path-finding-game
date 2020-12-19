package Tests_for_api;
import api.*;
import org.json.JSONException;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DWGraph_Algo_Test {

    @Test
    void copy(){

    }

    @Test
    void isConnected(){
        directed_weighted_graph g = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();

        //Empty graph is connected.
        ga.init(g);
        assertTrue(ga.isConnected());

        //1 node graph is connected.
        g.addNode(new DWGraph_DS.NodeData(2));
        ga.init(g);
        assertTrue(ga.isConnected());

        //2 nodes graph without any edge is not connected.
        g.addNode(new DWGraph_DS.NodeData(1));
        ga.init(g);
        assertFalse(ga.isConnected());

        //create a small connected graph.
        directed_weighted_graph g1 = smallDWG();
        ga.init(g1);
        assertTrue(ga.isConnected());

        g1.removeEdge(2, 3);
        ga.init(g1);
        assertFalse(ga.isConnected());

        g1.connect(2, 4, 3);
        ga.init(g1);
        assertTrue(ga.isConnected());

        g1.removeNode(9);
        ga.init(g1);
        assertTrue(ga.isConnected());
    }

    @Test
    void shortestPath_shortestPathDist(){
        directed_weighted_graph g = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);

        //Null graph
        assertEquals(-1, ga.shortestPathDist(0,1));
        assertNull(ga.shortestPath(0, 1));

        g.addNode(new DWGraph_DS.NodeData(2));
        ga.init(g);

        //Node to itself
        assertEquals(0, ga.shortestPathDist(2,2));
        List<node_data> p3 = ga.shortestPath(2, 2);
        int [] expect = {2};
        assertEquals(expect[0], p3.get(0).getKey());

        g.addNode(new DWGraph_DS.NodeData(1));
        ga.init(g);

        assertEquals(-1, ga.shortestPathDist(2,1));
        assertNull(ga.shortestPath(2, 1));

        //Create a connected small weighted graph.
        directed_weighted_graph g1 = smallDWG();
        ga.init(g1);

        assertEquals(2, ga.shortestPathDist(1, 2));
        List<node_data> p0 = ga.shortestPath(1, 2);
        expect = new int[]{1, 2};
        int i = 0;
        for(node_data n: p0) {
            assertEquals(n.getKey(), expect[i]);
            i++;
        }

        assertEquals(10, ga.shortestPathDist(0, 4));
        List<node_data> p1 = ga.shortestPath(0, 4);
        expect = new int[]{0, 1, 2, 3, 4};
        i = 0;
        for(node_data n: p1) {
            assertEquals(n.getKey(), expect[i]);
            i++;
        }

        g1.removeEdge(2, 3);
        ga.init(g1);
        assertEquals(-1, ga.shortestPathDist(0, 4));
        g1.connect(2, 4, 3);
        ga.init(g1);

        assertEquals(6, ga.shortestPathDist(0, 4));
        List<node_data> p2 = ga.shortestPath(0, 4);
        expect = new int[]{0, 1, 2, 4};
        i = 0;
        for(node_data n: p2) {
            assertEquals(n.getKey(), expect[i]);
            i++;
        }
    }

    @Test
    void save_load() throws JSONException {
        directed_weighted_graph g = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();

        ga.save("Test_graph.obj");

        g = smallDWG();
        ga.init(g);

        ga.save("Test_graph1.obj");

        directed_weighted_graph gc = smallDWG();
        ga.load("Test_graph1.obj");
        assertEquals(g, gc);
        g.removeEdge(8, 9);
        assertNotEquals(g, gc);
        gc.removeEdge(8, 9);
        assertEquals(g, gc);
    }

    /**
     * Private method that create a size 10 connected weighted graph.
     * @return g
     */
    private DWGraph_DS smallDWG(){
        DWGraph_DS g = new DWGraph_DS();

        g.addNode(new DWGraph_DS.NodeData(0));
        for(int i=0; i<9; i++){
            g.addNode(new DWGraph_DS.NodeData(i+1));

            g.connect(i, i+1, i+1);
            g.connect(i+1, i, i);
        }
        return g;
    }
}
