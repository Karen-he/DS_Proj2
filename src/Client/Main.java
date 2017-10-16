package Client;

import ChatBox.ChatClient;
import RMIInterfaces.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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


        Registry registry = LocateRegistry.getRegistry(2020);
        ServerInterface gsonServant = (ServerInterface) registry.lookup("Gson");
        ChatServerInterface chatServant = (ChatServerInterface) registry.lookup("Chatbox");

        // username get from the name after logging in



        new Thread (() -> {
            while(true)
            try {
                if (gsonServant.receivePaints() != null){
                    System.out.println("hihi 我可以画画啦");
                    ArrayList<String> drawCommand = gsonServant.receivePaints();
                    String shapeOption = drawCommand.get(0);
                    String attributeGson = drawCommand.get(1);
                    PaintAttribute attributeRec = gsonServant.getAttribute(attributeGson);
                    WBController.autoPaint(shapeOption,attributeRec);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();

        Runnable client = new ChatClient("Username", chatServant, gsonServant);
        Thread thread1 = new Thread(client);
        thread1.start();

// username get from the name after logging in

        WBController.setServant(gsonServant);

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












    private void closeAction() throws IOException{
        WBController wbController = new WBController();
        wbController.infoBox("Your changes will be lost if you don't save them.",
                 "Do you want to save the changes?","exit");
        if(wbController.close == true) {
            window.close();
        }

    }


}
