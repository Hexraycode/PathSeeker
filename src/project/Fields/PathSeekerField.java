package project.Fields;

import javafx.scene.canvas.GraphicsContext;
import project.Nodes.AgentNode;
import project.Nodes.FieldNode;
import project.Nodes.ObjectiveNode;
import project.Nodes.PathNode;

import java.util.Random;

/**
 * Created by Hexray on 21.04.2017.
 */
public class PathSeekerField {
    private PathNode[][] fieldNodes;
    private int fieldGraphicalSize;
    private int fieldWidth;
    private int fieldHeight;

    private AgentNode agentNode;
    private int agentAbsoluteX;
    private int agentAbsoluteY;

    private ObjectiveNode objectiveNode;
    private int objectiveAbsoluteX;
    private int objectiveAbsoluteY;

    private GraphicsContext graphicsContext;

    public PathSeekerField(GraphicsContext graphicsContext, int fieldWidth, int fieldHeight, int fieldGraphicalSize){
        //Determining of sizes
        this.graphicsContext = graphicsContext;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.fieldGraphicalSize = fieldGraphicalSize;
        //Creating of nodes
        fieldNodes = new PathNode[fieldWidth][fieldHeight];
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                fieldNodes[i][j] = new PathNode(graphicsContext);
            }
        }
        //Creating of agent
        this.agentAbsoluteX = 3;
        this.agentAbsoluteY = 3;
        agentNode = new AgentNode(graphicsContext);

        this.objectiveAbsoluteX = 7;
        this.objectiveAbsoluteY = 7;
        objectiveNode = new ObjectiveNode(graphicsContext);

        generateField();
        generateObstacles(50);
        generateCosts();
        reDrawField();
    }

    private void reDrawField(){
        for (int i = 0; i < fieldWidth ; i++) {
            for (int j = 0; j < fieldHeight ; j++) {
                fieldNodes[i][j].draw(i, j, fieldGraphicalSize);
            }
        }
        agentNode.draw(agentAbsoluteX, agentAbsoluteY, fieldGraphicalSize);
        objectiveNode.draw(objectiveAbsoluteX, objectiveAbsoluteY, fieldGraphicalSize);
    }

    private void makeNonPassable(int x, int y){
        fieldNodes[x][y].setPassable(false);
        fieldNodes[x][y].draw(x, y, fieldGraphicalSize);
    }

    private void makePassable(int x, int y){
        fieldNodes[x][y].setPassable(true);
        fieldNodes[x][y].draw(x, y, fieldGraphicalSize);
    }

    private void generateField(){
        //Create grass
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                makePassable(i, j);
            }
        }
        //Create mountains
        for (int i = 0; i < fieldWidth; i++) {
            makeNonPassable(i, 0);
            makeNonPassable(i, fieldHeight - 1);
        }
        for (int i = 0; i < fieldHeight; i++) {
            makeNonPassable(0, i);
            makeNonPassable(fieldWidth - 1, i);
        }
    }

    private void generateCosts(){
        Random random = new Random();
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                if(fieldNodes[i][j].isPassable()) {
                    fieldNodes[i][j].setCost(random.nextInt(100));
                }
            }
        }
    }

    private void generateObstacles(int count){
        Random random = new Random();
        for (int i = 0; i < count; i++){
            int x = 0;
            int y = 0;
            do {
                x = 1 + Math.abs(random.nextInt(fieldWidth - 2));
                y = 1 + Math.abs(random.nextInt(fieldHeight - 2));
            }
            while ((agentAbsoluteX == x && agentAbsoluteY == y) || (fieldNodes[x][y].isPassable() == false)
                    || (objectiveAbsoluteX == x && objectiveAbsoluteY == y));

            makeNonPassable(x, y);
        }
    }

    public void findPath() {

    }
}
