package Server;


import RMIInterfaces.UserSysInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import javax.net.ServerSocketFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

public class Server {

    private static int paintSequence = 0;
    private static int port = 3000;
    private static int counter = 0;


    public Server() {
    }

    public static void main(String[] args) {
        try {

            Server mainserver = new Server();

            //Export the remote math object to the Java RMI runtime so that it
            //can receive incoming remote calls.
            //Because RemoteMath extends UnicastRemoteObject, this
            //is done automatically when the object is initialized.

            GsonServant gsonServant = new GsonServant();
            ChatServant chatServant = new ChatServant();
            UserSysInterface userSysServant = new UserSysServant();

            //Publish the remote object's stub in the registry under the name "Compute"
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
            registry.bind("Gson", gsonServant);
            registry.bind("Chatbox", chatServant);
            registry.bind("UserSys", userSysServant);

            System.out.println("ServerInterface ready");

            System.out.println("Waiting for client connection..");


        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }  catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

