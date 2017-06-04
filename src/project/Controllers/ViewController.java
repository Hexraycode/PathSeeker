package project.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.util.Pair;
import project.Fields.AgentField;
import project.Fields.PathSeekerField;

import java.io.*;
import java.util.Optional;


public class ViewController {
    @FXML
    public TextField agentPositionX;
    @FXML
    public TextField agentPositionY;
    @FXML
    public TextField objectivePositionX;
    @FXML
    public TextField objectivePositionY;
    @FXML
    private TextField fieldSize;
    @FXML
    private TextField trashProbability;
    @FXML
    private Canvas canvasFieldAgent;
    @FXML
    public Canvas canvasFieldPathFind;
    @FXML
    private Label agentBouncesCountLabel;
    @FXML
    private Label trashAverageAmountLabel;

    private AgentField agentField;
    private PathSeekerField pathSeekerField;
    private Timeline timer;

    private boolean isObjectiveDragged;
    private boolean isAgentDragged;

    public void initialize(){
        GraphicsContext graphicsContextAgent = canvasFieldAgent.getGraphicsContext2D();
        agentField = new AgentField(graphicsContextAgent, 20, 20, 20);
        timer = new Timeline(new KeyFrame(Duration.millis(5), event -> {
            agentField.doOneStep();
            agentBouncesCountLabel.setText("Bounced: " + agentField.getAgentBounces());
            trashAverageAmountLabel.setText("Trash avg: " + agentField.getTrashAverageAmount());
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        GraphicsContext graphicsContextSeeker = canvasFieldPathFind.getGraphicsContext2D();
        pathSeekerField = new PathSeekerField(graphicsContextSeeker, 20, 20, 20);
        //Make node passable or non-passable
        canvasFieldPathFind.addEventHandler(MouseEvent.MOUSE_PRESSED,
                t -> {
                    Pair<Integer, Integer> coordinates = getCoordinatesFromClick(pathSeekerField, t);
                    if(t.isPrimaryButtonDown() && !t.isControlDown()) {

                        if (pathSeekerField.isPassable(coordinates.getKey(), coordinates.getValue())) {
                            pathSeekerField.makeNonPassable(coordinates.getKey(), coordinates.getValue());
                        } else {
                            pathSeekerField.makePassable(coordinates.getKey(), coordinates.getValue());
                        }
                        pathSeekerField.reDrawField();
                    }
                    else if (t.isPrimaryButtonDown() && t.isControlDown()){
                        int currentCost = pathSeekerField.getCostAmount(coordinates.getKey(),coordinates.getValue());
                        TextInputDialog dialog = new TextInputDialog(String.valueOf(currentCost));
                        dialog.setTitle("Редактирование стоимости");
                        dialog.setHeaderText("Введите новую стоимость перемещения в клетку");

                        Optional<String> newCost = dialog.showAndWait();
                        if (newCost.isPresent()){
                            int newCostValue = Integer.parseInt(newCost.get());
                            pathSeekerField.setCostAmount(coordinates.getKey(), coordinates.getValue(), newCostValue);
                        }
                        pathSeekerField.reDrawField();
                    }
                });
        //Drag agent or objective
        canvasFieldPathFind.addEventHandler(MouseEvent.DRAG_DETECTED,
                t -> {
                    if(t.isSecondaryButtonDown()) {
                        Pair<Integer, Integer> coordinates = getCoordinatesFromClick(pathSeekerField, t);
                        if(coordinates.getKey() == pathSeekerField.getAgentAbsoluteX() && coordinates.getValue() == pathSeekerField.getAgentAbsoluteY())
                        {
                            isAgentDragged = true;
                        }
                        else if(coordinates.getKey() == pathSeekerField.getObjectiveAbsoluteX() && coordinates.getValue() == pathSeekerField.getObjectiveAbsoluteY())
                        {
                            isObjectiveDragged = true;
                        }
                    }
                });

        canvasFieldPathFind.addEventHandler(MouseEvent.MOUSE_RELEASED,
                t -> {
                    isAgentDragged = false;
                    isObjectiveDragged = false;
                });

        canvasFieldPathFind.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                t -> {
                    if(isAgentDragged || isObjectiveDragged) {
                        Pair<Integer, Integer> coordinates = getCoordinatesFromClick(pathSeekerField, t);
                        if(pathSeekerField.isPassable(coordinates.getKey(), coordinates.getValue())) {
                            if(isAgentDragged){
                                pathSeekerField.setAgentAbsoluteX(coordinates.getKey());
                                pathSeekerField.setAgentAbsoluteY(coordinates.getValue());
                            }
                            else if(isObjectiveDragged)
                            {
                                pathSeekerField.setObjectiveAbsoluteX(coordinates.getKey());
                                pathSeekerField.setObjectiveAbsoluteY(coordinates.getValue());
                            }
                            pathSeekerField.reDrawField();
                        }
                    }
                });
    }

    private Pair<Integer, Integer> getCoordinatesFromClick(PathSeekerField pathSeekerField, MouseEvent mouseEvent)
    {
        int graphicalSize = pathSeekerField.getFieldGraphicalSize();
        int x = (int)(mouseEvent.getX() / graphicalSize);
        int y = (int)(mouseEvent.getY() / graphicalSize);
        return new Pair<>(x, y);
    }

    public void beginTraverseButtonHandler(ActionEvent actionEvent) {
        timer.play();
    }

    public void stopTraverseButtonHandler(ActionEvent actionEvent) {
        timer.stop();
    }

    public void doOneStepHandler(ActionEvent actionEvent) {
        agentField.doOneStep();
    }

    public void changeField(ActionEvent actionEvent) {
        agentField.setFieldGraphicalSize(Integer.parseInt(fieldSize.getText()));
        agentField.setTrashProbabilityAmount(Integer.parseInt(trashProbability.getText()));
    }

    public void doDijkstra(ActionEvent actionEvent) {
        pathSeekerField.findPathDijkstra();
    }

    public void doAStar(ActionEvent actionEvent) {
        pathSeekerField.findPathAStar();
    }

    public void doDStar(ActionEvent actionEvent) {
        pathSeekerField.findPathDStar();
    }

    public void doRegenerateField(ActionEvent actionEvent) {
        int objectiveAbsoluteX = Integer.parseInt(objectivePositionX.getText());;
        int objectiveAbsoluteY = Integer.parseInt(objectivePositionY.getText());;
        int agentAbsoluteX = Integer.parseInt(agentPositionX.getText());;
        int agentAbsoluteY = Integer.parseInt(agentPositionY.getText());;

        pathSeekerField.generateField(agentAbsoluteX, agentAbsoluteY, objectiveAbsoluteX, objectiveAbsoluteY);
    }

    public void saveField(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save field");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Field", "*.field"));
        Window w = trashAverageAmountLabel.getScene().getWindow();
        File file = fileChooser.showSaveDialog(w);
        if (file != null) {
            FileOutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(pathSeekerField);
                out.close();
                fileOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadField(ActionEvent actionEvent) {
        GraphicsContext graphicsContextSeeker = canvasFieldPathFind.getGraphicsContext2D();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load field");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Field", "*.field"));
        Window w = trashAverageAmountLabel.getScene().getWindow();
        File file = fileChooser.showOpenDialog(w);
        if (file != null) {
            FileInputStream fileIn = null;
            PathSeekerField psfDeserialized = null;
            try {
                fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                psfDeserialized = (PathSeekerField) in.readObject();
                psfDeserialized.setGlobalGraphicsContext(graphicsContextSeeker);
                pathSeekerField = psfDeserialized;
                in.close();
                fileIn.close();
                pathSeekerField.reDrawField();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
