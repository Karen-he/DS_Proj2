package RMIInterfaces;

import ChatBox.ChatClient;
import Client.PaintAttribute;
import Client.WBController;
import com.sun.org.apache.regexp.internal.RE;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

public interface ServerInterface extends Remote {
    String sendGson(ArrayList<String> keywords, ArrayList<String> contents) throws RemoteException;

    Hashtable receiveGson() throws RemoteException;

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

    String tellSeverNew(boolean command) throws RemoteException;

    boolean serverCheckNew() throws RemoteException;

    //log in
    void checkPassword(String userName, String password) throws RemoteException;

    boolean logginResult() throws RemoteException;

    //manager
    void sendApproval(boolean approval) throws RemoteException;

    String listenForApproval() throws RemoteException;

    //sign up
    void registerUser(String userName, String password) throws RemoteException;

    boolean validRegister() throws RemoteException;

<<<<<<< HEAD
    String getJsonPack() throws RemoteException;
=======
>>>>>>> 39cf79aa30500fc31b5de9ba0eb8e0d80dce2c1b
}
