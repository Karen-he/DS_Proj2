package Server;

import ChatBox.ChatClient;
import Client.WBController;
import RMIInterfaces.ChatClientInterface;
import RMIInterfaces.ChatServerInterface;
import RMIInterfaces.ClientServer;
import RMIInterfaces.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


//gsonReceive, return command: chatbox
//unpack gson retrieve userName, chatContent (timeStamp)
public class ChatServant extends UnicastRemoteObject implements ChatServerInterface {

    private ArrayList<ChatClient> chatClients = new ArrayList<ChatClient>();

    private HashMap<String,String> chatRecords = new HashMap();


    private ChatServerInterface wbController;

    //create a peer list by manager, constructor

    public ChatServant() throws RemoteException {
        this.chatClients = new ArrayList<ChatClient>();
    }

    public synchronized void registerChatClient(ChatClient chatClient)
            throws RemoteException {
        //add peer to chat arrayList
        this.chatClients.add(chatClient);
    }

    //set the message that will print out in GUI
    public synchronized void shareMsg(String userName, String msgPrint) throws RemoteException {
        chatRecords.put(userName, msgPrint);
    }
    public ArrayList<ChatClient> getChatClients() throws RemoteException{
        return chatClients;
    }

    
    public void kickClient(String userName) throws RemoteException {
        for (int i = 0; i < chatClients.size(); i++){
            if (chatClients.get(i).getUserName().equals(userName)) {
                chatClients.remove(i);
            }
        }
    }

    public HashMap<String, String> getChatRecords() throws RemoteException {
        return chatRecords;
    }

    public void clearRecords() throws RemoteException{
        chatRecords.clear();
    }

    public void printToAll(String chatContent) throws RemoteException{
        try
        {
            for (int i = 0; i < chatClients.size(); i++) {
                ChatClientInterface tempClient = (ChatClientInterface) chatClients.get(i);
                tempClient.retrieveMsg(chatContent);
            }
        }catch (Exception e){
                e.printStackTrace();
            }
        }

    public void setWbController(ClientServer wbController) throws RemoteException{
        for (int i = 0; i<chatClients.size();i++){
            chatClients.get(i).setWbController(wbController);
    }

    }
}
