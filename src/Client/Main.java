package Client;

import ChatBox.ChatClient;
import RMIInterfaces.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Hashtable;

import static java.lang.Thread.sleep;

public class Main extends Application {

    private static String ip = "localhost";
    private static int port = 3000;
    private Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception {


        FXMLLoader fLoader = new FXMLLoader(getClass().getResource("whiteBoard.fxml"));

        Parent root = (Parent) fLoader.load();
        WBController WBController = fLoader.getController();
        Registry registry = LocateRegistry.getRegistry(2020);
        ServerInterface gsonServant = (ServerInterface) registry.lookup("Gson");
        ChatServerInterface chatServant = (ChatServerInterface) registry.lookup("Chatbox");
        int clientCount = chatServant.getChatClients().size();
        if (clientCount == 0) {
            WBController.setIsManager(true);
            WBController.setClientCount(1);
        } else if (clientCount > 0 && clientCount < 4) {
            WBController.setIsManager(false);
            WBController.setClientCount(clientCount + 1);
        } else if (clientCount > 4) {
            WBController.warningDialog("Fail to login In", "You can not join in this room!");
            Platform.exit();
        }


        /***
         * synchronize paint
         */
        Thread paint = new Thread(() -> {
            String oldTimePaint = "2017";
            while (true) {
                try {
                    sleep(400);
                    if (!gsonServant.receivePaints().isEmpty()) {
                        ArrayList<String> drawCommand = gsonServant.receivePaints();
                        String timeStamp = drawCommand.get(2);
                        if (!timeStamp.equals(oldTimePaint)) {
                            String shapeOption = drawCommand.get(0);
                            String attributeGson = drawCommand.get(1);
                            PaintAttribute attributeRec = gsonServant.getAttribute(attributeGson);
                            WBController.autoPaint(shapeOption, attributeRec);
                            oldTimePaint = timeStamp;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        paint.start();

        /***
         * The thread for monitoring whether a new canvas is created.
         */

            new Thread (() -> {
                String oldTimeCanvas = "2017";
                    while (true)
                        try {
                            sleep(500);
                            if(!gsonServant.checkNewCanvas().isEmpty()) {
                                ArrayList<String> canvasCommand = gsonServant.checkNewCanvas();
                                String command = canvasCommand.get(0);
                                String timeStamp = canvasCommand.get(1);
                                if (!timeStamp.equals(oldTimeCanvas)) {
                                    if(command.equals("TRUE")) {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    WBController.autoNew();

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        oldTimeCanvas = timeStamp;
                                    }
                                }
                            }
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }catch (RemoteException e) {
                            e.printStackTrace();
                }
            }).start();

        /***
         * This thread is to monitor whether there is a new user want to join the whiteboard.
         */

        Thread approval = new Thread(() -> {
            while (true) {
                try {
                    boolean empty = gsonServant.listenForApproval().isEmpty();
                    if (!empty) {
                        WBController.approve(gsonServant.listenForApproval());

                    }
                } catch (RemoteException e) {
                    //WBController.errorDialog("Connection Error", "Connection is lost!");
                    //e.printStackTrace();
                } catch (IOException e) {

                }
            }
        });

        approval.start();

        /***
         * Show chatroom Content
         ***/

        Thread printChat = new Thread(() -> {
            String oriTimestamp = "2017";
            while (true) {
                try {
                    sleep(500);
//                        System.out.println(gsonServant.receiveMessage());
                    if (!gsonServant.receiveMessage().isEmpty()) {

                        ArrayList<String> tmp = gsonServant.receiveMessage();
                        String timestamp = tmp.get(2);
                        String userName = tmp.get(0);
                        System.out.println(timestamp);
                        if (!oriTimestamp.equals(timestamp)) {
                            String fullPrint = tmp.get(1);
                            System.out.println(userName + fullPrint);
                            WBController.appendToMessage(fullPrint);
                            oriTimestamp = timestamp;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        printChat.start();


        // username get from the name after logging in

        WBController.setServant(gsonServant, chatServant);

        window = primaryStage;
        window.setTitle("WhiteBoard");

        window.setOnCloseRequest(e -> {
            e.consume();
            try {
                closeAction();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        window.setScene(new Scene(root, 1000, 700));
        window.show();


    }

    public static void main(String[] args) {
        launch(args);
    }

    private void closeAction() throws IOException {
        WBController wbController = new WBController();
        wbController.infoBox("Your changes will be lost if you don't save them.",
                "Do you want to save the changes?", "exit");
        if (wbController.close == true) {
            window.close();
        }
    }

}
