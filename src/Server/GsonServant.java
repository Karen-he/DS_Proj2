package Server;

import ChatBox.ChatClient;
import Client.PaintAttribute;
import RMIInterfaces.ServerInterface;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private String jsonpack;

    public GsonServant() throws RemoteException{
        jsonpack = "";
    }

    @Override
    public String sendGson(ArrayList<String> keywords, ArrayList<String> contents) throws RemoteException{
        int k = keywords.size();
        int c = contents.size();
        if(k == c){
            for(int i = 0; i < k;i++){
                jsonObject.addProperty(keywords.get(i), contents.get(i));
            }
        }else{
            System.out.println("num of keywords does not match contents");
        }
        jsonpack = gson.toJson(jsonObject);
        System.out.println("the jsonpack in servant"+jsonpack);
        //i++;
        //System.out.println("the number of command: "+i);
        return jsonpack;
    }

    @Override
    public Hashtable receiveGson() throws RemoteException{
        boolean empty = jsonpack.isEmpty();
        Hashtable commands = new Hashtable();
        if(empty == false){
            System.out.println(jsonpack);
            JsonElement jsonElement = new JsonParser().parse(jsonpack);
            jsonObject = jsonElement.getAsJsonObject();
            Set<String> keywords = jsonObject.keySet();
            for(String i : keywords){
                commands.put(i,jsonObject.get(i).getAsString());
            }
            //System.out.println("back to string: "+actual);
            //j++;
            //System.out.println("the number of command: "+j);
        }
        return commands;

    }

    public String sendPaints(String keyShape, PaintAttribute paint) throws RemoteException{
        jsonObject.addProperty("Shape",keyShape);
        String sendpaints = gson.toJson(paint);
        System.out.println("sendpaints in servant: "+ sendpaints);
        jsonObject.addProperty("paintAttribute", sendpaints);
        jsonpack = gson.toJson(jsonObject);
        System.out.println("the jsonpack in servant"+jsonpack);
        return jsonpack;
    }

    public String[] receivePaints() throws RemoteException{
        boolean empty = jsonpack.isEmpty();
        String[] whiteboard = new String[2];
        whiteboard[0]="";
        whiteboard[1]="";
        if(empty==false){
            JsonElement jsonElement = new JsonParser().parse(jsonpack);
            jsonObject = jsonElement.getAsJsonObject();
            String shape = jsonObject.get("Shape").getAsString();
            System.out.println("shape is "+shape);
            String attribute = jsonObject.get("paintAttribute").getAsString();
            System.out.println("paintAttribute: " + attribute);
            whiteboard[0] = shape;
            whiteboard[1] = attribute;
            System.out.println("the string array is " + whiteboard[0]
                    + " ### " + whiteboard[1]);
        } else{ whiteboard = null;}

        return whiteboard;
    }
    public String sendError(String errorType) throws RemoteException{
        jsonObject.addProperty("Exception", errorType);
        jsonpack = gson.toJson(jsonObject);
        System.out.println("Error catch initiated: " + errorType);
        return jsonpack;
    }

    public String sendMessage(String userName, String chatContent) throws RemoteException{
        jsonObject.addProperty("Username", userName);
        jsonObject.addProperty("InputText", chatContent);
        System.out.println(userName + ": " + chatContent);
        jsonpack = gson.toJson(jsonObject);
        return jsonpack;
    }

    public String receiveMessage() throws RemoteException {
        boolean empty = jsonpack.isEmpty();
        String msgPrint = null;
        if(empty == false) {
            JsonElement jsonElement = new JsonParser().parse(jsonpack);
            jsonObject = jsonElement.getAsJsonObject();
            //unpack json to find username and content
            //retrieve userName
            String userName = jsonObject.get("UserName").getAsString();
            msgPrint = jsonObject.get("Content").getAsString();
            return msgPrint;
        }
        else{
            return msgPrint;
        }
    }
    public String sendClientList(ArrayList<ChatClient> chatClientList) throws RemoteException{
        JsonElement jsonElement = gson.toJsonTree(chatClientList);
        jsonpack = gson.toJson(jsonElement);
        return jsonpack;
    }

    public ArrayList receiveClientList() throws RemoteException{
        boolean empty = jsonpack.isEmpty();
        ArrayList<ChatClient> clientArrayList = null;
        if(empty == false){
            JsonElement jsonElement = new JsonParser().parse(jsonpack);
            clientArrayList = gson.fromJson(jsonElement, ArrayList.class);
        }
        return clientArrayList;
    }
}
