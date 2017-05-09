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
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import project.Fields.AgentField;
import project.Fields.PathSeekerField;

import java.io.*;


public class ViewController {
    @FXML
    private AnchorPane ap;
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

    public void initialize(){
        GraphicsContext graphicsContextAgent = canvasFieldAgent.getGraphicsContext2D();
        agentField = new AgentField(graphicsContextAgent, 20, 20, 20);
        timer = new Timeline(new KeyFrame(Duration.millis(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                agentField.doOneStep();
                agentBouncesCountLabel.setText("Bounced: " + agentField.getAgentBounces());
                trashAverageAmountLabel.setText("Trash avg: " + agentField.getTrashAverageAmount());
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);

        GraphicsContext graphicsContextSeeker = canvasFieldPathFind.getGraphicsContext2D();
        pathSeekerField = new PathSeekerField(graphicsContextSeeker, 20, 20, 20);
    }

    public void beginTraverseButtonHandler(ActionEvent actionEvent) {
        timer.play();
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
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("D:/pathfind.map");
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

    public void loadField(ActionEvent actionEvent) {
        GraphicsContext graphicsContextSeeker = canvasFieldPathFind.getGraphicsContext2D();
        FileInputStream fileIn = null;
        PathSeekerField psfDeserialized = null;
        try {
            fileIn = new FileInputStream("D:/pathfind.map");
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
