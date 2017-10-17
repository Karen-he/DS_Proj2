package Server;


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
    private Hashtable<Integer, String> userPassword;
    private Hashtable<Integer, User> userData;
    private int currentUserNum;
    private int numUser;
    private static int paintSequence = 0;
    private static int port = 3000;
    private static int counter = 0;


    public Server() {
        this.numUser = 0;
        this.userData = new Hashtable<Integer, User>();
        this.userPassword = new Hashtable<>();
        this.currentUserNum = 0;
    }

    public static void main(String[] args) {
        try {

            Server mainserver = new Server();
            //read in users
            mainserver.readInUsers("users.txt", mainserver.userData);
            //read in passwords
            mainserver.readInPassword("password.txt", mainserver.userPassword);
            //Export the remote math object to the Java RMI runtime so that it
            //can receive incoming remote calls.
            //Because RemoteMath extends UnicastRemoteObject, this
            //is done automatically when the object is initialized.
            GsonServant gsonServant = new GsonServant();
            ChatServant chatServant = new ChatServant();

            //Publish the remote object's stub in the registry under the name "Compute"
            Registry registry = LocateRegistry.createRegistry(2020);
            registry.bind("Gson", gsonServant);
            registry.bind("Chatbox", chatServant);

            System.out.println("ServerInterface ready");

            System.out.println("Waiting for client connection..");


            //gson
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();

            boolean run = true;
            new Thread (() -> {
                while (run) {
                    try {
                        if (!gsonServant.receivePaints().isEmpty()) {
                            Hashtable paintsContainer = new Hashtable();
                            PaintsDatabase paintsKeeper = new PaintsDatabase(paintsContainer);
                            ArrayList<String> whiteboard = gsonServant.receivePaints();
                            paintsKeeper.setPaintsDatabase(paintsContainer, whiteboard, paintSequence);
                            paintSequence = paintSequence + 1;
                            String wb0 = whiteboard.get(0);
                            String wb1 = whiteboard.get(1);
                            if (wb0.equals("") || wb1.equals("")) {
                                System.out.println("empty jsonPack");
                            } else {
                                System.out.println("the string array received in server: " + wb0
                                        + " ### " + wb1);
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // The thread for monitoring whether a new canvas is created.
            new Thread (() -> {
                while (run) {
                    try {
                        if (gsonServant.serverCheckNew()) {
                            Hashtable paintsContainer = new Hashtable();
                            PaintsDatabase paintsKeeper = new PaintsDatabase(paintsContainer);
                            paintsKeeper.clearDatabase(paintsContainer);
                            paintSequence = 0;
                        } else {
                            // System.out.println("No new canvas is created");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //user system thread
            //keep listening
            new Thread (() -> {
                while (run) {
                    //log in: chaeck password and ask manager to approve
                    String userNameAndPassword = gsonServant.serverCheckPassword();
                    Set<Integer> userIDs = mainserver.userData.keySet();
                    boolean empty = userNameAndPassword.isEmpty();
                    if(empty!=true){
                        String[] split = userNameAndPassword.split(" ");
                        String userName = split[0];
                        String password = split[1];
                        String actualPassword = "";
                        for (Integer id : userIDs) {
                            if (mainserver.userData.get(id).equals(userName)) {
                                actualPassword = mainserver.userPassword.get(id);
                            }
                        }
                        boolean validPassword = actualPassword.equals(password);
                        boolean managerApproved = false;
                        if(mainserver.currentUserNum!=0){
                            gsonServant.askManager(userName);
                            managerApproved = gsonServant.waitForManager();
                        }else{
                            managerApproved = true;
                        }
                        boolean validPasswordAndManagerAproved = validPassword && managerApproved;
                        gsonServant.validLogin(validPasswordAndManagerAproved);
                        mainserver.currentUserNum ++;
                    }
                }
            }).start();

            //add new users to the system
            new Thread (() -> {
                while (true){
                    Set<Integer> userIDs = mainserver.userData.keySet();
                    String userNamePlusPassword = gsonServant.addUser();
                    boolean notAlreadyExist = true;
                    boolean emptyUP = userNamePlusPassword.isEmpty();
                    if(emptyUP!=true){
                        String[] splitIt = userNamePlusPassword.split(" ");
                        String username = splitIt[0];
                        String passWord = splitIt[1];
                        for (Integer id : userIDs) {
                            if (mainserver.userData.get(id).equals(username)){
                                notAlreadyExist = false;
                                break;
                            }
                        }
                        if(notAlreadyExist){
                            mainserver.addInUser(username, passWord);
                            gsonServant.serverValidRegister(true);
                        }else{
                            gsonServant.serverValidRegister(false);
                        }
                    }
                }
            }).start();


            //The server will continue running as long as there are remote objects exported into
            //the RMI runtime, to remove remote objects from the
            //RMI runtime so that they can no longer accept RMI calls you can use:
            // UnicastRemoteObject.unexportObject(remoteMath, false);
            //write out users
            mainserver.saveUsers(mainserver.userData, mainserver.userPassword, "users.txt", "password.txt");

                /*
                String temp = keybord.nextLine();
                System.out.println("input from keyboard: "+temp);
                if(temp.equals("exit")){
                    run = false;
                }else{
                    String output = gsonServant.sendGson(temp);
                    System.out.println("output: "+output);
                }*/

        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readInUsers(String filename, Hashtable userData) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        while (true) {
            String user = input.readLine();
            if (user != null) {
                String[] info = user.split(",");
                int userID = Integer.parseInt(info[0]);
                String username = info[1];
                boolean managerTag = Boolean.parseBoolean(info[2]);
                User currentUser = new User(username, userID, managerTag);
                userData.put(userID, currentUser);
            } else {
                break;
            }
        }
        numUser = userData.size();
    }

    private void readInPassword(String filename, Hashtable userPassword) throws IOException{
        Hashtable<Integer, String> userSystem = new Hashtable<Integer, String>();
        BufferedReader pwInput = new BufferedReader(new FileReader(filename));
        while(true){
            String line = pwInput.readLine();
            if(line!=null){
                String[] info = line.split(",");
                int userID = Integer.parseInt(info[0]);
                String password = info[1];
                userPassword.put(userID, password);
            } else{ break; }
        }
    }

    private void saveUsers(Hashtable userData, Hashtable userPassword, String userDataFile, String userPasswordFile) throws FileNotFoundException {
        //save user data
        PrintWriter output = new PrintWriter(new FileOutputStream(userDataFile));
        Set userIDList = userData.keySet();
        for(Object i : userIDList){
            output.println((userData.get(i).toString()));
        }
        output.flush();
        output.close();

        //save password
        PrintWriter outputp = new PrintWriter(new FileOutputStream(userDataFile));
        Set userPasswordList = userPassword.keySet();
        for(Object i : userPasswordList){
            outputp.println((userPassword.get(i).toString()));
        }
        outputp.flush();
        outputp.close();
    }

    //add user in the server
    private void addInUser(String username, String password){
        User currentUser = new User(username);
        currentUser.setUserID(this.numUser);
        this.userData.put(currentUser.getUserID(), currentUser);
        this.userPassword.put(currentUser.getUserID(), password);
        this.numUser++;
    }
}

