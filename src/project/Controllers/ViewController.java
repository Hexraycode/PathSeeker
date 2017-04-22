package project.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import project.Field;

import java.util.Timer;
import java.util.TimerTask;


public class ViewController {
    @FXML
    private Canvas canvasField;
    @FXML
    private Label agentBouncesCountLabel;
    @FXML
    private Label trashAverageAmountLabel;

    private Field field;
    private Timeline timer;

    public void initialize(){
        GraphicsContext graphicsContext = canvasField.getGraphicsContext2D();
        field = new Field(graphicsContext, 20, 20, 20);
        timer = new Timeline(new KeyFrame(Duration.millis(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                field.doOneStep();
                agentBouncesCountLabel.setText("Bounced: " + field.getAgentBounces());
                trashAverageAmountLabel.setText("Trash avg: " + field.getTrashAverageAmount());
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    public void beginTraverseButtonHandler(ActionEvent actionEvent) {
        timer.play();
    }

    public void doOneStepHandler(ActionEvent actionEvent) {
        field.doOneStep();
    }

    public void doMillionSteps(ActionEvent actionEvent) {

    }
}
