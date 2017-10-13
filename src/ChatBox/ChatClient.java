package ChatBox;

import RMIInterfaces.ChatClientInterface;
import RMIInterfaces.ChatServerInterface;
import RMIInterfaces.ServerInterface;

import java.io.Serializable;
import java.rmi.RemoteException;

public class ChatClient implements ChatClientInterface, Serializable, Runnable{

    //receiveGson include peerID/peer Username, chat content
    //private int peerId;

    private String userName;
    private ChatServerInterface chatServant;
    private ServerInterface gsonServant;


    private String message;


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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //print message on own board
    public void retrieveMsg(String msgPrint) throws RemoteException {
        System.out.println(msgPrint);
//        return msgPrint;
    }

   /*
     * retrieve Msg from server
     * add listener to receive chat content
     */
    public void run() {

        try {
            if (gsonServant.receiveMessage() != null) {
                String chatContent = gsonServant.receiveMessage();
                chatServant.shareMsg(userName, chatContent);
                gsonServant.sendClientList(chatServant.getChatClients());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    }




