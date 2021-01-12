package api;

public class main {
    public static void main(String[] args) {
        DWGraph_DS graph = new DWGraph_DS();

        for (int i = 0; i < 8; i++) {
            graph.addNode(new DWGraph_DS.NodeData(i));
        }

        graph.connect(0, 1, 1);
        graph.connect(4, 0, 1);
        graph.connect(1, 4, 1);
        graph.connect(4, 5, 1);
        graph.connect(1, 2, 1);
        graph.connect(1, 5, 1);
        graph.connect(5, 6, 1);
        graph.connect(6, 5, 1);
        graph.connect(2, 6, 1);
        graph.connect(2, 3, 1);
        graph.connect(3, 2, 1);
        graph.connect(3, 7, 1);
        graph.connect(7, 3, 1);
        graph.connect(7, 6, 1);

        DWGraph_Algo ga = new DWGraph_Algo();
        ga.init(graph);

        ga.printSCCs();
    }
}
