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

    //all chatClients in the list retrieveMsg
    public void shareMsg(String userName, String msgPrint) throws RemoteException {
        String messageToAll;
        for (int i = 0; i < chatClients.size(); i++) {
            System.out.println(chatClients.size());

            if (this.chatClients.get(i).getUserName().equals(userName)) {
                messageToAll = msgPrint;
                this.chatClients.get(i).setMessage(messageToAll);
                System.out.println(messageToAll);
                continue;
            }
            else{
                messageToAll = userName + ": " + msgPrint;
                this.chatClients.get(i).setMessage(messageToAll);
                System.out.println(messageToAll);
            }
        }
    }
    public ArrayList<ChatClient> getChatClients() throws RemoteException{
        return chatClients;
    }

}
