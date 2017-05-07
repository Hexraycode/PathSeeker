package project.Nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by Hexray on 21.04.2017.
 */
public abstract class AbstractNode {
    protected Color color;
    protected GraphicsContext graphicsContext;

    public AbstractNode(GraphicsContext graphicsContext, Color color) {
        this.color = color;
        this.graphicsContext = graphicsContext;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
