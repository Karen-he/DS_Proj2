package RMIInterfaces;

import ChatBox.ChatClient;
import Client.PaintAttribute;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote{
    String sendGson(String keyword, String content) throws RemoteException;
    String receiveGson()throws RemoteException;
    String sendMessage(String userName, String chatContent) throws RemoteException;
    String receiveMessage() throws RemoteException;
    String sendClientList(ArrayList<ChatClient> chatClientList) throws RemoteException;
    ArrayList receiveClientList() throws RemoteException;


    String sendPaints(String line, PaintAttribute attribute) throws RemoteException;
//    String sendError(String errorType) throws RemoteException;


}
