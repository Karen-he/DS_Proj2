package Server;

import ChatBox.ChatClient;
import Client.PaintAttribute;
import RMIInterfaces.ServerInterface;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

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

    public GsonServant() throws RemoteException{
        jsonPack = "";
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
        jsonPack = gson.toJson(jsonObject);
        System.out.println("the jsonPack in servant"+ jsonPack);
        //i++;
        //System.out.println("the number of command: "+i);
        return jsonPack;
    }

    @Override
    public Hashtable receiveGson() throws RemoteException{
        boolean empty = jsonPack.isEmpty();
        Hashtable commands = new Hashtable();
        if(empty == false){
            System.out.println(jsonPack);
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
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
        jsonPack = gson.toJson(jsonObject);
        System.out.println("the jsonPack in servant"+ jsonPack);
        return jsonPack;
    }

    public String[] receivePaints() throws RemoteException{
        boolean empty = jsonPack.isEmpty();
        String[] whiteboard = new String[2];
        whiteboard[0]="";
        whiteboard[1]="";
        if(empty==false){
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
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
        jsonPack = gson.toJson(jsonObject);
        System.out.println("Error catch initiated: " + errorType);
        return jsonPack;
    }

    public synchronized String sendMessage(String userName, String chatContent) throws RemoteException{
        jsonObject.addProperty("Username", userName);
        jsonObject.addProperty("Content", chatContent);
//        System.out.println(userName + ": " + chatContent);
        jsonPack= gson.toJson(jsonObject);
        return jsonPack;
    }

    public String receiveMessage() throws RemoteException {
        boolean empty = jsonPack.isEmpty();
        String msgPrint = "";
        if(empty == false) {
            JsonElement jsonElement = new JsonParser().parse(jsonPack);
            jsonObject = jsonElement.getAsJsonObject();
            //unpack json to find username and content
            //retrieve userName
//            String userName = jsonObject.get("UserName").getAsString();
            msgPrint = jsonObject.get("Content").getAsString();
        }
        return msgPrint;
    }
    public String sendClientList(ArrayList<ChatClient> chatClientList) throws RemoteException{
        jsonPack = gson.toJson(chatClientList);
        return jsonPack;
    }

    public ArrayList receiveClientList() throws RemoteException{
        boolean empty = jsonPack.isEmpty();
        ArrayList<ChatClient> clientArrayList = null;
        if(empty == false){
            clientArrayList = gson.fromJson(jsonPack, new TypeToken<ArrayList<ChatClient>>(){}.getType());
        }
        return clientArrayList;
    }
}
