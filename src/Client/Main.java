package Client;

import ChatBox.ChatClient;
import RMIInterfaces.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main extends Application {

    private static String address;
    private static String port;
    private Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{

            FXMLLoader fLoader = new FXMLLoader(getClass().getResource("whiteBoard.fxml"));

            Parent root = (Parent) fLoader.load();
            WBController WBController = fLoader.getController();


            Registry registry = LocateRegistry.getRegistry(2020);
            ServerInterface gsonServant = (ServerInterface) registry.lookup("Gson");
            ChatServerInterface chatServant = (ChatServerInterface) registry.lookup("Chatbox");

            // username get from the name after logging in
/***
 * Thread
 */
            Runnable client = new ChatClient("Username", chatServant, gsonServant);
            Thread thread1 = new Thread(client);
            thread1.start();


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