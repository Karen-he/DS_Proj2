package RMIInterfaces;

import ChatBox.ChatClient;

import java.rmi.*;
import java.util.ArrayList;

public interface ChatServerInterface extends Remote{

   void registerChatClient(ChatClient peer) throws RemoteException;
   void shareMsg(String userName, String chatContent) throws RemoteException;
   ArrayList<ChatClient> getChatClients() throws RemoteException;


}
