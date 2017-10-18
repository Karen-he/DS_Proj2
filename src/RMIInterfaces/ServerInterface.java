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

    //Serve for note broadcasting
    String sendNote(String noteType, String userName) throws RemoteException;

    String receiveNote() throws RemoteException;

    String tellNewCanvas(boolean command, String timeStamp) throws RemoteException;

    ArrayList<String> checkNewCanvas() throws RemoteException;

    //log in
    void checkPassword(String userName, String password) throws RemoteException;

    boolean logginResult() throws RemoteException;

    //manager
    void sendApproval(boolean approval) throws RemoteException;

    String listenForApproval() throws RemoteException;

    //sign up
    void registerUser(String userName, String password) throws RemoteException;

    boolean validRegister() throws RemoteException;

    String getJsonPack() throws RemoteException;

}
