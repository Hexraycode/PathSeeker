package project.Nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by Hexray on 20.04.2017.
 */
public class FieldNode extends AbstractNode implements Drawable {
    private boolean isPassable;
    private int trashAmount;

    public FieldNode(GraphicsContext graphicsContext, Color color){
        super(graphicsContext, color);
    }

    public boolean isPassable() {
        return isPassable;
    }

    public void setPassable(boolean passable) {
        isPassable = passable;
    }

    public int getTrashAmount() {
        return trashAmount;
    }

    public void setTrashAmount(int trashAmount) {
        this.trashAmount = trashAmount;
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
            graphicsContext.setFill(Color.LIGHTGREEN);
        }
        else
        {
            graphicsContext.setFill(Color.DARKRED);
        }
        graphicsContext.fillRoundRect(cellSize*x + 1, cellSize*y + 1, cellSize - 1, cellSize - 1, 0, 0);
        //Draw trash amount
        if(isPassable)
        {
            graphicsContext.setFill(Color.BLUE);
            graphicsContext.fillText(String.valueOf(trashAmount), cellSize*x + 1, cellSize*(y+1) - cellSize/2.7);
        }
    }
}
