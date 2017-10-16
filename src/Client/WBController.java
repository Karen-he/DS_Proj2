package Client;

import ChatBox.ChatClient;
import RMIInterfaces.ServerInterface;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Optional;


public class WBController {

    protected double startX;

    protected double startY;

    protected double endX;

    protected double endY;

    private int count = 0;

    public Boolean close = false;

    private File file = null;

    public void setFile(File file) {
        this.file = file;
    }

    private String message;

    private String userName;

    private int clientCount = -1;

    private Boolean manager = false;

    private Boolean testSignIn = true;

    private Boolean isRegistered = false;

    public void setMessage(String message) {
        System.out.println("SetMessage" +message);
        this.message = message;
    }

    @FXML
    private TextField nameInput;

    @FXML
    private TextField passWordInput;

    @FXML
    private Pane signInPane;

    @FXML
    private BorderPane wbPane;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Canvas canvas;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Slider slider;

    @FXML
    private Label sliderSize;

    @FXML
    private Pane canvasPane;

    @FXML
    private TextArea textMessage;

    @FXML
    private TextArea input;

    @FXML
    private Canvas pathCanvas;

    @FXML
    private RadioButton sketch;

    @FXML
    private RadioButton eraser;

    @FXML
    private RadioButton line;

    @FXML
    private RadioButton oval;

    @FXML
    private RadioButton circle;

    @FXML
    private RadioButton rect;

    @FXML
    private RadioButton text;

    @FXML
    private ImageView imageView;

    protected ArrayList<Point> pointList = new ArrayList<Point>();
    Registry registry;
    ServerInterface gsonServant;

    private final ToggleGroup group = new ToggleGroup();



    // Initialize the canvas to make sure the default color of colorPicker is black.
    public void setImage(){
        sketch.setUserData("sketch");
        eraser.setUserData("eraser");
        line.setUserData("line");
        oval.setUserData("oval");
        circle.setUserData("circle");
        text.setUserData("text");
        rect.setUserData("rect");
        sketch.setToggleGroup(group);
        eraser.setToggleGroup(group);
        line.setToggleGroup(group);
        oval.setToggleGroup(group);
        circle.setToggleGroup(group);
        text.setToggleGroup(group);
        rect.setToggleGroup(group);
        sketch.setSelected(true);
        imageView.setImage(new Image("sketch.png"));
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (group.getSelectedToggle() != null) {
                    final Image image = new Image(
                                    group.getSelectedToggle().getUserData().toString() +
                                            ".png"
                            );
                    imageView.setImage(image);
                }
            }
        });

    }
    public void initialize() {

        colorPicker.setValue(Color.BLACK);
        setImage();
        sketch();

    }

    // It can change the size of font, which can be displayed while moving the slider.
    public void setFont() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        GraphicsContext newG = pathCanvas.getGraphicsContext2D();
        slider.valueProperty().addListener(e -> {
            double sliderValue = slider.getValue();
            String str = String.format("%.1f", sliderValue);
            sliderSize.setText(str);
            g.setLineWidth(sliderValue);
            newG.setLineWidth(sliderValue);
        });
        Color color = colorPicker.getValue();
        g.setStroke(color);
        newG.setStroke(color);
    }

    // when pressed the mouse, it starts to paint.
    public void sketch() {
        setFont();
        GraphicsContext g = canvas.getGraphicsContext2D();
        pathCanvas.setOnMousePressed(e -> {
            g.beginPath();
            g.lineTo(e.getX(), e.getY());
            pointList.add(getPoint(e.getX(),e.getY()));
            g.setStroke(colorPicker.getValue());
            g.stroke();

        });
        pathCanvas.setOnMouseDragged(e -> {

            g.lineTo(e.getX(), e.getY());
            pointList.add(getPoint(e.getX(),e.getY()));
            g.stroke();

        });
        pathCanvas.setOnMouseReleased(e -> {
            count = 1;
            jsonSendPaints("sketch", addPaintAttri(pointList, "null"));
            pointList.clear();
            g.closePath();
        });

    }

    public void erase() {
        setFont();
        GraphicsContext g = canvas.getGraphicsContext2D();
        ArrayList<Point> pointList = new ArrayList<>();
        pathCanvas.setOnMousePressed(e -> {
            g.beginPath();
            double size = slider.getValue();
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;
            pointList.add(getPoint(e.getX(),e.getY()));
            g.clearRect(x, y, size, size);
        });
        pathCanvas.setOnMouseDragged(e -> {
            double size = slider.getValue();
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;
            pointList.add(getPoint(e.getX(),e.getY()));
            g.clearRect(x, y, size, size);
            g.closePath();
        });
        pathCanvas.setOnMouseReleased(e -> {
            count = 1;
            jsonSendPaints("erase", addPaintAttri(pointList, "null"));
            pointList.clear();
        });
    }


    public void lineDraw() {
        setFont();
        GraphicsContext g = canvas.getGraphicsContext2D();
        GraphicsContext newG = pathCanvas.getGraphicsContext2D();
        pathCanvas.setOnMousePressed(e -> {

            g.beginPath();
            g.setStroke(colorPicker.getValue());
            newG.setStroke(colorPicker.getValue());
            startX = e.getX();
            startY = e.getY();

        });
        pathCanvas.setOnMouseDragged(e -> {
            endX = e.getX();
            endY = e.getY();
            newG.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
            newG.strokeLine(startX, startY, endX, endY);
        });
        pathCanvas.setOnMouseReleased(e -> {
            count = 1;
            endX = e.getX();
            endY = e.getY();
            newG.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
            g.strokeLine(startX, startY, endX, endY);
            pointList.add(getPoint(startX, startY));
            pointList.add(getPoint(endX, endY));
            jsonSendPaints("line", addPaintAttri(pointList, "null"));
            pointList.clear();
            g.closePath();

        });
    }

    public void cirDraw() {
        setFont();
        GraphicsContext g = canvas.getGraphicsContext2D();
        GraphicsContext newG = pathCanvas.getGraphicsContext2D();
        pathCanvas.setOnMousePressed(e -> {
            g.beginPath();
            g.setStroke(colorPicker.getValue());
            newG.setStroke(colorPicker.getValue());
            startX = e.getX();
            startY = e.getY();
        });
        pathCanvas.setOnMouseDragged(e -> {
            endX = e.getX();
            endY = e.getY();
            double x = Math.min(startX, endX);
            double y = Math.min(startY, endY);
            double height = Math.abs(startY - endY);
            newG.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
            newG.strokeOval(x, y, height, height);

        });
        pathCanvas.setOnMouseReleased(e -> {
            count = 1;
            endX = e.getX();
            endY = e.getY();
            double x = Math.min(startX, endX);
            double y = Math.min(startY, endY);
            double height = Math.abs(startY - endY);
            newG.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
            g.strokeOval(x, y, height, height);
            pointList.add(getPoint(startX, startY));
            pointList.add(getPoint(endX, endY));
            jsonSendPaints("cir", addPaintAttri(pointList, "null"));
            pointList.clear();
            g.closePath();
        });
    }

    public void rectDraw() {
        setFont();
        GraphicsContext g = canvas.getGraphicsContext2D();
        GraphicsContext newG = pathCanvas.getGraphicsContext2D();
        pathCanvas.setOnMousePressed(e -> {
            g.beginPath();
            g.setStroke(colorPicker.getValue());
            newG.setStroke(colorPicker.getValue());
            startX = e.getX();
            startY = e.getY();
        });
        pathCanvas.setOnMouseDragged(e -> {
            endX = e.getX();
            endY = e.getY();
            double x = Math.min(startX, endX);
            double y = Math.min(startY, endY);
            double width = Math.abs(startX - endX);
            double height = Math.abs(startY - endY);
            newG.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
            newG.strokeRect(x, y, width, height);


        });
        pathCanvas.setOnMouseReleased(e -> {
            count = 1;
            endX = e.getX();
            endY = e.getY();
            double x = Math.min(startX, endX);
            double y = Math.min(startY, endY);
            double width = Math.abs(startX - endX);
            double height = Math.abs(startY - endY);
            newG.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
            g.strokeRect(x, y, width, height);
            pointList.add(getPoint(startX, startY));
            pointList.add(getPoint(endX, endY));
            jsonSendPaints("rect", addPaintAttri(pointList, "null"));
            pointList.clear();
            g.closePath();

        });
    }

    public void ovalDraw() {
        setFont();
        GraphicsContext g = canvas.getGraphicsContext2D();
        GraphicsContext newG = pathCanvas.getGraphicsContext2D();
        pathCanvas.setOnMousePressed(e -> {
            g.beginPath();
            g.setStroke(colorPicker.getValue());
            newG.setStroke(colorPicker.getValue());
            startX = e.getX();
            startY = e.getY();
        });
        pathCanvas.setOnMouseDragged(e -> {
            endX = e.getX();
            endY = e.getY();
            double x = Math.min(startX, endX);
            double y = Math.min(startY, endY);
            double width = Math.abs(startX - endX);
            double height = Math.abs(startY - endY);
            newG.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
            newG.strokeOval(x, y, width, height);


        });
        pathCanvas.setOnMouseReleased(e -> {
            count = 1;
            endX = e.getX();
            endY = e.getY();
            double x = Math.min(startX, endX);
            double y = Math.min(startY, endY);
            double width = Math.abs(startX - endX);
            double height = Math.abs(startY - endY);
            newG.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
            g.strokeOval(x, y, width, height);
            pointList.add(getPoint(startX, startY));
            pointList.add(getPoint(endX, endY));
            jsonSendPaints("oval", addPaintAttri(pointList, "null"));
            pointList.clear();
            g.closePath();
        });
    }

    public void textInput() {
        setFont();
        GraphicsContext g = canvas.getGraphicsContext2D();
        pathCanvas.setOnMousePressed(e -> {
            startX = e.getX();
            startY = e.getY();
            Font font = new Font(slider.getValue());
            TextField textField = new TextField();
            textField.setLayoutX(startX);
            textField.setLayoutY(startY);
            textField.setMinWidth(100);
            textField.setMinHeight(50);
            textField.setFont(font);
            textField.setStyle("-fx-background-color: transparent");
            canvasPane.getChildren().add(textField);
            textField.requestFocus();
            textField.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    g.setFont(font);
                    g.setFill(colorPicker.getValue());
                    String text = textField.getText();
                    pointList.add(getPoint(startX, startY));
                    jsonSendPaints("text", addPaintAttri(pointList, text));
                    pointList.clear();
                    canvasPane.getChildren().remove(textField);

                    g.fillText(text, startX - 5, startY + 25);
                }
            });
        });

        pathCanvas.setOnMouseDragged(e -> {

        });
        pathCanvas.setOnMouseReleased(e -> {
            count = 1;

        });

    }


    private void newFile() throws IOException {
        canvasPane.getChildren().remove(canvas);
        canvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        pathCanvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        canvasPane.getChildren().add(canvas);
        canvasPane.getChildren().add(pathCanvas);
        slider.setValue(1);
        colorPicker.setValue(Color.BLACK);
        setFile(null);
        count = 0;
    }


    public void onNew() throws IOException {
        if (count == 1) {
            infoBox("Your changes will be lost if you don't save them.",
                    "Do you want to save the changes?", "save");
        } else {
            newFile();
            setFont();
        }
    }

    private void save() throws IOException {
        int width = (int) canvasPane.getWidth();
        int height = (int) canvasPane.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        canvas.snapshot(null, writableImage);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
        ImageIO.write(renderedImage, "png", file);
        count = 0;
    }

    public void onSave() throws IOException {
        if (file != null) {
            save();
        } else {
            onSaveAs();
        }
    }

    public void onSaveAs() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.bmp", "*.jpg", "*.gif"));
        File tempFile = fileChooser.showSaveDialog(null);
        if (tempFile == null) {

        } else {
            setFile(tempFile);
        }
        if (file != null) {
            save();
        }
    }

    private void open() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.bmp", "*.jpg", "*.gif"));
        File tempFile = fileChooser.showOpenDialog(null);
        if (tempFile == null) {

        } else {
            setFile(tempFile);
        }

        if (file != null) {
            Image image = new Image(new FileInputStream(file));
            GraphicsContext g = canvas.getGraphicsContext2D();
            g.drawImage(image, 0, 0, canvasPane.getWidth(), canvasPane.getHeight());
            setFont();
        }

    }


    public void onOpen() throws IOException {
        if (count == 1) {
            infoBox("Your changes will be lost if you don't save them.",
                    "Do you want to save the changes?", "open");
        } else {
            open();
        }

    }

    public void onExit() throws IOException {

        if (count == 1) {
            infoBox("Your changes will be lost if you don't save them.",
                    "Do you want to save the changes?", "exit");
            if (close == true) {
                Platform.exit();
            }
        } else {
            Platform.exit();
        }
    }


    private Point getPoint(double x, double y) {
        Point p = new Point(x, y);
        p.setPoint(x, y);
        return p;
    }

    public void infoBox(String infoMessage, String headerMessage, String command) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        ButtonType buttonTypeOne = new ButtonType("Don't Save");
        ButtonType buttonTypeTwo = new ButtonType("Cancel");
        ButtonType buttonTypeThree = new ButtonType("Save");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);
        Optional<ButtonType> result = alert.showAndWait();
        switch (command) {
            case "open":
                if (result.get() == buttonTypeOne) {
                    open();
                } else if (result.get() == buttonTypeTwo) {
                    alert.close();
                } else if (result.get() == buttonTypeThree) {
                    onSave();
                    open();
                }
                break;
            case "save":
                if (result.get() == buttonTypeOne) {
                    newFile();
                    setFont();
                } else if (result.get() == buttonTypeTwo) {
                    alert.close();
                } else if (result.get() == buttonTypeThree) {
                    onSave();
                }
                break;
            case "exit":
                if (result.get() == buttonTypeOne) {
                    close = true;
                } else if (result.get() == buttonTypeTwo) {
                    alert.close();
                    close = false;

                } else if (result.get() == buttonTypeThree) {
                    onSave();
                    if (file != null) {
                        Platform.exit();
                        close = true;
                    } else {
                        alert.close();
                    }
                }
                break;
        }

    }


    private void jsonSendPaints(String shapeKey, PaintAttribute attribute) {
        try {
            String output = gsonServant.sendPaints(shapeKey, attribute);
            // String output = sendPoints(shapeKey, list);
            System.out.println("output = " + output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PaintAttribute addPaintAttri(ArrayList<Point> pointList, String text) {
        double lineWidth = slider.getValue();
        double colorRed = colorPicker.getValue().getRed();
        double colorGreen = colorPicker.getValue().getGreen();
        double colorBlue = colorPicker.getValue().getBlue();
        double[] color = {colorRed, colorGreen, colorBlue};
        PaintAttribute paintAttribute = new PaintAttribute(pointList, lineWidth, color, text);
        return paintAttribute;
    }

    public void setServant(ServerInterface gsonServant) {
        this.gsonServant = gsonServant;
    }


    private void jsonSendUserData(String userName, String password){
        //test userSys
        /*
        try{
            gsonServant.sendGson("username", userName);
            gsonServant.sendGson("passward", password);
        }catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public synchronized void autoPaint(String keyword, PaintAttribute attribute){
        // convert Json String back to PaintAttribute object.
        // System.out.println(gson.fromJson(attribute, PaintAttribute.class));
        switch (keyword) {
            case "sketch": autoSketch(attribute);
                break;
            case "erase": autoErase(attribute);
                break;
            case "line": autoLine(attribute);
                break;
            case "cir": autoCir(attribute);
                break;
            case "rect": autoRect(attribute);
                break;
            case "oval": autoOval(attribute);
                break;
            case "text": autoText(attribute);
                break;
            default: System.out.println("Error shape keyword! Lao Ma Ni Za Hui SHier!!!");
                break;
        }
    }

    private void autoSketch(PaintAttribute attribute){
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.beginPath();
        Color newColor = assembleColor(attribute);
        g.setStroke(newColor);
        g.setLineWidth(attribute.getLineWidth());
        ArrayList<Point> nodeList = attribute.getPointList();
        for(int i = 0; i < nodeList.size(); i++){
            g.lineTo(nodeList.get(i).getPointX(), nodeList.get(i).getPointY());
        }
        g.stroke();
        count = 1;
        g.closePath();
    }

    private void autoErase(PaintAttribute attribute){
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.beginPath();
        double size = attribute.getLineWidth();
        ArrayList<Point> nodeList = attribute.getPointList();
        for(int i = 0; i < nodeList.size(); i++){
            double x = nodeList.get(i).getPointX() - size / 2;
            double y = nodeList.get(i).getPointY() - size / 2;
            g.clearRect(x, y, size, size);
        }
            count = 1;
            g.closePath();

    }

    private void autoLine(PaintAttribute attribute){
        GraphicsContext g = canvas.getGraphicsContext2D();
        Color newColor = assembleColor(attribute);
        g.setStroke(newColor);
        g.setLineWidth(attribute.getLineWidth());
        g.beginPath();
        ArrayList<Point> nodeList = attribute.getPointList();
        startX = nodeList.get(0).getPointX();
        startY = nodeList.get(0).getPointY();
        endX = nodeList.get(1).getPointX();
        endY = nodeList.get(1).getPointY();
        g.strokeLine(startX, startY, endX, endY);
        count = 1;
        g.closePath();
    }

    private void autoCir(PaintAttribute attribute){
        GraphicsContext g = canvas.getGraphicsContext2D();
        Color newColor = assembleColor(attribute);
        g.setStroke(newColor);
        g.setLineWidth(attribute.getLineWidth());
        g.beginPath();
        ArrayList<Point> nodeList = attribute.getPointList();
        startX = nodeList.get(0).getPointX();
        startY = nodeList.get(0).getPointY();
        endX = nodeList.get(1).getPointX();
        endY = nodeList.get(1).getPointY();
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double height = Math.abs(startY - endY);
        g.strokeOval(x, y, height, height);
        count = 1;
        g.closePath();
    }

    private void autoRect(PaintAttribute attribute) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Color newColor = assembleColor(attribute);
        g.setStroke(newColor);
        g.setLineWidth(attribute.getLineWidth());
        g.beginPath();
        ArrayList<Point> nodeList = attribute.getPointList();
        startX = nodeList.get(0).getPointX();
        startY = nodeList.get(0).getPointY();
        endX = nodeList.get(1).getPointX();
        endY = nodeList.get(1).getPointY();
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(startX - endX);
        double height = Math.abs(startY - endY);
        g.strokeRect(x, y, width, height);
        count = 1;
        g.closePath();
    }

    private void autoOval(PaintAttribute attribute){
        GraphicsContext g = canvas.getGraphicsContext2D();
        Color newColor = assembleColor(attribute);
        g.setStroke(newColor);
        g.setLineWidth(attribute.getLineWidth());
        g.beginPath();
        ArrayList<Point> nodeList = attribute.getPointList();
        startX = nodeList.get(0).getPointX();
        startY = nodeList.get(0).getPointY();
        endX = nodeList.get(1).getPointX();
        endY = nodeList.get(1).getPointY();
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(startX - endX);
        double height = Math.abs(startY - endY);
        g.strokeOval(x, y, width, height);
        count = 1;
        g.closePath();
    }

    private void autoText(PaintAttribute attribute) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Color newColor = assembleColor(attribute);
        ArrayList<Point> nodeList = attribute.getPointList();
        startX = nodeList.get(0).getPointX();
        startY = nodeList.get(0).getPointY();
        Font font = new Font(attribute.getLineWidth());
        g.setFont(font);
        g.setFill(newColor);
        String text = attribute.getText();
        g.fillText(text, startX - 5, startY + 25);
    }

    private Color assembleColor(PaintAttribute attribute){
        double[] colorList = attribute.getColor();
        Color newColor = Color.color(colorList[0], colorList[1], colorList[2]);
        return newColor;
    }

    private void kickUser(){

    }

    public synchronized void send() throws IOException {
        String allMessages = ("userName: ");
        String message = input.getText();
        setMessage(message);
        allMessages += message;
        input.clear();
        textMessage.appendText(allMessages + "\n");
        gsonServant.sendMessage("userName",allMessages);
    }



    //print to GUI chat room
    public void setText(String msgPrint) throws IOException{
        message = msgPrint;

    }
    public void signIn() throws Exception {
        String user = nameInput.getText();
        String encrypt = passWordInput.getText();
        if (testSignIn) {
            if (true) {
                signInPane.setVisible(false);
                mainPane.setPrefHeight(700);
                mainPane.setPrefWidth(1000);
                wbPane.setVisible(true);

                //launch the whiteboard and turn off the signIn UI
            }else if (clientCount <4){
                //launch the whiteboard and turn off the signIn UI
                // launch the client

            } else if (clientCount == 4) {
                warningDialog("Fail to login In", "You can not join in this room!");
            }
        }
        else{
            warningDialog(user + " is not existed!",
                    "You should confirm your username or register for " + user + " !");
        }
    }

    public void signUp() {
        String userRegister = nameInput.getText();
        String passwordRe = passWordInput.getText();
        if (isRegistered) {
            warningDialog(userRegister + " is existed!", "Please change your username to register!");

        } else {
            inforDialog(userRegister);
        }

    }

    private void inforDialog(String name) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration");
        alert.setHeaderText("Successful");
        alert.setContentText("Congratulation! you can use " + name + " now!");
        alert.showAndWait();
    }

    private void warningDialog(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }


}

