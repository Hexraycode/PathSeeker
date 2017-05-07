package project.Fields;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Path;
import javafx.util.Pair;
import project.Nodes.AgentNode;
import project.Nodes.FieldNode;
import project.Nodes.ObjectiveNode;
import project.Nodes.PathNode;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

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

    private Queue<PathNode> priorityQueue;
    private List<PathNode> visitedNodes;

    private GraphicsContext graphicsContext;

    public PathSeekerField(GraphicsContext graphicsContext, int fieldWidth, int fieldHeight, int fieldGraphicalSize){
        //Determining of sizes
        this.graphicsContext = graphicsContext;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.fieldGraphicalSize = fieldGraphicalSize;

        this.objectiveAbsoluteX = 7;
        this.objectiveAbsoluteY = 7;
        objectiveNode = new ObjectiveNode(graphicsContext);


        //Creating of nodes
        fieldNodes = new PathNode[fieldWidth][fieldHeight];
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                fieldNodes[i][j] = new PathNode(graphicsContext, computeDistance(i,j, 7,7));
            }
        }

        //Creating of agent
        this.agentAbsoluteX = 3;
        this.agentAbsoluteY = 3;
        agentNode = new AgentNode(graphicsContext);



        priorityQueue = new PriorityQueue<>();
        visitedNodes = new ArrayList<>();

        //visitedNodes.add(fieldNodes[agentAbsoluteX][agentAbsoluteY]);

        generateField();
        generateObstacles(50);
        generateCosts();
        reDrawField();
    }

    private double computeDistance(Integer x1, Integer y1, Integer x2, Integer y2)
    {
        Double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return distance;
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
                    fieldNodes[i][j].setCost(random.nextInt(50));
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
        List<PathNode> nearPassableNodes = getNearPassableNodes();

        priorityQueue.addAll(nearPassableNodes);
        boolean newFieldFound = false;
        while (newFieldFound == false) {
            PathNode bestWayNode = priorityQueue.peek();
            priorityQueue.remove(bestWayNode);
            Pair<Integer, Integer> coordinatesForNode = getCoordinatesForNode(bestWayNode);
            //Moving agent to new node
            if (!visitedNodes.contains(fieldNodes[coordinatesForNode.getKey()][coordinatesForNode.getValue()])) {
                agentAbsoluteX = coordinatesForNode.getKey();
                agentAbsoluteY = coordinatesForNode.getValue();
                visitedNodes.add(fieldNodes[agentAbsoluteX][agentAbsoluteY]);
                newFieldFound = true;
            }
        }
        if(agentAbsoluteX == objectiveAbsoluteX && agentAbsoluteY == objectiveAbsoluteY)
            throw new RuntimeException("Победа!");
        reDrawField();
    }

    private Pair<Integer, Integer> getCoordinatesForNode(PathNode pathNode){
        for (int i = 0; i < fieldWidth; i++)
        {
            for (int j = 0; j < fieldHeight; j++)
            {
                if(fieldNodes[i][j] == pathNode)
                {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }

    private List<PathNode> getNearPassableNodes(){
        PathNode upper = fieldNodes[agentAbsoluteX][agentAbsoluteY-1];
        PathNode lower = fieldNodes[agentAbsoluteX][agentAbsoluteY+1];
        PathNode left = fieldNodes[agentAbsoluteX-1][agentAbsoluteY];
        PathNode right = fieldNodes[agentAbsoluteX+1][agentAbsoluteY];

        List<PathNode> passableNodes = new LinkedList<>();

        if(upper.isPassable())
            passableNodes.add(upper);

        if(lower.isPassable())
            passableNodes.add(lower);

        if(left.isPassable())
            passableNodes.add(left);

        if(right.isPassable())
            passableNodes.add(right);

        return passableNodes;
    }
}
