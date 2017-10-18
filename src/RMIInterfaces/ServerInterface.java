package RMIInterfaces;

import Client.PaintAttribute;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;

public interface ServerInterface extends Remote {
    String sendGson(ArrayList<String> keywords, ArrayList<String> contents) throws RemoteException;

    Hashtable receiveGson() throws RemoteException;

    //Serve for chatbox
    String sendMessage(String userName, String chatContent,String timestamp) throws RemoteException;

    ArrayList<String> receiveMessage() throws RemoteException;

    //Serve for drawing
    String sendPaints(String line, PaintAttribute attribute, String timestamp) throws RemoteException;

    ArrayList<String> receivePaints() throws RemoteException;

    PaintAttribute getAttribute(String attribute) throws RemoteException;

    String tellNewCanvas(boolean command, String timeStamp) throws RemoteException;

    ArrayList<String > checkNewCanvas() throws RemoteException;

    String getJsonPack() throws RemoteException;

}
