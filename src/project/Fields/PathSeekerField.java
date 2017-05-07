package project.Fields;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.util.Pair;
import project.Nodes.*;

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

    private List<PathNodeForDijkstra> listDijkstra;
//  private Queue<PathNode> priorityQueue;

    private List<PathNode> visitedNodes;

    private GraphicsContext graphicsContext;

    public PathSeekerField(GraphicsContext graphicsContext, int fieldWidth, int fieldHeight, int fieldGraphicalSize){
        // Determining of sizes
        this.graphicsContext = graphicsContext;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.fieldGraphicalSize = fieldGraphicalSize;
        // priorityQueue = new PriorityQueue<>();
        this.objectiveAbsoluteX = 7;
        this.objectiveAbsoluteY = 7;
        this.agentAbsoluteX = 3;
        this.agentAbsoluteY = 3;
        generateField();
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
        listDijkstra = new ArrayList<>();
        visitedNodes = new ArrayList<>();
        //Determining of nodes
        fieldNodes = new PathNode[fieldWidth][fieldHeight];
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                fieldNodes[i][j] = new PathNode(graphicsContext, Color.LIGHTGREEN, computeDistance(i,j, 7,7));
            }
        }
        //Creating of objective
        objectiveNode = new ObjectiveNode(graphicsContext, Color.YELLOW);
        //Creating of agent
        agentNode = new AgentNode(graphicsContext, Color.ORANGE);
        //Create grass
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                makePassable(i, j);
            }
        }
        //Create perimeter mountains
        for (int i = 0; i < fieldWidth; i++) {
            makeNonPassable(i, 0);
            makeNonPassable(i, fieldHeight - 1);
        }
        for (int i = 0; i < fieldHeight; i++) {
            makeNonPassable(0, i);
            makeNonPassable(fieldWidth - 1, i);
        }
        generateObstacles(20);
        generateCosts();
        reDrawField();
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

    public void findPathDStar(){

    }

    public void findPathAStar(){

        // Oleg A*
//        List<PathNode> nearPassableNodes = getNearPassableNodes();
//        priorityQueue.addAll(nearPassableNodes);
//        boolean newFieldFound = false;
//        while (newFieldFound == false) {
//            PathNode bestWayNode = priorityQueue.peek();
//            priorityQueue.remove(bestWayNode);
//            Pair<Integer, Integer> coordinatesForNode = getCoordinatesForNode(bestWayNode);
//            //Moving agent to new node
//            if (!visitedNodes.contains(fieldNodes[coordinatesForNode.getKey()][coordinatesForNode.getValue()])) {
//                agentAbsoluteX = coordinatesForNode.getKey();
//                agentAbsoluteY = coordinatesForNode.getValue();
//                visitedNodes.add(fieldNodes[agentAbsoluteX][agentAbsoluteY]);
//                newFieldFound = true;
//            }
//        }
//        if(agentAbsoluteX == objectiveAbsoluteX && agentAbsoluteY == objectiveAbsoluteY)
//            throw new RuntimeException("Победа!");
//        reDrawField();

    }

    public void findPathDijkstra() {
        // создадим список всех вершин, в которых нету преград
        for (int i = 0; i < fieldWidth; i++)
        {
            for (int j = 0; j < fieldHeight; j++)
            {
                if(fieldNodes[i][j].isPassable())
                    listDijkstra.add(new PathNodeForDijkstra(fieldNodes[i][j], getCoordinatesForNode(fieldNodes[i][j])));
            }
        }

        Pair<Integer, Integer> coordinatesForNode = new Pair<>(agentAbsoluteX, agentAbsoluteY);

        // Шаг 1: Присвоим вершине истоку постоянное значение метки L (minCost) = 0.
        Optional<PathNodeForDijkstra> pathNodeForDijkstra = listDijkstra.stream()
                .filter(d -> d.getCoordinates().equals(coordinatesForNode) )
                .findFirst();

        if(pathNodeForDijkstra.isPresent()) {
            PathNodeForDijkstra currentNode = pathNodeForDijkstra.get();
            currentNode.setConst(true);
            currentNode.setMinCost(0);
            Integer iterator = 1;
            // Пока не достигнем цели повторяем Шаг 2 и 3
            Boolean objectiveReached = false;
            while (!objectiveReached){
                // Шаг 2: берем вершину, которая получила постоянную метку на последней итерации
                // и рассматриваем смежные для нее вершины
                Pair<Integer,Integer> coord = getCoordinatesForNode(currentNode);

                fieldNodes[coord.getKey()][coord.getValue()].setColor(Color.YELLOW);
                reDrawField();
                // берем смежные достижимые вершины
                List<PathNode> nearPassableNodes = getNearPassableNodes(coord);
                // для каждой из них пересчитаем возможное уменьшение стоимости
                for (PathNode pth : nearPassableNodes) {
                    Optional<PathNodeForDijkstra> isNearPasNode = listDijkstra.stream()
                            .filter(d -> d.getCoordinates().equals(getCoordinatesForNode(pth)))
                            .findFirst();
                    if(isNearPasNode.isPresent()){
                        PathNodeForDijkstra nearNode = isNearPasNode.get();
                        // уменьшаем занчения, если это возможно
                        double newCost = nearNode.getCost() + currentNode.getMinCost();
                        if ( newCost < nearNode.getMinCost()){
                            nearNode.setParentNodeForDijkstra(currentNode);
                            nearNode.setMinCost(newCost);
                        }
                    }
                    else {
                        System.out.println("Заданный элемент не найден в списке");
                    }
                }

                // Шаг 3: Выбираем со всего множества вершин с непостоянными метками ту вершину, у которой минимальная
                // стоимость достижения (из listDijkstra элемент c min minCost, у которого isConst = false)
                currentNode = listDijkstra.stream()
                        .filter(x -> x.getConst() == false)
                        .min(Comparator.comparingDouble(PathNodeForDijkstra::getMinCost))
                        .get();
                currentNode.setConst(true);

                // Шаг 4: Проверяем достигнута ли цель
                if(currentNode.getCoordinates().getKey() == objectiveAbsoluteX &&
                        currentNode.getCoordinates().getValue() == objectiveAbsoluteY)
                    objectiveReached = true;

                System.out.println("Итерация " + iterator++ +". Координаты текущей вершины: " + coord.toString() +
                        ". Стоимость пути: " + currentNode.getMinCost());
            }
            System.out.println("Алгоритм Дейкстры нашел кратчайший путь стоимостью " + currentNode.getMinCost() + ": ");
            // Шаг 5: Строим маршрут
            printSolveDijkstra(currentNode);
        }
        else {
            System.out.println("Стартовый элемент не найден в списке");
        }
    }

    private void printSolveDijkstra(PathNodeForDijkstra node) {
        if (node.getParentNodeForDijkstra() != null){
            printSolveDijkstra(node.getParentNodeForDijkstra());
            fieldNodes[node.getCoordinates().getKey()][node.getCoordinates().getValue()].setColor(Color.ORANGE);
            reDrawField();

            System.out.println("Клетка с координатами X=" + node.getCoordinates().getKey() + " Y=" +
                    + node.getCoordinates().getValue() + " (Стоимость прохода клетки:" + node.getCost()
                    + " Текущая стоимость: " + node.getMinCost() + ")");
        }
    }

    private Pair<Integer, Integer> getCoordinatesForNode(PathNodeForDijkstra pathNode){
        for (int i = 0; i < fieldWidth; i++)
        {
            for (int j = 0; j < fieldHeight; j++)
            {
                if(i == pathNode.getCoordinates().getKey() && j == pathNode.getCoordinates().getValue())
                {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
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
        PathNode right = fieldNodes[agentAbsoluteX+1][agentAbsoluteY];
        PathNode lower = fieldNodes[agentAbsoluteX][agentAbsoluteY+1];
        PathNode left = fieldNodes[agentAbsoluteX-1][agentAbsoluteY];

        List<PathNode> passableNodes = new LinkedList<>();

        if(upper.isPassable())
            passableNodes.add(upper);
        if(right.isPassable())
            passableNodes.add(right);
        if(lower.isPassable())
            passableNodes.add(lower);
        if(left.isPassable())
            passableNodes.add(left);

        return passableNodes;
    }

    private List<PathNode> getNearPassableNodes(Pair<Integer, Integer> coord){
        PathNode upper = fieldNodes[coord.getKey()][coord.getValue()-1];
        PathNode right = fieldNodes[coord.getKey()+1][coord.getValue()];
        PathNode lower = fieldNodes[coord.getKey()][coord.getValue()+1];
        PathNode left = fieldNodes[coord.getKey()-1][coord.getValue()];

        List<PathNode> passableNodes = new LinkedList<>();

        if(upper.isPassable())
            passableNodes.add(upper);
        if(right.isPassable())
            passableNodes.add(right);
        if(lower.isPassable())
            passableNodes.add(lower);
        if(left.isPassable())
            passableNodes.add(left);

        return passableNodes;
    }

    public void regenerateField() {
        generateField();
    }
}
