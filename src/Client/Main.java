package Client;

import ChatBox.ChatClient;
import RMIInterfaces.*;
import Server.UserSysServant;
import com.google.gson.Gson;
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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
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
        UserSysInterface userSysServant = (UserSysInterface) registry.lookup(("UserSys"));
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
         * This thread is to monitor whether there is a new user want to join the whiteboard.
         */

        Thread approval = new Thread(() -> {
            while (WBController.getManager()) {
                try {
                    sleep(9000);
                    String jsonPack = userSysServant.listenRequestList();
                    JsonElement jsonElement = new JsonParser().parse(jsonPack);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if(!jsonObject.get("isEmpty").isJsonNull()){
                        boolean empty = jsonObject.get("isEmpty").getAsBoolean();
                        if (empty == false){
                            Platform.runLater(new Runnable(){
                                public void run() {
                                    try {
                                        String userName = jsonObject.get("userName").getAsString();
                                        WBController.approve(userName);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
                catch (RemoteException e) {
                    //WBController.errorDialog("Connection Error", "Connection is lost!");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        approval.start();

        Thread kick = new Thread(() -> {
            while (!WBController.getManager()){
                try {
                    sleep(9000);
                    if(userSysServant.checkKick(WBController.getUserName())){
                        Platform.runLater(new Runnable(){
                            public void run() {
                                try {
                                    WBController.onExit();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        kick.start();

//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(new Runnable() {
//            public void run() {
//                System.out.println("get manager: "+WBController.getManager());
//                while (WBController.getManager()) {
//                    try {
//                        sleep(1000);
//                        if (userSysServant.listenRequestList() == false){
//                            WBController.approve(WBController.getUserName());
//
//                        }
//
//                    }
//                    catch (RemoteException e) {
//                        //WBController.errorDialog("Connection Error", "Connection is lost!");
//                        //e.printStackTrace();
//                    } catch (IOException e) {
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        });
        //latch.await();

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
                        //System.out.println(timestamp);
                        if (!oriTimestamp.equals(timestamp)) {
                            String fullPrint = tmp.get(1);
                            //System.out.println(userName + fullPrint);
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

        WBController.setServant(gsonServant, chatServant,userSysServant);


        window = primaryStage;
        window.setTitle("WhiteBoard");

        window.setOnCloseRequest(e -> {
            Platform.exit();

        });
        window.setScene(new Scene(root, 1000, 700));
        window.show();


    }

    public static void main(String[] args) {
        launch(args);
    }



}
