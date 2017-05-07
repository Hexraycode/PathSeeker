package project.Nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 * Created by Алена on 07.05.2017.
 */
public class PathNodeForDijkstra extends PathNode {
    private Double minCost;
    private Boolean isConst;
    private PathNodeForDijkstra parentNodeForDijkstra;
    private Pair<Integer, Integer> coordinates;

    public PathNodeForDijkstra(PathNode pathNode, Pair<Integer, Integer> coordinates)
    {
        super(pathNode.getGraphicsContext(), pathNode.color, pathNode.getDistanceToTarget());
        minCost = Double.POSITIVE_INFINITY;
        isConst = false;
        this.coordinates = coordinates;
        super.setCost(pathNode.getCost());
    }

    public double getMinCost() {
        return minCost;
    }

    public void setMinCost(double minCost) {
        this.minCost = minCost;
    }

    public PathNodeForDijkstra getParentNodeForDijkstra() {
        return parentNodeForDijkstra;
    }

    public void setParentNodeForDijkstra(PathNodeForDijkstra parentNodeForDijkstra) {
        this.parentNodeForDijkstra = parentNodeForDijkstra;
    }

    public Boolean getConst() {
        return isConst;
    }

    public void setConst(Boolean aConst) {
        isConst = aConst;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Pair<Integer, Integer> coordinates) {
        this.coordinates = coordinates;
    }
}
