package Server;

import ChatBox.ChatClient;
import Client.PaintAttribute;
import RMIInterfaces.ServerInterface;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.scene.layout.BorderPane;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class GsonServant extends UnicastRemoteObject implements ServerInterface {
    private Gson gson = new Gson();
    //private int i;
    //private int j;
    private JsonObject jsonObject = new JsonObject();
    private String jsonPack;
//    private String chatPack;

    public GsonServant() throws RemoteException {
        jsonPack = "";
    }

    @Override
    public String sendGson(ArrayList<String> keywords, ArrayList<String> contents) throws RemoteException {
        int k = keywords.size();
        int c = contents.size();
        if (k == c) {
            for (int i = 0; i < k; i++) {
                jsonObject.addProperty(keywords.get(i), contents.get(i));
            }
        } else {
            System.out.println("num of keywords does not match contents");
        }
        jsonPack = gson.toJson(jsonObject);
        System.out.println("the jsonPack in servant" + jsonPack);
        return jsonPack;
    }

    @Override
    public Hashtable receiveGson() throws RemoteException {
        boolean empty = jsonPack.isEmpty();
        Hashtable commands = new Hashtable();
        if (empty == false) {
            //System.out.println(jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            Set<String> keywords = jsonObject.keySet();
            for (String i : keywords) {
                commands.put(i, jsonObject.get(i).getAsString());
            }
        }
        return commands;
    }


    public String sendPaints(String keyShape, PaintAttribute paint) throws RemoteException {
        jsonObject.addProperty("Shape", keyShape);
        String sendpaints = gson.toJson(paint);
        System.out.println("sendpaints in servant: " + sendpaints);
        jsonObject.addProperty("paintAttribute", sendpaints);
        jsonPack = gson.toJson(jsonObject);
        System.out.println("the jsonPack in servant" + jsonPack);
        return jsonPack;
    }


    public ArrayList<String> receivePaints() throws RemoteException {
        boolean empty = jsonPack.isEmpty();
        //System.out.println("isEmpty = " + empty);
        ArrayList<String> whiteBoard = new ArrayList<>();
        if (empty == false) {
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("Shape")!=null){
                String shape = jsonObject.get("Shape").getAsString();
                System.out.println("shape is " + shape);
                String attribute = jsonObject.get("paintAttribute").getAsString();
                System.out.println("paintAttribute: " + attribute);

                whiteBoard.add(0, shape);
                whiteBoard.add(1, attribute);
                System.out.println("the string array is " + whiteBoard.get(0) + " ### " + whiteBoard.get(1));
            }
        }
        return whiteBoard;

    }

    /***
     *
     * @param command
     * @return
     * @throws RemoteException
     *
     *
     * send a command of initilize a new canvas to the server in order to clear the paints database.
     */
    public synchronized String tellSeverNew(boolean command) throws RemoteException{
        jsonObject.addProperty("Newcanvase", command);
        jsonPack = gson.toJson(jsonObject);
        return jsonPack;
    }

    //Server uses this method to monitor whether a new canvas is crated.
    public boolean serverCheckNew() throws RemoteException {
        boolean empty = jsonPack.isEmpty();
        boolean command = false;
        if (empty == false) {
            //System.out.println(jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("Newcanvase") != null) {
                command = jsonObject.get("Newcanvase").getAsBoolean();
            }
            //System.out.println("back to string: "+actual);
            //j++;
            //System.out.println("the number of command: "+j);
        }
        return command;
    }

    public PaintAttribute getAttribute(String attribute) throws RemoteException {
        PaintAttribute attributeRec = gson.fromJson(attribute, PaintAttribute.class);
        return attributeRec;

    }


    public synchronized String sendMessage(String userName, String chatContent) throws RemoteException {
        jsonObject.addProperty("Username", userName);
        jsonObject.addProperty("Content", chatContent);
        jsonPack = gson.toJson(jsonObject);
        return jsonPack;
    }

    /***
     *
     * @return
     * @throws RemoteException
     *
     * Get messages sent from others
     *
     */


    public ArrayList<String> receiveMessage() throws RemoteException {
        boolean empty = jsonPack.isEmpty();
        ArrayList<String> tmp = new ArrayList<String>();
        if (empty == false) {
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            //unpack json to find username and content
            //retrieve userName
            if(jsonObject.get("Username") != null) {
                String user = jsonObject.get("Username").getAsString();
                String msgPrint = jsonObject.get("Content").getAsString();

                tmp.add(0, user);
                tmp.add(1, msgPrint);
            }
        }
        return tmp;
    }

    /***
     *
     * @param noteType
     * @param userName
     * @return
     * @throws RemoteException
     *
     * Broadcast the notification of any member left the room
     * NoteType: 1) whiteBoard disband - userName = manager name
     *           2) Member left room （kickoff or volunteer)
     *
     */
    public String sendNote(String noteType, String userName) throws RemoteException {
        jsonObject.addProperty("NoteType", noteType);
        jsonObject.addProperty("Username", userName);
        jsonPack = gson.toJson(jsonObject);
        System.out.println("Notification Send out: " + noteType);
        return jsonPack;
    }

    /***
     *
     * @return
     * @throws RemoteException
     *
     * show notification info on dialog window
     */
    public String receiveNote() throws RemoteException {
        boolean empty = jsonPack.isEmpty();
        String notePrint = "";
        if (empty == false) {
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("NoteTyple") != null) {
                String noteType = jsonObject.get("NoteTyple").getAsString();
                String userName = jsonObject.get("Username").getAsString();
                notePrint = userName + " has left room.";
            }
        }
        return notePrint;
    }

    //log in system
    //server uses this method to get the password from client when logging in
    public String serverCheckPassword() {
        boolean empty = jsonPack.isEmpty();
        String command = "";
        if (empty == false) {
            //System.out.println(jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("checkPassword") != null) {
                command = jsonObject.get("checkPassword").getAsString();
            }
            //System.out.println("back to string: "+actual);
            //j++;
            //System.out.println("the number of command: "+j);
        }
        return command;
    }

    /***
     *
     * @param valid
     * @return
     * server uses this method to tell the client if this log is valid
     */
   
    public String validLogin(boolean valid) {
        jsonObject.addProperty("validLogin", valid);
        jsonPack = gson.toJson(jsonObject);
        System.out.println("the jsonpack in servant" + jsonPack);
        //i++;
        //System.out.println("the number of command: "+i);
        return jsonPack;
    }

    //clients use this method to check if the password is valid
    @Override
    public void checkPassword(String userName, String password)throws RemoteException {
        String userNameAndPassword = userName + " " + password;
        jsonObject.addProperty("checkPassword", userNameAndPassword);
        jsonPack = gson.toJson(jsonObject);
        System.out.println("the jsonpack in servant" + jsonPack);
    }
    //clients use this method to receive the result of loggin
    @Override
    public boolean logginResult()throws RemoteException{
        boolean empty = jsonPack.isEmpty();
        boolean command = false;
        if (empty == false) {
            //System.out.println(jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("validLogin")!=null){
                command = jsonObject.get("validLogin").getAsBoolean();
            }

            //System.out.println("back to string: "+actual);
            //j++;
            //System.out.println("the number of command: "+j);
        }
        return command;
    }

    /***
     * manager approve
     * server uses thismethod to ask manager to approve log in
     * @param username
     */

    public void askManager(String username){
        jsonObject.addProperty("askManager", username);
        jsonPack = gson.toJson(jsonObject);
        System.out.println("the jsonpack in servant" + jsonPack);
    }

    /***
     *
     * @param approval
     * @throws RemoteException
     * manager uses this method to send approval
     */
    @Override
    public void sendApproval(boolean approval)throws RemoteException{
        jsonObject.addProperty("approvalFromManager", approval);
        jsonPack = gson.toJson(jsonObject);
        System.out.println("the jsonpack in servant" + jsonPack);
    }

    /***
     * server uses this method to receive the approval from manager
     * @return
     */

    public boolean waitForManager(){
        boolean empty = jsonPack.isEmpty();
        boolean command = false;
        if (empty == false) {
            //System.out.println(jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("approvalFromManager") != null) {
                command = jsonObject.get("approvalFromManager").getAsBoolean();
            }
            //System.out.println("back to string: "+actual);
            //j++;
            //System.out.println("the number of command: "+j);
        }
        return command;
    }

    /***
     *
     * @return
     * @throws RemoteException
     * manager uses this to listen request of approval
     */

    @Override
    public String listenForApproval()throws RemoteException{
        boolean empty = jsonPack.isEmpty();
        String username = "";
        if (empty == false) {
            //System.out.println(jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("askManager")!=null){
                username = jsonObject.get("askManager").getAsString();
            }
        }
        return username;
    }


    /***
     *
     * @return
     * sign up system
     * server uses this method to add user into the system
     */
    public String addUser() {
        boolean empty = jsonPack.isEmpty();
        String command = "";
        if (empty == false) {
            //System.out.println(jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("registerUser") != null) {
                command = jsonObject.get("registerUser").getAsString();
            }
        }
        return command;
    }

    /***
     *
     * @param userName
     * @param password
     * @throws RemoteException
     * client uses this method to register into the system and ckeck this register
     */
    @Override
    public void registerUser(String userName, String password)throws RemoteException {
        String userNameAndPassword = userName + " " + password;
        jsonObject.addProperty("registerUser", userNameAndPassword);
        jsonObject.addProperty("checkRegister", userName);
        jsonPack = gson.toJson(jsonObject);
    }

    /***
     *
     * @param valid
     * server uses this method to send if this client exist in system
     */

    public void serverValidRegister(boolean valid) {
        jsonObject.addProperty("checkExist", valid);
        jsonPack = gson.toJson(jsonObject);
        //System.out.println("the jsonpack in servant" + jsonPack);
    }
    //clients use this method to receive if this register is valid
    @Override
    public boolean validRegister()throws RemoteException{
        boolean empty = jsonPack.isEmpty();
        System.out.println("in valid Register:"+jsonPack);
        boolean command = false;
        if (empty == false) {
            //System.out.println("in valid Register and jsonpack is not empty:"+jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("checkExist")!=null){
                command = jsonObject.get("checkExist").getAsBoolean();
            }
            //System.out.println("back to string: "+actual);
            //j++;
            //System.out.println("the number of command: "+j);
        }
        return command;
    }

    public String getJsonPack() throws RemoteException{
        String temp = this.jsonPack;
        return temp;
    }
}

//    public String sendClientList(ArrayList<ChatClient> chatClientList) throws RemoteException{
//        jsonPack = gson.toJson(chatClientList);
//        return jsonPack;
//    }
//
//    public ArrayList receiveClientList() throws RemoteException{
//        boolean empty = jsonPack.isEmpty();
//        ArrayList<ChatClient> clientArrayList = null;
//        if(empty == false){
//            clientArrayList = gson.fromJson(jsonPack, new TypeToken<ArrayList<ChatClient>>(){}.getType());
//        }
//        return clientArrayList;
//    }
//}
