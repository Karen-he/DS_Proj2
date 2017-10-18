/***
 * GsonServant serves for information transmission between clients
 */


package Server;

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
    private JsonObject jsonObject = new JsonObject();
    private JsonObject chatObject = new JsonObject();
    private JsonObject paintObject = new JsonObject();
    private String jsonPack;
    private String chatPack;
    private String paintPack;
    private static int paintSequence = 0;
    private PaintsDatabase paintsKeeper;

    public GsonServant() throws RemoteException {
        jsonPack = "";
        chatPack = "";
        paintPack = "";
        this.paintsKeeper = new PaintsDatabase();
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


    public String sendPaints(String keyShape, PaintAttribute paint, String timeStamp) throws RemoteException {
        paintObject.addProperty("Shape", keyShape);
        String sendpaints = gson.toJson(paint);
        System.out.println("sendpaints in servant: " + sendpaints);
        paintObject.addProperty("paintAttribute", sendpaints);
        paintObject.addProperty("timeStamp", timeStamp);
        paintPack = gson.toJson(paintObject);
        System.out.println("the jsonPack in servant" + paintPack);
        return paintPack;
    }

    public ArrayList<String> receivePaints() throws RemoteException {
        boolean empty = paintPack.isEmpty();
        ArrayList<String> whiteBoard = new ArrayList<>();
        if (empty == false) {
            JsonElement jsonElement = new JsonParser().parse(paintPack);
            paintObject = jsonElement.getAsJsonObject();
            if (paintObject.get("Shape") != null) {
                String shape = paintObject.get("Shape").getAsString();
                //System.out.println("shape is " + shape);
                String attribute = paintObject.get("paintAttribute").getAsString();
                String timeStamp = paintObject.get("timeStamp").getAsString();
                //System.out.println("paintAttribute: " + attribute);
                whiteBoard.add(0, shape);
                whiteBoard.add(1, attribute);
                whiteBoard.add(2, timeStamp);

                paintsKeeper.addPaintsDatabase(whiteBoard, paintSequence);
                paintSequence++;
                //System.out.println("the string array is " + whiteBoard.get(0) + " ### " + whiteBoard.get(1));
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
    public synchronized String tellSeverNew(boolean command) throws RemoteException {
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
            if (jsonObject.get("Newcanvase") != null) {
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


    public String sendMessage(String userName, String chatContent, String timestamp) throws RemoteException {
        System.out.println("hou get here 2");
        chatObject.addProperty("Username", userName);
        chatObject.addProperty("Content", chatContent);
        chatObject.addProperty("Timestamp", timestamp);
        chatPack = gson.toJson(chatObject);

        System.out.println(chatPack.toString() + "!!!!!!!");
        return chatPack;
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
        boolean empty = chatPack.isEmpty();
        ArrayList<String> tmp = new ArrayList<String>();
        if (empty == false) {
            JsonElement jsonElement = new JsonParser().parse(chatPack);
            chatObject = jsonElement.getAsJsonObject();
            //unpack json to find username and content
            //retrieve userName
            if (chatObject.get("Username") != null) {
                String user = chatObject.get("Username").getAsString();
                String msgPrint = chatObject.get("Content").getAsString();
                String timestamp = chatObject.get("Timestamp").getAsString();

                tmp.add(0, user);
                tmp.add(1, msgPrint);
                tmp.add(2, timestamp);
            }
        }
        return tmp;
    }

    public String getJsonPack() throws RemoteException {
        String temp = this.jsonPack;
        return temp;
    }
}

