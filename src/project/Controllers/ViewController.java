package project.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;


public class ViewController {
    @FXML
    public ComboBox comboBoxCoefficient;
    @FXML
    public ComboBox comboBoxFieldSize;
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
    private Path propertiesPath = Paths.get("config.properties");
    private String propertiesAbsolutePath = propertiesPath.toAbsolutePath().toString();
    private File propertiesFile = new File(propertiesAbsolutePath);
    private GraphicsContext graphicsContextSeeker;

    private boolean isObjectiveDragged;
    private boolean isAgentDragged;

    public void initialize() {
        //GraphicsContext graphicsContextAgent = canvasFieldAgent.getGraphicsContext2D();
        //agentField = new AgentField(graphicsContextAgent, 20, 20, 20);
        comboBoxCoefficient.getItems().add("0.1");
        comboBoxCoefficient.getItems().add("0.2");
        comboBoxCoefficient.getItems().add("0.3");
        comboBoxCoefficient.getItems().add("0.4");
        comboBoxCoefficient.setValue("0.1");

        comboBoxFieldSize.getItems().add("15");
        comboBoxFieldSize.getItems().add("20");
        comboBoxFieldSize.getItems().add("25");
        comboBoxFieldSize.getItems().add("30");
        comboBoxFieldSize.setValue("15");
        graphicsContextSeeker = canvasFieldPathFind.getGraphicsContext2D();
        GraphicsContext graphicsContextSeeker = canvasFieldPathFind.getGraphicsContext2D();
        pathSeekerField = initializePathSeekerField(graphicsContextSeeker);

        comboBoxFieldSize.setOnAction(event ->
        {
            pathSeekerField = initializePathSeekerField(graphicsContextSeeker);
            pathSeekerField.reDrawField();
        });

        comboBoxCoefficient.setOnAction(event -> {
            String val = comboBoxCoefficient.getValue().toString();
            Double valueDouble = Double.parseDouble(val);
            pathSeekerField.setCoefficient(valueDouble);
            doRegenerateField(null);

        });

        timer = new Timeline(new KeyFrame(Duration.millis(5), event -> {
            agentField.doOneStep();
            agentBouncesCountLabel.setText("Bounced: " + agentField.getAgentBounces());
            trashAverageAmountLabel.setText("Trash avg: " + agentField.getTrashAverageAmount());
        }));
        timer.setCycleCount(Timeline.INDEFINITE);

        bindEvents();
    }

    private void bindEvents() {
        //Make node passable or non-passable
        canvasFieldPathFind.addEventHandler(MouseEvent.MOUSE_PRESSED,
                t -> {
                    Pair<Integer, Integer> coordinates = getCoordinatesFromClick(pathSeekerField, t);
                    if (t.isPrimaryButtonDown() && !t.isControlDown()) {

                        if (pathSeekerField.isPassable(coordinates.getKey(), coordinates.getValue())) {
                            pathSeekerField.makeNonPassable(coordinates.getKey(), coordinates.getValue());
                        } else {
                            pathSeekerField.makePassable(coordinates.getKey(), coordinates.getValue());
                        }
                        pathSeekerField.reDrawField();
                    } else if (t.isPrimaryButtonDown() && t.isControlDown()) {
                        int currentCost = pathSeekerField.getCostAmount(coordinates.getKey(), coordinates.getValue());
                        TextInputDialog dialog = new TextInputDialog(String.valueOf(currentCost));
                        dialog.setTitle("Редактирование стоимости");
                        dialog.setHeaderText("Введите новую стоимость перемещения в клетку");

                        Optional<String> newCost = dialog.showAndWait();
                        if (newCost.isPresent()) {
                            int newCostValue = Integer.parseInt(newCost.get());
                            pathSeekerField.setCostAmount(coordinates.getKey(), coordinates.getValue(), newCostValue);
                        }
                        pathSeekerField.reDrawField();
                    }
                });
        //Drag agent or objective
        canvasFieldPathFind.addEventHandler(MouseEvent.DRAG_DETECTED,
                t -> {
                    if (t.isSecondaryButtonDown()) {
                        Pair<Integer, Integer> coordinates = getCoordinatesFromClick(pathSeekerField, t);
                        if (coordinates.getKey() == pathSeekerField.getAgentAbsoluteX() && coordinates.getValue() == pathSeekerField.getAgentAbsoluteY()) {
                            isAgentDragged = true;
                        } else if (coordinates.getKey() == pathSeekerField.getObjectiveAbsoluteX() && coordinates.getValue() == pathSeekerField.getObjectiveAbsoluteY()) {
                            isObjectiveDragged = true;
                        }
                    }
                });

        canvasFieldPathFind.addEventHandler(MouseEvent.MOUSE_RELEASED,
                t -> {
                    isAgentDragged = false;
                    isObjectiveDragged = false;
                    Properties properties = new Properties();
                    properties.setProperty("pathSeekerField.agent.x", String.valueOf(pathSeekerField.getAgentAbsoluteX()));
                    properties.setProperty("pathSeekerField.agent.y", String.valueOf(pathSeekerField.getAgentAbsoluteY()));
                    properties.setProperty("pathSeekerField.objective.x", String.valueOf(pathSeekerField.getObjectiveAbsoluteX()));
                    properties.setProperty("pathSeekerField.objective.y", String.valueOf(pathSeekerField.getObjectiveAbsoluteY()));
                    try {
                        FileOutputStream out = new FileOutputStream(propertiesAbsolutePath);
                        properties.store(out, null);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pathSeekerField.recomputeDistance();
                });

        canvasFieldPathFind.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                t -> {
                    if (isAgentDragged || isObjectiveDragged) {
                        Pair<Integer, Integer> coordinates = getCoordinatesFromClick(pathSeekerField, t);
                        if (pathSeekerField.isPassable(coordinates.getKey(), coordinates.getValue())) {
                            if (isAgentDragged) {
                                pathSeekerField.setAgentAbsoluteX(coordinates.getKey());
                                pathSeekerField.setAgentAbsoluteY(coordinates.getValue());
                            } else if (isObjectiveDragged) {
                                pathSeekerField.setObjectiveAbsoluteX(coordinates.getKey());
                                pathSeekerField.setObjectiveAbsoluteY(coordinates.getValue());
                            }
                            pathSeekerField.reDrawField();
                        }
                    }
                });
    }

    private Pair<Integer, Integer> getCoordinatesFromClick(PathSeekerField pathSeekerField, MouseEvent mouseEvent) {
        int graphicalSize = pathSeekerField.getFieldGraphicalSize();
        int x = (int) (mouseEvent.getX() / graphicalSize);
        int y = (int) (mouseEvent.getY() / graphicalSize);
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

    private PathSeekerField initializePathSeekerField(GraphicsContext graphicsContextSeeker) {
        PathSeekerField pathSeekerField = null;

        //Create if not exists
        if (Files.notExists(propertiesPath)) {
            try {
                int defaultAgentAbsoluteX = 3;
                int defaultAgentAbsoluteY = 3;
                int defaultObjectiveAbsoluteX = 15;
                int defaultObjectiveAbsoluteY = 15;
                propertiesFile.createNewFile();
                Properties properties = new Properties();
                properties.setProperty("pathSeekerField.agent.x", String.valueOf(defaultAgentAbsoluteX));
                properties.setProperty("pathSeekerField.agent.y", String.valueOf(defaultAgentAbsoluteY));
                properties.setProperty("pathSeekerField.objective.x", String.valueOf(defaultObjectiveAbsoluteX));
                properties.setProperty("pathSeekerField.objective.y", String.valueOf(defaultObjectiveAbsoluteY));
                FileOutputStream out = new FileOutputStream(propertiesAbsolutePath);
                properties.store(out, null);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Load properties
        try {
            FileInputStream fis = new FileInputStream(propertiesAbsolutePath);
            Properties properties = new Properties();
            properties.load(fis);
            fis.close();
            int agentAbsoluteX = Integer.parseInt(properties.getProperty("pathSeekerField.agent.x"));
            int agentAbsoluteY = Integer.parseInt(properties.getProperty("pathSeekerField.agent.y"));
            int objectiveAbsoluteX = Integer.parseInt(properties.getProperty("pathSeekerField.objective.x"));
            int objectiveAbsoluteY = Integer.parseInt(properties.getProperty("pathSeekerField.objective.y"));
            String val = comboBoxFieldSize.getValue().toString();
            Integer valueInt = Integer.parseInt(val);
            pathSeekerField = new PathSeekerField(graphicsContextSeeker, valueInt, valueInt, 20, agentAbsoluteX, agentAbsoluteY, objectiveAbsoluteX, objectiveAbsoluteY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathSeekerField;
    }

    public void doRegenerateField(ActionEvent actionEvent) {
        //Load properties
        try {
            FileInputStream fis = new FileInputStream(propertiesAbsolutePath);
            Properties properties = new Properties();
            properties.load(fis);
            int agentAbsoluteX = Integer.parseInt(properties.getProperty("pathSeekerField.agent.x"));
            int agentAbsoluteY = Integer.parseInt(properties.getProperty("pathSeekerField.agent.y"));
            int objectiveAbsoluteX = Integer.parseInt(properties.getProperty("pathSeekerField.objective.x"));
            int objectiveAbsoluteY = Integer.parseInt(properties.getProperty("pathSeekerField.objective.y"));
            pathSeekerField.generateField(agentAbsoluteX, agentAbsoluteY, objectiveAbsoluteX, objectiveAbsoluteY);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void doClearField(ActionEvent actionEvent) {
        pathSeekerField.clearField();
    }

    public void doStatistics(ActionEvent actionEvent) throws InterruptedException {
        int startSize = 5;
        double startCoeff = 0.1;
        // Сгенерировать поле
        for (int i = 2; i < 11; i++) {
            for (double j = startCoeff; j < 0.6; j+=0.1) {
                int size = startSize * i;
                comboBoxFieldSize.setValue(size);
                comboBoxCoefficient.setValue(j);
                int goal = size - 2;
                for (int iter = 0; iter < 2; iter++) {
                    pathSeekerField = initializePathSeekerField(graphicsContextSeeker);
                    System.out.println("Размер поля: " + size + ", коэффициент: " + j + ", итерация: " + iter);
                    pathSeekerField.setObjectiveAbsoluteX(goal);
                    pathSeekerField.setObjectiveAbsoluteY(goal);
                    pathSeekerField.getFieldNodes()[goal][goal].setPassable(true);
                    pathSeekerField.recomputeDistance();
                    long t1Dijkstra = System.currentTimeMillis();
                    pathSeekerField.findPathDijkstra();
                    long t2Dijkstra = System.currentTimeMillis();
                    long lengthDijkstra = t2Dijkstra - t1Dijkstra;
                    System.out.println("Время: " + lengthDijkstra + " мс");
                    long t1AStar = System.currentTimeMillis();
                    pathSeekerField.findPathAStar();
                    long t2AStar = System.currentTimeMillis();
                    long lengthAStar = t2AStar - t1AStar;
                    System.out.println("Время: " + lengthAStar + " мс");
                    System.out.println();
                }
            }

        }
    }
}
