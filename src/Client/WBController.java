package Client;

import ChatBox.ChatClient;
import RMIInterfaces.ChatServerInterface;
import RMIInterfaces.ClientServer;
import RMIInterfaces.ServerInterface;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Optional;


public class WBController implements ClientServer, Serializable {

    protected double startX;

    protected double startY;

    protected double endX;

    protected double endY;

    private int canvasCount = 0;

    public Boolean close = false;

    private File file = null;

    public void setFile(File file) {
        this.file = file;
    }

    private String message;

    public String getUserName() {
        return userName;
    }

    private String userName;

    private String client1 = null;

    private String client2 = null;

    private String client3 = null;

    private int clientCount = 0;


    private Boolean isManager = false;

    private Boolean testSignIn = true;

    private Boolean isRegistered = false;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        System.out.println("SetMessage" + message);
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
    private Pane mainPane;

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

    @FXML
    private Button clientOne;

    @FXML
    private Button clientTwo;

    @FXML
    private Button clientThree;

    @FXML
    private Label managerName;


    protected ArrayList<Point> pointList = new ArrayList<Point>();
    Registry registry;
    ServerInterface gsonServant;
    ChatServerInterface chatServant;


    private final ToggleGroup group = new ToggleGroup();

    private void setClient() throws Exception{
        ArrayList<ChatClient> chatClients = chatServant.getChatClients();
        client1 = chatClients.get(1).getUserName();
        client2 = chatClients.get(2).getUserName();
        client3 = chatClients.get(3).getUserName();
        clientCount = chatClients.size();
    }




    // Initialize the canvas to make sure the default color of colorPicker is black.
    public void setImage() {
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
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
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

//        colorPicker.setValue(Color.BLACK);
//        setImage();
//        sketch();

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
            pointList.add(getPoint(e.getX(), e.getY()));
            g.setStroke(colorPicker.getValue());
            g.stroke();

        });
        pathCanvas.setOnMouseDragged(e -> {

            g.lineTo(e.getX(), e.getY());
            pointList.add(getPoint(e.getX(), e.getY()));
            g.stroke();

        });
        pathCanvas.setOnMouseReleased(e -> {
            canvasCount = 1;
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
            pointList.add(getPoint(e.getX(), e.getY()));
            g.clearRect(x, y, size, size);
        });
        pathCanvas.setOnMouseDragged(e -> {
            double size = slider.getValue();
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;
            pointList.add(getPoint(e.getX(), e.getY()));
            g.clearRect(x, y, size, size);
            g.closePath();
        });
        pathCanvas.setOnMouseReleased(e -> {
            canvasCount = 1;
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
            canvasCount = 1;
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
            canvasCount = 1;
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
            canvasCount = 1;
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
            canvasCount = 1;
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
            canvasCount = 1;

        });

    }


    private void newFile() throws IOException {
        gsonServant.tellSeverNew(true);
        canvasPane.getChildren().remove(canvas);
        canvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        pathCanvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        canvas.setStyle("-fx-background-color: white");
        pathCanvas.setStyle("-fx-background-color: white");
        canvasPane.getChildren().add(canvas);
        canvasPane.getChildren().add(pathCanvas);
        slider.setValue(1);
        colorPicker.setValue(Color.BLACK);
        setFile(null);
        canvasCount = 0;

    }



    public void onNew() throws IOException {
        if (isManager) {
            if (canvasCount == 1) {
                infoBox("Your changes will be lost if you don't save them.",
                        "Do you want to save the changes?", "save");
            } else {
                newFile();
                setFont();
            }
        }
    }

    private void save() throws IOException {
        int width = (int) canvasPane.getWidth();
        int height = (int) canvasPane.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        canvas.snapshot(null, writableImage);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
        ImageIO.write(renderedImage, "png", file);
        canvasCount = 0;
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
        if (isManager) {
            if (canvasCount == 1) {
                infoBox("Your changes will be lost if you don't save them.",
                        "Do you want to save the changes?", "open");
            } else {
                open();
            }
        }

    }

    public void onExit() throws IOException {

        if (canvasCount == 1) {
            infoBox("Your changes will be lost if you don't save them.",
                    "Do you want to save the changes?", "exit");
            if (close == true) {
                Platform.exit();
            }
        } else {
            Platform.exit();
        }
    }

    public void onClose() throws IOException {
        if (isManager) {
            confirmBox("Close", "Close the Whiteboard", "All Clients will lose the connections",0);
        }
    }

    private void kick(String userName, int clientNum) throws IOException {
        confirmBox("Kick", "Kick the " + userName + "!",
                "Do you want to kick the " + userName + " ?", clientNum);
        chatServant.kickClient(userName);
    }



<<<<<<< HEAD

    public Boolean approve(String clientName) throws IOException {
        if (isManager) {
            int currentNum = clientCount;
            confirmBox("Approve", "Approve the " + clientName + "!",
                    "Do you want to approve the " + clientName + " ?", currentNum+1);
            if(clientCount - currentNum == 1 ){
                return true;
            }
            else{
                return false;
            }
=======
    private void approve(String userName ,int clientNum) throws IOException {
        if(isManager) {
            confirmBox("Approve", "Approve the " + userName + "!",
                    "Do you want to approve the " + userName + " ?",clientNum);
>>>>>>> ca8dc8efa3170f5a83038612d4abef19cf3dc677
        }
        return false;
    }

    public void kickUserOne() throws IOException {
        if (isManager) {
            String clientName = clientOne.getText();
            kick(clientName,1);
        }
    }

    public void kickUserTwo() throws IOException {
        if (isManager) {
            String clientName = clientTwo.getText();
            kick(clientName,2);
        }
    }

    public void kickUserThree() throws IOException {
        if (isManager) {
            String clientName = clientThree.getText();
            kick(clientName,3);
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

    // this is for manager to control the client
    private void confirmBox(String command, String header, String content, int clientNum) throws IOException {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle(command);
        confirmAlert.setHeaderText(header);
        confirmAlert.setContentText(content);
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");

        confirmAlert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);


        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.get() == buttonTypeOne) {
            switch (command) {
                case "Kick":
                    if (clientNum==2) {
                        clientOne.setText(null);
                        break;
                    }
                    if (clientNum==3) {
                        clientTwo.setText(null);
                        break;
                    }
                    if (clientNum==4) {
                        clientThree.setText(null);
                        break;
                    }
                    break;
                case "Approve":
                    if (clientNum == 2) {
                        clientOne.setText(userName);
                        clientCount = 2;
                        break;
                    }
                    if (clientNum == 3) {
                        clientTwo.setText(userName);
                        clientCount = 3;
                        break;
                    }
                    if (clientNum == 4) {
                        clientThree.setText(userName);
                        clientCount = 4;
                        break;
                    }
                    break;
                case "Close":
                    infoBox("Your changes will be lost if you don't save them.",
                            "Do you want to save the changes?", "exit");

            }

        }
        if (result.get() == buttonTypeTwo) {
            confirmAlert.close();
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

    public void setServant(ServerInterface gsonServant,ChatServerInterface chatServant) {
        this.gsonServant = gsonServant;
        this.chatServant = chatServant;
    }


    private void jsonSendUserData(String userName, String password) {
        //test userSys
        /*
        try{
            gsonServant.sendGson("username", userName);
            gsonServant.sendGson("passward", password);
        }catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public synchronized void autoPaint(String keyword, PaintAttribute attribute) {
        // convert Json String back to PaintAttribute object.
        // System.out.println(gson.fromJson(attribute, PaintAttribute.class));
        switch (keyword) {
            case "sketch":
                autoSketch(attribute);
                break;
            case "erase":
                autoErase(attribute);
                break;
            case "line":
                autoLine(attribute);
                break;
            case "cir":
                autoCir(attribute);
                break;
            case "rect":
                autoRect(attribute);
                break;
            case "oval":
                autoOval(attribute);
                break;
            case "text":
                autoText(attribute);
                break;
            default:
                System.out.println("Error shape keyword! Lao Ma Ni Za Hui SHier!!!");
                break;
        }
    }

    private void autoSketch(PaintAttribute attribute) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.beginPath();
        Color newColor = assembleColor(attribute);
        g.setStroke(newColor);
        g.setLineWidth(attribute.getLineWidth());
        ArrayList<Point> nodeList = attribute.getPointList();
        for (int i = 0; i < nodeList.size(); i++) {
            g.lineTo(nodeList.get(i).getPointX(), nodeList.get(i).getPointY());
        }
        g.stroke();
        canvasCount = 1;
        g.closePath();
    }

    private void autoErase(PaintAttribute attribute) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.beginPath();
        double size = attribute.getLineWidth();
        ArrayList<Point> nodeList = attribute.getPointList();
        for (int i = 0; i < nodeList.size(); i++) {
            double x = nodeList.get(i).getPointX() - size / 2;
            double y = nodeList.get(i).getPointY() - size / 2;
            g.clearRect(x, y, size, size);
        }
        canvasCount = 1;
        g.closePath();

    }

    private void autoLine(PaintAttribute attribute) {
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
        canvasCount = 1;
        g.closePath();
    }

    private void autoCir(PaintAttribute attribute) {
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
        canvasCount = 1;
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
        canvasCount = 1;
        g.closePath();
    }

    private void autoOval(PaintAttribute attribute) {
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
        canvasCount = 1;
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

    private Color assembleColor(PaintAttribute attribute) {
        double[] colorList = attribute.getColor();
        Color newColor = Color.color(colorList[0], colorList[1], colorList[2]);
        return newColor;
    }


    public void send() throws IOException {
        String message = input.getText();
        setMessage(message);
        input.clear();
        chatServant.printToAll(message);
//        chatServant.shareMsg(userName,message);
    }


    //print to GUI chat room
    public void setText(String msgPrint) throws IOException {
        message = msgPrint;

    }

    public void appendToMessage(String message){
        textMessage.appendText(message+"\n");
    }


    public void signIn(String user, String encrypt) throws Exception {
//        gsonServant.checkPassword(user, encrypt);
//        Boolean isSignIn = gsonServant.logginResult();
        if (true) {
            // the number of client
            if (clientCount == 0) {
                isManager = true;
//                signInPane.setVisible(false);
//                wbPane.setVisible(true);
                managerName.setText(user);
                userName = user;

                chatServant.addChatClient(user,chatServant,gsonServant);


                //launch the whiteboard and turn off the signIn UI
            } else if (clientCount < 4) {
                isManager = false;
                userName = user;
                //launch the whiteboard and turn off the signIn UI
                // launch the client
                chatServant.addChatClient(user,chatServant,gsonServant);

            } else if (clientCount == 4) {
                warningDialog("Fail to login In", "You can not join in this room!");
            }
        } else {
            warningDialog(user + " is not existed!",
                    "You should confirm your username or register for " + user + " !");
        }
    }

//    public void signUp() throws Exception {
//        String userRegister = nameInput.getText();
//        String passwordRe = passWordInput.getText();
//        gsonServant.registerUser(userRegister, passwordRe);
//        Boolean isRegistered =gsonServant.validRegister();
//        if (isRegistered) {
//            warningDialog(userRegister + " is existed!", "Please change your username to register!");
//
//        } else {
//            inforDialog(userRegister);
//        }
//
//    }

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

    public void errorDialog(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public void loginDialog(){
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Welcome");
        dialog.setHeaderText("LogIn");

        ImageView imageLogin = new ImageView(this.getClass().getResource("../user.png").toString());
        imageLogin.setFitHeight(40);
        imageLogin.setFitWidth(40);
        dialog.setGraphic(imageLogin);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameInput = new TextField();
        nameInput.setPromptText("Username");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(nameInput, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordInput, 1, 1);


        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        nameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> nameInput.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(nameInput.getText(), passwordInput.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            try {
                signIn(usernamePassword.getKey(),usernamePassword.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }




}

