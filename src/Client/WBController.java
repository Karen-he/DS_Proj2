package Client;

import ChatBox.ChatClient;
import RMIInterfaces.ChatServerInterface;
import RMIInterfaces.ServerInterface;
import RMIInterfaces.UserSysInterface;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;


public class WBController {

    @FXML
    private BorderPane wbPane;

    @FXML
    private BorderPane signInPane;

    @FXML
    private TextField nameInput;

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

    protected double startX;

    protected double startY;

    protected double endX;

    protected double endY;

    private int canvasCount = 0;

    public Boolean close = false;

    private File file = null;

    private String userName;

    private String client1 = null;

    private String client2 = null;

    private String client3 = null;

    private int clientCount;

    private Boolean isManager = false;

    private final ToggleGroup group = new ToggleGroup();

    protected ArrayList<Point> pointList = new ArrayList<Point>();
    Registry registry;
    ServerInterface gsonServant;
    ChatServerInterface chatServant;
    UserSysInterface userSysServant;

    // initialize the default value of controller.
    public void initialize() throws Exception {
        colorPicker.setValue(Color.BLACK);
        setImage();
        sketch();
    }

    public void setServant(ServerInterface gsonServant, ChatServerInterface chatServant,
                           UserSysInterface userSysServant) {
        this.gsonServant = gsonServant;
        this.chatServant = chatServant;
        this.userSysServant = userSysServant;
    }

    // Set the value of file when new file is created.
    public void setFile(File file) {
        this.file = file;
    }

    // Judge whether the user is manager.
    public void setIsManager(Boolean manager) {
        isManager = manager;
    }

    // Count the number of clients for manager
    public void setClientCount(int clientNum) {
        clientCount = clientNum;
    }

    // Update the client list.
    public void setClient() throws Exception {
        ArrayList<ChatClient> chatClients = chatServant.getChatClients();
        managerName.setText(chatClients.get(0).getUserName());
        int clientSize = chatClients.size();
        if (clientSize == 4) {
            clientOne.setText(chatClients.get(1).getUserName());
            clientTwo.setText(chatClients.get(2).getUserName());
            clientThree.setText(chatClients.get(3).getUserName());
        } else if (clientSize == 3) {
            clientOne.setText(chatClients.get(1).getUserName());
            clientTwo.setText(chatClients.get(2).getUserName());
        } else if (clientSize == 2) {
            clientOne.setText(chatClients.get(1).getUserName());
        }
    }

    // Initialize the canvas to make sure the default color of colorPicker is black.
    private void setImage() {
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

    // It can change the size of font, which can be displayed while moving the slider.
    private void setFont() throws Exception {
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

    public Boolean getManager() {
        return isManager;
    }

    public String getUserName() {
        return userName;
    }

    private Point getPoint(double x, double y) {
        Point p = new Point(x, y);
        p.setPoint(x, y);
        return p;
    }

    // Press the mouse, it starts to paint.
    public void sketch() throws Exception {
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
            jsonSendPaints("sketch", addPaintAttri(pointList, "null", "null"));
            pointList.clear();
            g.closePath();
        });

    }

    // Press the mouse, it starts to erase.
    public void erase() throws Exception {
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
            jsonSendPaints("erase", addPaintAttri(pointList, "null", "null"));
            pointList.clear();
        });

    }

    // Press the mouse to get the starting point, release the mouse to get the final point
    // Then draw the line.
    public void lineDraw() throws Exception {
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
            jsonSendPaints("line", addPaintAttri(pointList, "null", "null"));
            pointList.clear();
            g.closePath();

        });

    }

    // Press the mouse to get the starting point, release the mouse to get the final point
    // Then draw the circle.
    public void cirDraw() throws Exception {
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
            jsonSendPaints("cir", addPaintAttri(pointList, "null", "null"));
            pointList.clear();
            g.closePath();
        });

    }

    // Press the mouse to get the starting point, release the mouse to get the final point
    // Then draw the rectangular.
    public void rectDraw() throws Exception {
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
            jsonSendPaints("rect", addPaintAttri(pointList, "null", "null"));
            pointList.clear();
            g.closePath();

        });

    }

    // Press the mouse to get the starting point, release the mouse to get the final point
    // Then draw the oval.
    public void ovalDraw() throws Exception {
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
            jsonSendPaints("oval", addPaintAttri(pointList, "null", "null"));
            pointList.clear();
            g.closePath();
        });

    }

    // Press the mouse to get the starting point and input the content into textfield.
    // After then, it can print the text you input in canvas.
    public void textInput() throws Exception {
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
                    jsonSendPaints("text", addPaintAttri(pointList, text, "null"));
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

    // Action of whiteBoard.fxml, to new the file, only for manager.
    public void onNew() throws Exception {
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

    // Action of whiteBoard.fxml, to save the file, only for manager.
    public void onSave() throws IOException {
        if (isManager) {
            if (file != null) {
                save();
            } else {
                onSaveAs();
            }
        }
    }

    // Action of whiteBoard.fxml, to save as other formats of file, only for manager.
    public void onSaveAs() throws IOException {
        try {
            if (isManager) {
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
        } catch (ConnectException e) {
            errorDialog("Connection Error", "Connection is lost!");
        }
    }

    // Action of whiteboard.fxml, to open the file from the file chooser, only for manager.
    public void onOpen() throws Exception {
        if (isManager) {
            if (canvasCount == 1) {
                infoBox("Your changes will be lost if you don't save them.",
                        "Do you want to save the changes?", "open");
            } else {
                try {
                    open();
                } catch (ConnectException e) {
                    errorDialog("Connection Error", "Connection is lost!");
                }
            }
        }

    }

    // Action of whiteboard.fxml, to close all the whiteboard of clients, all clients will disconnect to the manager.
    public void onClose() throws Exception {
        if (isManager) {
            confirmBox("CloseManager", "Close the Whiteboard", "All Clients will lose the connections", 0, "");

        } else {
            confirmBox("CloseClient", "It will disconnect.", "Do you want to continue this?", clientCount, "");
            if (close == true) {
                Platform.exit();
            }
        }
    }


    // Function of creating new life, called by Action "onNew".
    public void newFile() throws IOException {
        try {
            String timeStamp = (new Timestamp(System.currentTimeMillis())).toString();
            gsonServant.tellNewCanvas(true, timeStamp);
        } catch (ConnectException e) {
            errorDialog("Connection Error", "Connection is lost!");
        }

    }

    // Function of saving file into local, called by "onSave".
    private void save() throws IOException {
        try {
            int width = (int) canvasPane.getWidth();
            int height = (int) canvasPane.getHeight();
            WritableImage writableImage = new WritableImage(width, height);
            canvas.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
            canvasCount = 0;
        } catch (ConnectException e) {
            errorDialog("Connection Error", "Connection is lost!");
        }
    }


    // Function of opening the file from local file, called by open.
    private void open() throws Exception {
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
            try {
                Image image = new Image(new FileInputStream(file));
                GraphicsContext g = canvas.getGraphicsContext2D();
                g.drawImage(image, 0, 0, canvasPane.getWidth(), canvasPane.getHeight());
                setFont();
                String imageString = encodeImage(file);
                //System.out.println("who are you = " + imageString);
                jsonSendPaints("image", addPaintAttri(pointList, "null", imageString));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // Used to implement the chatBox.
    public void send() throws IOException {
        try {
            String message = input.getText();
            input.clear();
            //System.out.println("hou get here 0");
            String fullMessgae = userName + ": " + message;
            //System.out.println("hou get here 1");
            String timestamp = (new Timestamp(System.currentTimeMillis())).toString();
            gsonServant.sendMessage(userName, fullMessgae, timestamp);
        } catch (ConnectException e) {
            errorDialog("Server Error", "Server Disconnected! Please contact manager!");
        }

    }

    // Put all the message into testArea to show the conversations between clients.
    public void appendToMessage(String message) {
        textMessage.appendText(message + "\n");
    }

    // Action of whiteBoard.fxml, click the button 'Sign In' to access the whiteboard.
    public void signIn() throws Exception {
        String user = nameInput.getText();
        try {
            switch (clientCount) {
                case 1:
                    signInPane.setVisible(false);
                    wbPane.setVisible(true);
                    managerName.setText(user);
                    userName = user;
                    ChatClient chatClient = new ChatClient(user, chatServant, gsonServant);
                    break;

                case 2:
                case 3:
                case 4:
                    listenApproval(user);
                    break;

            }
        } catch (ConnectException e) {
            errorDialog("Connection Error", "Connection is lost!");
        }
    }

    // Approve the client to access the whiteboard after approvement from manager.
    public void approve(String clientName) throws Exception {
        if (isManager) {
            confirmBox("Approve", "Approve the " + clientName + "!",
                    "Do you want to approve the " + clientName + " ?", clientCount, clientName);
        }
    }

    // Listen whether the client who want to log in is approved by manager.
    private void listenApproval(String clientName) throws Exception {
        userSysServant.sendRequest(clientName);
        boolean run = true;
        String approval = null;
        while (run) {
            approval = userSysServant.checkApproval(clientName);
            if (approval != null) {
                run = false;
            }
        }
        if (approval.equals("Y")) {
            signInPane.setVisible(false);
            wbPane.setVisible(true);
            userName = clientName;
            approveDialog();
            ChatClient chatClient = new ChatClient(clientName, chatServant, gsonServant);
            setClient();
        } else {
            warningDialog("DECLINED", "Your request has been denied!");
            Platform.exit();
        }

    }

    // Kick the first client according to click the button which show the name of clientOne, manager only.
    public void kickUserOne() throws Exception {
        if (isManager) {
            String clientName = clientOne.getText();
            kick(clientName, 2);
        }
    }

    // Kick the second client according to click the button which show the name of clientTwo, manager only.
    public void kickUserTwo() throws Exception {
        if (isManager) {
            String clientName = clientTwo.getText();
            kick(clientName, 3);
        }
    }

    // Kick the third client according to click the button which show the name of clientThree, manager only.
    public void kickUserThree() throws Exception {
        if (isManager) {
            String clientName = clientThree.getText();
            kick(clientName, 4);
        }
    }


    // Kick function called by three button 'kickUserOne', 'kickUserTwo', 'kickUserThree'.
    private void kick(String userName, int clientNum) throws Exception {
        confirmBox("Kick", "Kick the " + userName + "!",

                "Do you want to kick the " + userName + " ?", clientNum, userName);
        chatServant.kickClient(userName);
        userSysServant.kick(userName);
    }

    // If manager kick one of the client, client will be informed you are kicked off.
    public void beKicked() throws IOException {
        warningDialog("Sorry", "You have been kicked off!");
        Platform.exit();
    }


    private void jsonSendPaints(String shapeKey, PaintAttribute attribute) {
        try {
            String timestamp = (new Timestamp(System.currentTimeMillis())).toString();
            String output = gsonServant.sendPaints(shapeKey, attribute, timestamp);
            // String output = sendPoints(shapeKey, list);
            System.out.println("output = " + output);

        } catch (ConnectException e) {
            errorDialog("Connection Error", "Connection is lost!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private PaintAttribute addPaintAttri(ArrayList<Point> pointList, String text, String imageString) {
        double lineWidth = slider.getValue();
        double colorRed = colorPicker.getValue().getRed();
        double colorGreen = colorPicker.getValue().getGreen();
        double colorBlue = colorPicker.getValue().getBlue();
        double[] color = {colorRed, colorGreen, colorBlue};
        PaintAttribute paintAttribute = new PaintAttribute(pointList, lineWidth, color, text, imageString);
        return paintAttribute;
    }

    public synchronized void autoPaint(String keyword, PaintAttribute attribute) {
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
            case "image":
                try {
                    autoOpen(attribute);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void autoSketch(PaintAttribute attribute) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Color newColor = assembleColor(attribute);
        g.setStroke(newColor);
        g.setLineWidth(attribute.getLineWidth());
        ArrayList<Point> nodeList = attribute.getPointList();
        g.beginPath();
        for (int i = 0; i < nodeList.size(); i++) {
            g.lineTo(nodeList.get(i).getPointX(), nodeList.get(i).getPointY());
            g.stroke();
        }
        g.closePath();
        canvasCount = 1;
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

    public void autoNew() throws IOException {
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

    private void autoOpen(PaintAttribute attribute) throws IOException {
        String imageString = attribute.getImageString();
        byte[] byteData = Base64.decodeBase64(imageString);
        try {
            BufferedImage aweImage = ImageIO.read(new ByteArrayInputStream(byteData));
            Image image = SwingFXUtils.toFXImage(aweImage, null);

            System.out.println("Did you come here???");
            GraphicsContext g = canvas.getGraphicsContext2D();
            g.drawImage(image, 0, 0, canvasPane.getWidth(), canvasPane.getHeight());
            setFont();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encodeImage(File file) {
        String imageString = null;
        try {
            FileInputStream imageFile = new FileInputStream(file);
            byte[] byteData = new byte[(int) file.length()];
            imageFile.read(byteData);
            imageString = Base64.encodeBase64String(byteData);
            imageFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    private Color assembleColor(PaintAttribute attribute) {
        double[] colorList = attribute.getColor();
        Color newColor = Color.color(colorList[0], colorList[1], colorList[2]);
        return newColor;
    }

    // The dialog for information of approvement.
    private void approveDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("APPROVE");
        alert.setHeaderText("Successful");
        alert.setContentText("Congratulation! you are approved by manager!");
        alert.showAndWait();
    }

    // The dialog for information of warning.
    public void warningDialog(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // The dialog to inform the occured error during the connection.
    public void errorDialog(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // The box which have three options for manager to choose, three cases for this information box,
    // 'Open' action, 'save' action, 'exit' action.
    public void infoBox(String infoMessage, String headerMessage, String command) throws Exception {
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
                    kickAllUser();
                    Platform.exit();
                    break;

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


    // this is for manager to approve or kick the client or close all connection of clients.
    private void confirmBox(String command, String header, String content, int clientNum, String clientName) throws Exception {
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
                    if (clientNum == 2) {
                        chatServant.kickClient(client1);
                        client1 = null;
                        clientOne.setText(client1);
                        clientCount = 1;
                        break;
                    }
                    if (clientNum == 3) {
                        chatServant.kickClient(client2);
                        client2 = null;
                        clientTwo.setText(client2);
                        clientCount = 2;
                        break;
                    }
                    if (clientNum == 4) {
                        chatServant.kickClient(client3);
                        client3 = null;
                        clientThree.setText(client3);
                        clientCount = 3;
                        break;
                    }
                    break;
                case "Approve":
                    if (clientNum == 1) {
                        client1 = clientName;
                        clientOne.setText(client1);
                        clientCount = 2;
                        userSysServant.addApprove(client1, true);
                        String tmpStamp = (new Timestamp(System.currentTimeMillis())).toString();
                        gsonServant.sendMessage("Notification", client1 + " joined room.", tmpStamp);
                        break;
                    }
                    if (clientNum == 2) {
                        client2 = clientName;
                        clientTwo.setText(client2);
                        clientCount = 3;
                        userSysServant.addApprove(client2, true);
                        String tmpStamp = (new Timestamp(System.currentTimeMillis())).toString();
                        gsonServant.sendMessage("Notification", client2 + " joined room.", tmpStamp);
                        break;
                    }
                    if (clientNum == 3) {
                        client3 = clientName;
                        clientThree.setText(client3);
                        clientCount = 4;
                        userSysServant.addApprove(client3, true);
                        String tmpStamp = (new Timestamp(System.currentTimeMillis())).toString();
                        gsonServant.sendMessage("Notification", client3 + " joined room.", tmpStamp);
                        break;
                    }
                    break;
                case "CloseManager":
                    if (canvasCount == 1) {
                        infoBox("Your changes will be lost if you don't save them.",
                                "Do you want to save the changes?", "exit");
                    } else {
                        kickAllUser();
                        Platform.exit();
                        break;
                    }
                    break;
                case "CloseClient":
                    if (clientNum == 2) {
                        chatServant.kickClient(client1);
                        String tmpStamp = (new Timestamp(System.currentTimeMillis())).toString();
                        gsonServant.sendMessage("Exit", this.userName + " left room.", tmpStamp);
                        Platform.exit();
                        break;
                    }
                    if (clientNum == 3) {
                        chatServant.kickClient(client2);
                        String tmpStamp = (new Timestamp(System.currentTimeMillis())).toString();
                        gsonServant.sendMessage("Exit", this.userName + " left room.", tmpStamp);
                        Platform.exit();
                        break;
                    }
                    if (clientNum == 4) {
                        chatServant.kickClient(client3);
                        String tmpStamp = (new Timestamp(System.currentTimeMillis())).toString();
                        gsonServant.sendMessage("Exit", this.userName + " left room.", tmpStamp);
                        Platform.exit();
                        break;
                    }
            }

        }
        if (result.get() == buttonTypeTwo) {
            confirmAlert.close();
            switch (command) {
                case "Approve":
                    if (clientNum == 1) {
                        client1 = null;
                        userSysServant.addApprove(clientName, false);
                        break;
                    }
                    if (clientNum == 2) {
                        client2 = null;
                        userSysServant.addApprove(clientName, false);
                        break;
                    }
                    if (clientNum == 3) {
                        client3 = null;
                        userSysServant.addApprove(clientName, false);
                        break;
                    }
            }
        }
    }

    private void kickAllUser() throws Exception {
        if (clientCount == 4) {
            chatServant.kickClient(client1);
            userSysServant.kick(client1);
            chatServant.kickClient(client2);
            userSysServant.kick(client2);
            chatServant.kickClient(client3);
            userSysServant.kick(client3);
        } else if (clientCount == 3) {
            chatServant.kickClient(client1);
            userSysServant.kick(client1);
            chatServant.kickClient(client2);
            userSysServant.kick(client2);
        } else if (clientCount == 2) {
            chatServant.kickClient(client1);
            userSysServant.kick(client1);
        }
        Platform.exit();
    }

}






