package project.Nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;

import java.util.Comparator;

/**
 * Created by Hexray on 20.04.2017.
 */
public class PathNode extends AbstractNode implements Drawable {
    private boolean isPassable;
    private int cost;
    private double distanceToTarget;

    public PathNode(GraphicsContext graphicsContext, Color color, double distanceToTarget) {
        super(graphicsContext, color);
        this.distanceToTarget = distanceToTarget;
    }

    public boolean isPassable() {
        return isPassable;
    }

    public void setPassable(boolean passable) {
        isPassable = passable;
    }

    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    public void setDistanceToTarget(double distanceToTarget) {
        this.distanceToTarget = distanceToTarget;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    @Override
    public void draw(int x, int y, int cellSize) {
        //Draw black background to make a black border
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRoundRect(cellSize*x, cellSize*y, cellSize, cellSize, 0, 0);
        //Determine the color of the node
        if(isPassable){
            graphicsContext.setFill(color);
        }
        else
        {
            graphicsContext.setFill(Color.DARKRED);
        }
        graphicsContext.fillRoundRect(cellSize*x + 1, cellSize*y + 1, cellSize - 1, cellSize - 1, 0, 0);
        //Draw cost amount
        if(isPassable)
        {
            graphicsContext.setFill(Color.BLUE);
            graphicsContext.fillText(String.valueOf(cost), cellSize*x + 1, cellSize*(y+1) - cellSize/2.7);
        }
    }

}
