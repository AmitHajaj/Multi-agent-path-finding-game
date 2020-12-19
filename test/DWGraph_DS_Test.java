package Tests_for_api;
import api.DWGraph_DS;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DWGraph_DS_Test {

    @Test
    void getEdge (){
        DWGraph_DS g = new DWGraph_DS();

        for(int i=0; i<10; i++){
            DWGraph_DS.NodeData n = new DWGraph_DS.NodeData(i);
            g.addNode(n);
        }

        for(int i=0; i<8; i++){
            g.connect(i, i+1, i+1);
            g.connect(i+1, i+2, i);
        }

        assertEquals(7, g.getEdge(6, 7).getWeight());
        assertTrue(g.getEdge(0, 1) != null);
        assertFalse(g.getEdge(1, 0) != null);
        assertFalse(g.getEdge(9, 8) != null);

        g.connect(9, 8, 9);
        assertTrue(g.getEdge(9, 8) != null);
    }

    @Test
    void addNode(){
        DWGraph_DS g = new DWGraph_DS();

        for(int i=0; i<10; i++){
            DWGraph_DS.NodeData n = new DWGraph_DS.NodeData(i);
            g.addNode(n);
        }

        for(int i=0; i<8; i++){
            g.connect(i, i+1, i+1);
            g.connect(i+1, i+2, i);
        }

        assertEquals(10, g.nodeSize());
        //Add another node. no problems expected.
        g.addNode(new DWGraph_DS.NodeData(10));
        assertEquals(11, g.nodeSize());
        //Add the same node. no changes expected.
        g.addNode(new DWGraph_DS.NodeData(10));
        assertEquals(11, g.nodeSize());
    }

    @Test
    void connect(){
        DWGraph_DS g = new DWGraph_DS();

        for(int i=0; i<10; i++){
            DWGraph_DS.NodeData n = new DWGraph_DS.NodeData(i);
            g.addNode(n);
        }

        for(int i=0; i<8; i++){
            g.connect(i, i+1, i+1);
            g.connect(i+1, i+2, i);
        }

        assertEquals(9, g.edgeSize());

        g.connect(0, 1, 1);
        assertEquals(9, g.edgeSize());

        g.connect(1, 0, 1);
        assertEquals(10, g.edgeSize());
        assertEquals(1, g.getEdge(1,0).getWeight());
        //Connect existing node but with different weight.
        g.connect(1,0,2);
        assertEquals(2, g.getEdge(1,0).getWeight());
    }

    @Test
    void getV(){
        DWGraph_DS g = new DWGraph_DS();

        for(int i=0; i<10; i++){
            DWGraph_DS.NodeData n = new DWGraph_DS.NodeData(i);
            g.addNode(n);
        }

        for(int i=0; i<8; i++){
            g.connect(i, i+1, i+1);
            g.connect(i+1, i+2, i);
        }
        assertEquals(10, g.getV().size());
    }

    @Test
    void getE (){
        DWGraph_DS g = new DWGraph_DS();

        for(int i=0; i<10; i++){
            DWGraph_DS.NodeData n = new DWGraph_DS.NodeData(i);
            g.addNode(n);
        }

        for(int i=0; i<8; i++){
            g.connect(i, i+1, i+1);
            g.connect(i+1, i+2, i);
        }
        assertEquals(9, g.edgeSize());

        g.connect(9, 8, 9);
        assertEquals(10, g.edgeSize());

        g.connect(9, 8, 10);
        assertEquals(10, g.edgeSize());

        g.removeEdge(0,1);
        assertEquals(9, g.edgeSize());

        g.removeEdge(1, 0);
        assertEquals(9, g.edgeSize());
    }

    @Test
    void removeNode(){
        DWGraph_DS g = new DWGraph_DS();

        for(int i=0; i<10; i++){
            DWGraph_DS.NodeData n = new DWGraph_DS.NodeData(i);
            g.addNode(n);
        }

        for(int i=0; i<8; i++){
            g.connect(i, i+1, i+1);
            g.connect(i+1, i+2, i);

            g.connect(i+1, i, i+1);
        }

        assertEquals(10, g.nodeSize());

        g.removeNode(0);
        g.removeNode(1);
        assertEquals(8, g.nodeSize());
        assertEquals(13, g.edgeSize());
        //remove node that has been removed already.
        g.removeNode(0);
        assertEquals(8, g.nodeSize());
        //remove node that not exist.
        g.removeNode(11);
        assertEquals(8, g.nodeSize());
    }

    @Test
    void removeEdge(){
        DWGraph_DS g = new DWGraph_DS();

        for(int i=0; i<10; i++){
            DWGraph_DS.NodeData n = new DWGraph_DS.NodeData(i);
            g.addNode(n);
        }

        for(int i=0; i<8; i++){
            g.connect(i, i+1, i+1);
            g.connect(i+1, i+2, i);

            g.connect(i+1, i, i+1);
        }

        assertEquals(10, g.nodeSize());
        assertEquals(17, g.edgeSize());
        //remove edge that not exist.
        g.removeEdge(9,8);
        assertEquals(17, g.edgeSize());

        g.removeEdge(8,9);
        assertEquals(16, g.edgeSize());

        g.removeEdge(2, 3);
        g.removeEdge(3, 2);
        assertEquals(14, g.edgeSize());

        g.connect(2,3, 2);
        assertEquals(15, g.edgeSize());
    }
}
