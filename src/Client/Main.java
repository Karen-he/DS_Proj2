package Client;

import RMIInterfaces.*;
import javafx.application.Application;
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

public class Main extends Application {

    private static String ip = "localhost";
    private static int port = 3000;
    private Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception {


            FXMLLoader fLoader = new FXMLLoader(getClass().getResource("whiteBoard.fxml"));

            Parent root = (Parent) fLoader.load();
            WBController WBController = fLoader.getController();
        try {
            Registry registry = LocateRegistry.getRegistry(2020);
            ServerInterface gsonServant = (ServerInterface) registry.lookup("Gson");
            ChatServerInterface chatServant = (ChatServerInterface) registry.lookup("Chatbox");



//            chatServant.setWbController(WBController);
//        new Thread (() -> {
//            while(true) {
//                try {
//                    if (gsonServant.receivePaints() != null) {
//                        System.out.println("hihi 我可以画画啦");
//                        ArrayList<String> drawCommand = gsonServant.receivePaints();
//                        String shapeOption = drawCommand.get(0);
//                        String attributeGson = drawCommand.get(1);
//                        PaintAttribute attributeRec = gsonServant.getAttribute(attributeGson);
//                        WBController.autoPaint(shapeOption, attributeRec);
//                    }
//                } catch (RemoteException e) {
//                    //WBController.errorDialog("Connection Error", "Connection is lost!");
//                    //e.printStackTrace();
//                }
//            }
//        }).start();

        // This thread is to monitor whether there is a new user want to join the whiteboard.
            new Thread (() -> {
                while(true) {
                    try {
                        if (gsonServant.listenForApproval() != null) {
//                            WBController.approve(gsonServant.listenForApproval(), 3);

                        }
                    } catch (RemoteException e) {
                        //WBController.errorDialog("Connection Error", "Connection is lost!");
                        //e.printStackTrace();
                    } catch (IOException e) {

                    }
                }
            }).start();


//        new Thread(() -> {
//            while(true) {
//                ArrayList<ChatClient> tmpChatList = null;
//                try {
//                    tmpChatList = chatServant.getChatClients();
//                    for (int i = 0; i < tmpChatList.size(); i++) {
//                        ChatClient tempClient = tmpChatList.get(i);
//                        tempClient.retrieveMsg(WBController.getMessage());
//                    }
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();



//
//        new Thread(() -> {
//            while(true) {
//                try {
//                    chatServant.printToAll(WBController.getUserName(), WBController.getMessage());
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();


//            while(true) {
//                try {
//
//                    HashMap<String, String> chatRecords = chatServant.getChatRecords();
//                    System.out.println("hihihi");
//
//                    if (chatRecords != null) {
//
//                        System.out.println("byebyebye");
//
//                        System.out.println(chatRecords);
//
//                        Iterator it = chatRecords.entrySet().iterator();
//
//                        while (it.hasNext()) {
//
//                            HashMap.Entry pair = (HashMap.Entry) it.next();
//
//                            System.out.println("进入chatClient的list打印啦");
//
//                            String messagePrint = pair.getKey() + ": " + pair.getValue();
//
//                            WBController.appendToMessage(messagePrint);
//                        }
//
//                    }
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

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
        }catch(ConnectException e){ // This RemoteException is for the lookup and Register.
                //e.printStackTrace(); // When the server has not started and a client want to connect,
                // Here is that place to catch the Exception
                WBController.errorDialog("Connection Error", "Connection is lost!" );
        }

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
