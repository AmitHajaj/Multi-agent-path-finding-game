package api;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

public class graphDeserialize implements JsonDeserializer<directed_weighted_graph>
{
    public DWGraph_DS deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        DWGraph_DS graph = new DWGraph_DS();

        JsonArray nodesArr = jsonObject.get("Nodes").getAsJsonArray();
        JsonArray edgesArr = jsonObject.get("Edges").getAsJsonArray();

        for(int i=0; i<nodesArr.size(); i++) {
            JsonObject currNode = nodesArr.get(i).getAsJsonObject();
            String pos = currNode.get("pos").getAsString();
            String[] cord = pos.split(",");
            double x = Double.parseDouble(cord[0]);
            double y = Double.parseDouble(cord[1]);
            double z = Double.parseDouble(cord[2]);

            int k = currNode.get("id").getAsInt();
            DWGraph_DS.NodeData curr = new DWGraph_DS.NodeData(k);
            curr.setLocation(new DWGraph_DS.geoLocation(x,y,z));
            graph.addNode(curr);
        }

        for(int i=0; i<edgesArr.size(); i++) {
            JsonObject currEdge = edgesArr.get(i).getAsJsonObject();
            int src = currEdge.get("src").getAsInt();
            int dest = currEdge.get("dest").getAsInt();
            int weight = currEdge.get("w").getAsInt();
            graph.connect(src, dest, weight);
        }
        return graph;
    }
}
