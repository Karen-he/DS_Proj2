package Server;

import ChatBox.ChatClient;
import RMIInterfaces.ChatServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


//gsonReceive, return command: chatbox
//unpack gson retrieve userName, chatContent (timeStamp)
public class ChatServant extends UnicastRemoteObject implements ChatServerInterface {

    private ArrayList<ChatClient> chatClients = new ArrayList<ChatClient>();

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
        String messageToAll;
        for (int i = 0; i < chatClients.size(); i++) {
            System.out.println("Total Chat Clients Number: "+chatClients.size());

    // for each clients, set the message supposed to print out in GUI
            if (chatClients.get(i).getUserName().equals(userName)) {
//                System.out.println("进入client's的loop啦");
                messageToAll = msgPrint;
                chatClients.get(i).setMessage(messageToAll);
                System.out.println("Message to All: "+ messageToAll);
//                continue;
            }
            else{
                messageToAll = userName + ": " + msgPrint;
                chatClients.get(i).setMessage(messageToAll);
                System.out.println("Message to All: "+ messageToAll);
            }
        }
    }
    public ArrayList<ChatClient> getChatClients() throws RemoteException{
        return chatClients;
    }

}
