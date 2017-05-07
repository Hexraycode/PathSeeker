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
import javafx.util.Duration;
import project.Fields.AgentField;
import project.Fields.PathSeekerField;


public class ViewController {
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
        pathSeekerField = new PathSeekerField(graphicsContextSeeker, 10, 10, 30);

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

    public void doPathFinding(ActionEvent actionEvent) {
        pathSeekerField.findPath();
    }
}
