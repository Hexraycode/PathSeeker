package project.Nodes;

import javafx.util.Pair;

/**
 * Created by Алена on 07.05.2017.
 */
public class PathNodeForAStar extends PathNode implements Comparable<PathNodeForAStar>{
    private Double minCost;
    private Double function;
    private PathNodeForAStar parentNodeForAStar;
    private Pair<Integer, Integer> coordinates;

    public PathNodeForAStar(PathNode pathNode, Pair<Integer, Integer> coordinates)
    {
        super(pathNode.getGraphicsContext(), pathNode.color, pathNode.getDistanceToTarget());
        minCost = Double.POSITIVE_INFINITY;
        this.coordinates = coordinates;
        super.setCost(pathNode.getCost());
    }

    public Double getMinCost() {
        return minCost;
    }

    public void setMinCost(Double minCost) {
        this.minCost = minCost;
    }

    public PathNodeForAStar getParentNodeForAStar() {
        return parentNodeForAStar;
    }

    public void setParentNodeForAStar(PathNodeForAStar parentNodeForAStar) {
        this.parentNodeForAStar = parentNodeForAStar;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Pair<Integer, Integer> coordinates) {
        this.coordinates = coordinates;
    }

    public Double getFunction() {
        return function;
    }

    public void setFunction(Double function) {
        this.function = function;
    }

    @Override
    public int compareTo(PathNodeForAStar o) {
//        Double thisWeight = this.minCost + getDistanceToTarget();
//        Double otherWeight = o.getMinCost() + o.getDistanceToTarget();
//        return thisWeight.compareTo(otherWeight);
        return function.compareTo(o.function);
    }
}
