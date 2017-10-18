
/***
 * Interface for Chatbox method
 *
 */

package RMIInterfaces;

import ChatBox.ChatClient;
import java.rmi.*;
import java.util.ArrayList;


public interface ChatServerInterface extends Remote {

    void registerChatClient(ChatClient peer) throws RemoteException;

    ArrayList<ChatClient> getChatClients() throws RemoteException;

    void kickClient(String userName) throws RemoteException;

}
