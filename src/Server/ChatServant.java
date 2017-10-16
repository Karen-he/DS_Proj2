package Server;

import ChatBox.ChatClient;
import RMIInterfaces.ChatServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


//gsonReceive, return command: chatbox
//unpack gson retrieve userName, chatContent (timeStamp)
public class ChatServant extends UnicastRemoteObject implements ChatServerInterface {

    private ArrayList<ChatClient> chatClients = new ArrayList<ChatClient>();

    private HashMap<String,String> chatRecords = new HashMap();

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

//        String messageToAll;
//        for (int i = 0; i < chatClients.size(); i++) {
//            System.out.println("Total Chat Clients Number: " + chatClients.size());
//
//            // for each clients, set the message supposed to print out in GUI
//            if (chatClients.get(i).getUserName().equals(userName)) {
//                System.out.println("进入client's的loop啦");
//                messageToAll = msgPrint;
//                chatClients.get(i).setChatContent(messageToAll);
//                System.out.println("Message to All: " + messageToAll);
//                continue;
//            }
//        }
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
}
