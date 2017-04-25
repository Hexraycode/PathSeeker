package project.Nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Hexray on 20.04.2017.
 */
public class ObjectiveNode extends AbstractNode implements Drawable {
    double[] polygonXPoints;
    double[] polygonYPoints;

    public ObjectiveNode(GraphicsContext graphicsContext){
        this.graphicsContext = graphicsContext;
        polygonXPoints = new double[4];
        polygonYPoints = new double[4];
    }

    @Override
    public void draw(int x, int y, int cellSize) {
        //Draw black background to make a black border
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRoundRect(cellSize*x, cellSize*y, cellSize, cellSize, 0, 0);
        graphicsContext.setFill(Color.DARKGRAY);
        graphicsContext.fillRoundRect(cellSize*x + 1, cellSize*y + 1, cellSize - 1, cellSize - 1, 0, 0);
        //Determine the color of the agent by the last action status
        graphicsContext.setFill(Color.YELLOW);

        polygonXPoints[0] = cellSize*x + 1;
        polygonXPoints[1] = cellSize*x + 1 + cellSize/2;
        polygonXPoints[2] = cellSize*x + cellSize;
        polygonXPoints[3] = cellSize*x + 1 + cellSize/2;

        polygonYPoints[0] = cellSize*y + 1 + cellSize/2;
        polygonYPoints[1] = cellSize*y + 1;
        polygonYPoints[2] = cellSize*y + 1 + cellSize/2;
        polygonYPoints[3] = cellSize*y + cellSize;
        graphicsContext.fillPolygon(polygonXPoints, polygonYPoints, 4);
    }
}
