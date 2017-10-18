package ChatBox;

import RMIInterfaces.ChatServerInterface;

import RMIInterfaces.ServerInterface;
import java.io.Serializable;
import java.rmi.RemoteException;


public class ChatClient implements Serializable  {

    //receiveGson include peerID/peer Username, chat content
    //private int peerId;

    private String userName;
    private ChatServerInterface chatServant;
    private ServerInterface gsonServant;
    private String chatContent;


    // chat register with userName and server
    public ChatClient(String userName, ChatServerInterface chatServant, ServerInterface gsonServant) throws RemoteException {
        this.userName = userName;
        this.chatServant = chatServant;
        this.gsonServant = gsonServant;
        chatServant.registerChatClient(this);
    }

    public String getUserName() {
        return userName;
    }
}






