package RMIInterfaces;

import ChatBox.ChatClient;
import Client.WBController;

import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface ChatServerInterface extends Remote{

   void registerChatClient(ChatClient peer) throws RemoteException;
   void shareMsg(String userName, String chatContent) throws RemoteException;
   ArrayList<ChatClient> getChatClients() throws RemoteException;
   void kickClient(String userName) throws RemoteException;
   HashMap<String, String> getChatRecords() throws RemoteException;
   void clearRecords() throws RemoteException;
   void printToAll(String chatContent) throws RemoteException;
   void setWbController(ClientServer wbController) throws RemoteException;

}
