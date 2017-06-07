package project.Fields;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import project.Nodes.AgentNode;
import project.Nodes.FieldNode;

import java.util.Comparator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hexray on 21.04.2017.
 */
public class AgentField {
    private FieldNode[][] fieldNodes;
    private int fieldGraphicalSize;
    private int fieldWidth;
    private int fieldHeight;

    private AgentNode agentNode;
    private int agentAbsoluteX;
    private int agentAbsoluteY;
    private int agentBounces;

    private int trashProbabilityAmount;

    private GraphicsContext graphicsContext;

    public AgentField(GraphicsContext graphicsContext, int fieldWidth, int fieldHeight, int fieldGraphicalSize){
        //Determining of sizes
        this.graphicsContext = graphicsContext;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.fieldGraphicalSize = fieldGraphicalSize;
        this.trashProbabilityAmount = 1;
        //Creating of nodes
        fieldNodes = new FieldNode[fieldWidth][fieldHeight];
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                fieldNodes[i][j] = new FieldNode(graphicsContext, Color.LIGHTGREEN);
            }
        }
        //Creating of agent
        this.agentBounces = 0;
        this.agentAbsoluteX = 3;
        this.agentAbsoluteY = 3;
        agentNode = new AgentNode(graphicsContext, Color.ORANGE);
        generateField();
        generateObstacles(50);
        reDrawField();
    }

    public int getTrashProbabilityAmount() {
        return trashProbabilityAmount;
    }

    public void setTrashProbabilityAmount(int trashProbabilityAmount) {
        this.trashProbabilityAmount = trashProbabilityAmount;
    }

    public int getFieldGraphicalSize() {
        return fieldGraphicalSize;
    }

    public void setFieldGraphicalSize(int fieldGraphicalSize) {
        this.fieldGraphicalSize = fieldGraphicalSize;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    private boolean isThereNoObstacleForAgent(AgentNode.Action agentDesiredPosition){
        boolean isDesiredPassable = false;
        switch (agentDesiredPosition)
        {
            case Up:
                isDesiredPassable = fieldNodes[agentAbsoluteX][agentAbsoluteY - 1].isPassable();
                break;
            case Left:
                isDesiredPassable = fieldNodes[agentAbsoluteX - 1][agentAbsoluteY].isPassable();
                break;
            case Down:
                isDesiredPassable = fieldNodes[agentAbsoluteX][agentAbsoluteY + 1].isPassable();
                break;
            case Right:
                isDesiredPassable = fieldNodes[agentAbsoluteX + 1][agentAbsoluteY].isPassable();
                break;
            default:
                break;
        }
        return isDesiredPassable;
    }

    private void reDrawField(){
        for (int i = 0; i < fieldWidth ; i++) {
            for (int j = 0; j < fieldHeight ; j++) {
                fieldNodes[i][j].draw(i, j, fieldGraphicalSize);
            }
        }
        agentNode.draw(agentAbsoluteX, agentAbsoluteY, fieldGraphicalSize);
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

    private void generateTrash(){
        Random random = new Random();
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                if(fieldNodes[i][j].isPassable()) {
                    if(Math.abs(random.nextInt(100)) < 5) {
                        int currentAmount = fieldNodes[i][j].getTrashAmount();
                        fieldNodes[i][j].setTrashAmount(currentAmount + trashProbabilityAmount);
                    }
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
            while ((agentAbsoluteX == x && agentAbsoluteY == y) || (fieldNodes[x][y].isPassable() == false));

            makeNonPassable(x, y);
        }
    }

    public void doOneStep() {
        generateTrash();
        AgentNode.Action agentNewPosition = agentNode.chooseNewPosition();
        boolean isNewPositionPassable = isThereNoObstacleForAgent(agentNewPosition);
        agentNode.thinkAndMove(isNewPositionPassable);
        //Move agent on field if the direction is passable
        if(isNewPositionPassable)
        {
            switch (agentNewPosition){
                case Up:
                    agentAbsoluteY--;
                    break;
                case Down:
                    agentAbsoluteY++;
                    break;
                case Right:
                    agentAbsoluteX++;
                    break;
                case Left:
                    agentAbsoluteX--;
                    break;
                default:
                    break;
            }
            fieldNodes[agentAbsoluteX][agentAbsoluteY].setTrashAmount(0);
        }
        else {
            agentBounces++;
        }
        reDrawField();
    }

    public int getAgentBounces() {
        return agentBounces;
    }

    public int getTrashAverageAmount(){
        int sum = 0;
        int passableCount = 0;
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                if(fieldNodes[i][j].isPassable()) {
                    sum += fieldNodes[i][j].getTrashAmount();
                    passableCount++;
                }
            }
        }
        sum /= passableCount;
        return sum;
    }

}
