package project.Fields;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import project.Nodes.*;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Hexray on 21.04.2017.
 */
public class PathSeekerField implements Serializable {
    private static final long serialVersionUID = 123456789L;
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
    private Queue<PathNodeForAStar> openList;

    private List<PathNodeForAStar> closedList;

    private transient GraphicsContext graphicsContext;

    public PathSeekerField(GraphicsContext graphicsContext, int fieldWidth, int fieldHeight, int fieldGraphicalSize,
                           int agentAbsoluteX, int agentAbsoluteY, int objectiveAbsoluteX, int objectiveAbsoluteY){
        // Determining of sizes
        this.graphicsContext = graphicsContext;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.fieldGraphicalSize = fieldGraphicalSize;
        generateField(agentAbsoluteX, agentAbsoluteY, objectiveAbsoluteX, objectiveAbsoluteY);
    }

    private double computeDistance(Integer x1, Integer y1, Integer x2, Integer y2)
    {
//        Double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        Double distance = 10 * (Math.abs(x1 - x2) + Math.abs(y1 - y2))/1.0;
        return distance;
    }

    public void reDrawField(){
        for (int i = 0; i < fieldWidth ; i++) {
            for (int j = 0; j < fieldHeight ; j++) {
                fieldNodes[i][j].draw(i, j, fieldGraphicalSize);
            }
        }
        agentNode.draw(agentAbsoluteX, agentAbsoluteY, fieldGraphicalSize);
        objectiveNode.draw(objectiveAbsoluteX, objectiveAbsoluteY, fieldGraphicalSize);
    }

    public void makeNonPassable(int x, int y){
        fieldNodes[x][y].setPassable(false);
        fieldNodes[x][y].draw(x, y, fieldGraphicalSize);
    }

    public void makePassable(int x, int y){
        fieldNodes[x][y].setPassable(true);
        fieldNodes[x][y].draw(x, y, fieldGraphicalSize);
    }

    public void generateField(int agentX, int agentY, int objectiveX, int objectiveY){
        this.objectiveAbsoluteX = objectiveX;
        this.objectiveAbsoluteY = objectiveY;
        this.agentAbsoluteX = agentX;
        this.agentAbsoluteY = agentY;
        //Determining of nodes
        fieldNodes = new PathNode[fieldWidth][fieldHeight];
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                fieldNodes[i][j] = new PathNode(graphicsContext, Color.LIGHTGREEN,
                        computeDistance(i,j, this.objectiveAbsoluteX, this.objectiveAbsoluteY));
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
        generateObstacles(70);
        generateCosts();
        reDrawField();
    }

    private void generateCosts(){
        Random random = new Random();
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                if(fieldNodes[i][j].isPassable()) {
                    fieldNodes[i][j].setCost(random.nextInt(20) + 1);
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

    public void setGlobalGraphicsContext(GraphicsContext globalGraphicsContext){
        this.graphicsContext = globalGraphicsContext;
        agentNode.setGraphicsContext(globalGraphicsContext);
        objectiveNode.setGraphicsContext(globalGraphicsContext);
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldHeight; j++) {
                fieldNodes[i][j].setGraphicsContext(globalGraphicsContext);
            }
        }
    }

    public void findPathDStar(){

    }

    public void findPathAStar(){
        // Вспомогательные списки
        closedList = new ArrayList<>();
        openList = new PriorityQueue<>();
        // Добавим стартовый узел в открытый список
        PathNodeForAStar startNode = new PathNodeForAStar(fieldNodes[agentAbsoluteX][agentAbsoluteY],
                new Pair<>(agentAbsoluteX,agentAbsoluteY));
        startNode.setMinCost(0.0);
        startNode.setFunction(startNode.getDistanceToTarget());
        openList.add(startNode);

        // Подсчет итераций
        Integer iterator = 0;
        Boolean objectiveReached = false;

        while(!openList.isEmpty()){
            // Берем элемент с наименьшим значением евристики
            PathNodeForAStar currentNode = openList.peek();
            // Проверяем не достиг ли алгоритм цели
            if(currentNode.getCoordinates().getKey() == objectiveAbsoluteX &&
                    currentNode.getCoordinates().getValue() == objectiveAbsoluteY) {
                objectiveReached = true;
                System.out.println("Алгоритм A* нашел путь стоимостью " + currentNode.getMinCost() + " за "
                        + iterator + " итераций: ");
                printSolveAStar(currentNode);
                break;
            }
            openList.remove();
            closedList.add(currentNode);
            Pair<Integer, Integer> coordinates = getCoordinatesForNode(currentNode);

//            System.out.println("Итерация " + iterator +". Координаты: " + coordinates.toString() +
//                    ". Стоимость пути: " + currentNode.getMinCost() + ". До цели: " + currentNode.getDistanceToTarget()
//                    + ". Функция: " + (currentNode.getMinCost() + currentNode.getDistanceToTarget()));
            iterator++;
            fieldNodes[coordinates.getKey()][coordinates.getValue()].setColor(Color.GREEN);

            // Для каждой соседней клетки
            List<PathNode> nearPassableNodes = getNearPassableNodes(coordinates);
            for (PathNode nearNode : nearPassableNodes) {
                // Если клетка не состоит в закрытом списке
                Pair<Integer, Integer> nearCoordinates = getCoordinatesForNode(nearNode);
                if (!closedList.stream().filter(d -> d.getCoordinates().equals(getCoordinatesForNode(nearNode)))
                        .findFirst().isPresent()){
                    // Составим новую минимальную стоимость
                    Double tempMinCost = currentNode.getMinCost() + nearNode.getCost();
                    Optional<PathNodeForAStar> optNodeFromOpenList = openList.stream().filter(d -> d.getCoordinates()
                            .equals(getCoordinatesForNode(nearNode))).findFirst();
                    PathNodeForAStar newNode;

                    if(!optNodeFromOpenList.isPresent()){
                        newNode = new PathNodeForAStar(nearNode, nearCoordinates);
                        newNode.setParentNodeForAStar(currentNode);
                        newNode.setMinCost(tempMinCost);
                        newNode.setFunction(tempMinCost + newNode.getDistanceToTarget());
                        openList.add(newNode);
                        fieldNodes[nearCoordinates.getKey()][nearCoordinates.getValue()].setColor(Color.LIGHTPINK);
                    }
                    else if (tempMinCost < optNodeFromOpenList.get().getMinCost()){
                        newNode = optNodeFromOpenList.get();
                        newNode.setParentNodeForAStar(currentNode);
                        newNode.setMinCost(tempMinCost);
                        newNode.setFunction(tempMinCost + newNode.getDistanceToTarget());
                    }

                }
            }
        }
        if (!objectiveReached)
            System.out.println("Цель недостижима для алгоритма A*");
        reDrawField();
    }

    private void printSolveAStar(PathNodeForAStar node) {
        if (node.getParentNodeForAStar() != null){
            printSolveAStar(node.getParentNodeForAStar());
            fieldNodes[node.getCoordinates().getKey()][node.getCoordinates().getValue()].setColor(Color.RED);
//            System.out.println("Клетка с координатами X=" + node.getCoordinates().getKey() + " Y=" +
//                    + node.getCoordinates().getValue() + " (Стоимость прохода клетки:" + node.getCost()
//                    + " Текущая стоимость: " + node.getMinCost() + ")");
        }
    }

    public void findPathDijkstra() {
        listDijkstra = new ArrayList<>();
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
            currentNode.setMinCost(0.0);
            Integer iterator = 1;
            // Пока не достигнем цели повторяем Шаг 2 и 3
            Boolean objectiveReached = false;
            while (!objectiveReached){
                // Шаг 2: берем вершину, которая получила постоянную метку на последней итерации
                // и рассматриваем смежные для нее вершины
                Pair<Integer,Integer> coord = getCoordinatesForNode(currentNode);

                fieldNodes[coord.getKey()][coord.getValue()].setColor(Color.YELLOW);
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
                    else System.out.println("Заданный элемент не найден в списке");
                }

//                System.out.println("Итерация " + iterator +". Координаты: " + coord.toString() +
//                        ". Стоимость пути: " + currentNode.getMinCost());
                // Шаг 3: Выбираем со всего множества вершин с непостоянными метками ту вершину, у которой минимальная
                // стоимость достижения (из listDijkstra элемент c min minCost, у которого isConst = false)
                iterator++;
                currentNode = listDijkstra.stream()
                        .filter(x -> x.getConst() == false)
                        .min(Comparator.comparingDouble(PathNodeForDijkstra::getMinCost))
                        .get();
                currentNode.setConst(true);

                // Шаг 4: Проверяем достигнута ли цель
                if(currentNode.getCoordinates().getKey() == objectiveAbsoluteX &&
                        currentNode.getCoordinates().getValue() == objectiveAbsoluteY) {
                    objectiveReached = true;
//                    System.out.println("Итерация " + iterator +". Координаты: " + objectiveAbsoluteX
//                            + " " + objectiveAbsoluteY + ". Стоимость пути: " + currentNode.getMinCost());
                    iterator++;
                }

            }
            System.out.println("Алгоритм Дейкстры нашел кратчайший путь стоимостью " + currentNode.getMinCost() + " за "
                    + iterator + " итераций: ");
            // Шаг 5: Строим маршрут
            printSolveDijkstra(currentNode);
        }
        else System.out.println("Стартовый элемент не найден в списке");
        reDrawField();
    }

    private void printSolveDijkstra(PathNodeForDijkstra node) {
        if (node.getParentNodeForDijkstra() != null){
            printSolveDijkstra(node.getParentNodeForDijkstra());
            fieldNodes[node.getCoordinates().getKey()][node.getCoordinates().getValue()].setColor(Color.ORANGE);
//            System.out.println("Клетка с координатами X=" + node.getCoordinates().getKey() + " Y=" +
//                    + node.getCoordinates().getValue() + " (Стоимость прохода клетки:" + node.getCost()
//                    + " Текущая стоимость: " + node.getMinCost() + ")");
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

    private Pair<Integer, Integer> getCoordinatesForNode(PathNodeForAStar pathNode){
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

    public int getFieldGraphicalSize() {
        return fieldGraphicalSize;
    }

    public void setFieldGraphicalSize(int fieldGraphicalSize) {
        this.fieldGraphicalSize = fieldGraphicalSize;
    }

    public boolean isPassable(int x, int y){
        if((agentAbsoluteX == x && agentAbsoluteY == y) || (objectiveAbsoluteX == x && objectiveAbsoluteY == y))
        {
            return false;
        }
        return fieldNodes[x][y].isPassable();
    }

    public int getAgentAbsoluteX() {
        return agentAbsoluteX;
    }

    public void setAgentAbsoluteX(int agentAbsoluteX) {
        this.agentAbsoluteX = agentAbsoluteX;
    }

    public int getAgentAbsoluteY() {
        return agentAbsoluteY;
    }

    public void setAgentAbsoluteY(int agentAbsoluteY) {
        this.agentAbsoluteY = agentAbsoluteY;
    }

    public int getObjectiveAbsoluteX() {
        return objectiveAbsoluteX;
    }

    public void setObjectiveAbsoluteX(int objectiveAbsoluteX) {
        this.objectiveAbsoluteX = objectiveAbsoluteX;
    }

    public int getObjectiveAbsoluteY() {
        return objectiveAbsoluteY;
    }

    public void setObjectiveAbsoluteY(int objectiveAbsoluteY) {
        this.objectiveAbsoluteY = objectiveAbsoluteY;
    }

    public int getCostAmount(int x, int y){
        return fieldNodes[x][y].getCost();
    }

    public void setCostAmount(int x, int y, int cost){
        fieldNodes[x][y].setCost(cost);
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

    public PathNode[][] getFieldNodes() {
        return fieldNodes;
    }

    public void setFieldNodes(PathNode[][] fieldNodes) {
        this.fieldNodes = fieldNodes;
    }

    public void recomputeDistance() {
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                fieldNodes[i][j].setDistanceToTarget(computeDistance(i,j, this.objectiveAbsoluteX, this.objectiveAbsoluteY));
            }
        }
    }

    public void clearField() {
        for (int i = 0; i < fieldWidth; i++){
            for (int j = 0; j < fieldHeight; j++) {
                if(fieldNodes[i][j].isPassable()){
                    fieldNodes[i][j].setColor(Color.LIGHTGREEN);
                }
            }
        }
        reDrawField();
    }
}
