package RMIInterfaces;

import ChatBox.ChatClient;
import Client.PaintAttribute;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

public interface ServerInterface extends Remote{
    String sendGson(ArrayList<String> keywords, ArrayList<String> contents) throws RemoteException;
    Hashtable receiveGson()throws RemoteException;

    //Serve for chatbox
    String sendMessage(String userName, String chatContent) throws RemoteException;
    String receiveMessage() throws RemoteException;

//    String sendClientList(ArrayList<ChatClient> chatClientList) throws RemoteException;
//    ArrayList receiveClientList() throws RemoteException;

    //Serve for drawing
    String sendPaints(String line, PaintAttribute attribute) throws RemoteException;
    ArrayList<String> receivePaints() throws RemoteException;
    PaintAttribute getAttribute(String attribute) throws RemoteException;

    //Serve for note broadcasting
    String sendNote(String noteType, String userName) throws RemoteException;
    String receiveNote() throws RemoteException;

    void kickClient(String userName) throws RemoteException;

}
