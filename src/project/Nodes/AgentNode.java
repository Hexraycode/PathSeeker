package project.Nodes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.*;


/**
 * Created by Hexray on 20.04.2017.
 */
public class AgentNode extends AbstractNode implements Drawable {
    private int xRelativePosition;
    private int yRelativePosition;
    private Action chosenDirection;
    private ActionStatus lastMovingStatus;
    private Map<Pair<Integer, Integer>, Integer> environmentInfo;

    double[] polygonXPoints;
    double[] polygonYPoints;

    public AgentNode(GraphicsContext graphicsContext){
        this.graphicsContext = graphicsContext;
        polygonXPoints = new double[4];
        polygonYPoints = new double[4];
        chosenDirection = Action.Thinking;
        lastMovingStatus = ActionStatus.Successful;
        environmentInfo = new HashMap<>();

        List<Integer> list = new ArrayList<>();
        list.add(8);
        list.add(1);
        list.add(12);
        list.add(3);

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                int a = ((Integer) o1).intValue();
                int b = ((Integer) o2).intValue();
                return a < b ? 1 : a == b ? 0 : -1;
            }
        });


        Map<Pair<Integer, Integer>, Integer> filtered = new Hashtable<>();
        //Filtration
        environmentInfo.forEach((KeyPair, Value) -> {
            if(Value == 10)
            {
                filtered.put(KeyPair, Value);
            }
        });

        //Remove from hashtable
        //Duplicate
        Map<Pair<Integer, Integer>, Integer> temp = new Hashtable<>(environmentInfo);


        environmentInfo.forEach((KeyPair, Value) -> {
            if(Value == 10)
            {
                temp.remove(KeyPair);
            }
        });
        //Replace old
        environmentInfo = temp;

        //Average
        List<Integer> listForAverage = new ArrayList<>();
        environmentInfo.forEach((KeyPair, Value) -> {
            listForAverage.add(Value);
        });

        IntSummaryStatistics summaryStatistics = new IntSummaryStatistics();
        summaryStatistics = listForAverage.stream().mapToInt((x) -> x).summaryStatistics();

        summaryStatistics.getAverage();



        Collections.sort(list, (Integer p1, Integer p2) -> p1.compareTo(p2));

        list.sort(Integer::compareTo);
    }

    @Override
    public void draw(int x, int y, int cellSize) {
        //Draw black background to make a black border
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRoundRect(cellSize*x, cellSize*y, cellSize, cellSize, 0, 0);
        graphicsContext.setFill(Color.DARKGRAY);
        graphicsContext.fillRoundRect(cellSize*x + 1, cellSize*y + 1, cellSize - 1, cellSize - 1, 0, 0);
        //Determine the color of the agent by the last action status
        if(lastMovingStatus == ActionStatus.Successful)
        {
            graphicsContext.setFill(Color.ORANGE);
        }
        else
        {
            graphicsContext.setFill(Color.ORANGERED);
        }
        //Draw an agent
        switch (chosenDirection)
        {
            case Up:
                polygonXPoints[0] = cellSize*x + 1;
                polygonXPoints[1] = cellSize*x + 1 + cellSize/2;
                polygonXPoints[2] = cellSize*x + cellSize;

                polygonYPoints[0] = cellSize*y + 1 + cellSize/2;
                polygonYPoints[1] = cellSize*y + 1;
                polygonYPoints[2] = cellSize*y + 1 + cellSize/2;
                graphicsContext.fillPolygon(polygonXPoints, polygonYPoints, 3);
                break;
            case Down:
                polygonXPoints[0] = cellSize*x + 1;
                polygonXPoints[1] = cellSize*x + 1 + cellSize/2;
                polygonXPoints[2] = cellSize*x + cellSize;

                polygonYPoints[0] = cellSize*y + 1 + cellSize/2;
                polygonYPoints[1] = cellSize*y + cellSize;
                polygonYPoints[2] = cellSize*y + 1 + cellSize/2;
                graphicsContext.fillPolygon(polygonXPoints, polygonYPoints, 3);
                break;
            case Right:
                polygonXPoints[0] = cellSize*x + 1 + cellSize/2;
                polygonXPoints[1] = cellSize*x + cellSize;
                polygonXPoints[2] = cellSize*x + 1 + cellSize/2;

                polygonYPoints[0] = cellSize*y + 1;
                polygonYPoints[1] = cellSize*y + 1 + cellSize/2;
                polygonYPoints[2] = cellSize*y + cellSize;
                graphicsContext.fillPolygon(polygonXPoints, polygonYPoints, 3);
                break;
            case Left:
                polygonXPoints[0] = cellSize*x + 1 + cellSize/2;
                polygonXPoints[1] = cellSize*x + 1;
                polygonXPoints[2] = cellSize*x + 1 + cellSize/2;

                polygonYPoints[0] = cellSize*y + 1;
                polygonYPoints[1] = cellSize*y + 1 + cellSize/2;
                polygonYPoints[2] = cellSize*y + cellSize;
                graphicsContext.fillPolygon(polygonXPoints, polygonYPoints, 3);
                break;
            case Thinking:
                polygonXPoints[0] = cellSize*x + 1;
                polygonXPoints[1] = cellSize*x + 1 + cellSize/2;
                polygonXPoints[2] = cellSize*x + cellSize;
                polygonXPoints[3] = cellSize*x + 1 + cellSize/2;

                polygonYPoints[0] = cellSize*y + 1 + cellSize/2;
                polygonYPoints[1] = cellSize*y + 1;
                polygonYPoints[2] = cellSize*y + 1 + cellSize/2;
                polygonYPoints[3] = cellSize*y + cellSize;
                graphicsContext.fillPolygon(polygonXPoints, polygonYPoints, 4);
                break;
        }

    }



    public void thinkAndMove(boolean isNewPositionPassable){
        if(isNewPositionPassable)
        {
            lastMovingStatus = ActionStatus.Successful;
            moveSuccessfullyTo(chosenDirection);
        }
        else
        {
            lastMovingStatus = ActionStatus.Obstacle;
            switch (chosenDirection) {
                case Up:
                    environmentInfo.replace(new Pair<>(xRelativePosition, yRelativePosition - 1), -1);
                    break;
                case Down:
                    environmentInfo.replace(new Pair<>(xRelativePosition, yRelativePosition + 1), -1);
                    break;
                case Right:
                    environmentInfo.replace(new Pair<>(xRelativePosition + 1, yRelativePosition), -1);
                    break;
                case Left:
                    environmentInfo.replace(new Pair<>(xRelativePosition - 1, yRelativePosition), -1);
                    break;
                default:
                    break;
            }
        }
    }

    private void moveSuccessfullyTo(Action direction){
        switch (direction){
            case Up:
                yRelativePosition--;
                break;
            case Down:
                yRelativePosition++;
                break;
            case Right:
                xRelativePosition++;
                break;
            case Left:
                xRelativePosition--;
                break;
            case Thinking:
                break;
        }
    }

    public Action chooseNewPosition(){
        return chooseNewDirection();
    }

    private Action chooseNewDirection(){
        //Add info that agent has visited current position one more time
        Pair<Integer, Integer> currentPosition = new Pair<>(xRelativePosition, yRelativePosition);
        environmentInfo.merge(currentPosition, 1, Integer::sum);

        //Add the nearest directions agent can visit
        Pair<Integer, Integer> upper = new Pair<>(xRelativePosition, yRelativePosition - 1);
        Pair<Integer, Integer> lower = new Pair<>(xRelativePosition, yRelativePosition + 1);
        Pair<Integer, Integer> left = new Pair<>(xRelativePosition - 1, yRelativePosition);
        Pair<Integer, Integer> right = new Pair<>(xRelativePosition + 1, yRelativePosition);

        if(!environmentInfo.containsKey(upper)){
            environmentInfo.put(upper, 0);
        }
        if(!environmentInfo.containsKey(lower)){
            environmentInfo.put(lower, 0);
        }
        if(!environmentInfo.containsKey(left)){
            environmentInfo.put(left, 0);
        }
        if(!environmentInfo.containsKey(right)){
            environmentInfo.put(right, 0);
        }

        if(lastMovingStatus == ActionStatus.Obstacle)
        {
            switch (chosenDirection)
            {
                case Up:
                    chosenDirection = Action.Left;
                    break;
                case Left:
                    chosenDirection = Action.Down;
                    break;
                case Down:
                    chosenDirection = Action.Right;
                    break;
                case Right:
                    chosenDirection = Action.Up;
                    break;
                case Thinking:
                    chosenDirection = Action.Up;
                    break;
                default:
                    break;
            }
        }
        else {
            int[] mass = new int[4];
            //Точка выше
            mass[0] = environmentInfo.get(upper);
            //Точка ниже
            mass[1] = environmentInfo.get(lower);
            //Точка левее
            mass[2] = environmentInfo.get(left);
            //Точка правее
            mass[3] = environmentInfo.get(right);
            int minValue = 0;
            int minValueIndex = 0;
            //Найдем достижимую наименьшую позицию
            boolean firstReached = false;
            for (int i = 0; i < 4; i++) {
                if (mass[i] < 0) {
                    continue;
                } else {
                    if (firstReached == false) {
                        minValue = mass[i];
                        minValueIndex = i;
                        firstReached = true;
                    } else {
                        if (minValue > mass[i]) {
                            minValue = mass[i];
                            minValueIndex = i;
                        }
                    }
                }
            }
            //Меняем местоположение робота
            switch (minValueIndex)
            {
                case(0):
                    //Вверх
                    chosenDirection = Action.Up;
                    break;
                case(1):
                    //Вниз
                    chosenDirection = Action.Down;
                    break;
                case(2):
                    //Влево
                    chosenDirection = Action.Left;
                    break;
                case(3):
                    //Вправо
                    chosenDirection = Action.Right;
                    break;
            }
        }
        return chosenDirection;
    }

    public enum  Action{
        Up, Down, Right, Left, Thinking
    }

    public enum ActionStatus{
        Successful, Obstacle
    }
}
