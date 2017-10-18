package Client;

import ChatBox.ChatClient;
import RMIInterfaces.*;
import Server.UserSysServant;
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
import java.util.concurrent.CountDownLatch;

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
        System.out.println("判断之前："+clientCount);
        if (clientCount == 0) {
            System.out.println("before set clientcount:"+clientCount);
            WBController.setIsManager(true);
            WBController.setClientCount(1);
            System.out.println("after set: "+WBController.getManager());
            System.out.println("after set clientcount:"+clientCount);
        } else if (clientCount > 0 && clientCount < 4) {
            System.out.println("before set clientcount:"+clientCount);
            WBController.setIsManager(false);
            WBController.setClientCount(clientCount + 1);
            System.out.println("after set: "+WBController.getManager());
            System.out.println("clientcount:"+clientCount);
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
            System.out.println("get manager: "+WBController.getManager());
            while (WBController.getManager()) {
                try {
                    sleep(1000);
                    if (userSysServant.listenRequestList() == false){
                        Platform.runLater(new Runnable() {
                            public void run() {
                                try {
                                    WBController.approve(WBController.getUserName());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        WBController.approve(WBController.getUserName());

                    }

                    }
                catch (RemoteException e) {
                    //WBController.errorDialog("Connection Error", "Connection is lost!");
                    //e.printStackTrace();
                } catch (IOException e) {

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        approval.start();

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

        WBController.setServant(gsonServant, chatServant, userSysServant);

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
