package ChatBox;

import RMIInterfaces.ChatClientInterface;
import RMIInterfaces.ChatServerInterface;
import RMIInterfaces.ServerInterface;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ChatClient implements ChatClientInterface, Serializable {

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

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }

    //print chatContent on own board
    public void retrieveMsg(String msgPrint) throws RemoteException {
        System.out.println(msgPrint);
//        return msgPrint;
    }

}
   /*
     * retrieve Msg from server
     * add listener to receive chat content
     */

//    public void run() {
        // Broadcast messgae when input in GUI
//        new Thread(() -> {
//            while(true) {
//                try {
//                    if (gsonServant.receiveMessage() != null) {
//
//                        String chatContent = gsonServant.receiveMessage();
//
//                        System.out.println(chatContent);
//
//                        this.setChatContent(chatContent);
//
////                        chatServant.shareMsg(userName, chatContent);
//                    }
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();


     // show others sent chatContent
//        new Thread(() -> {
//            ArrayList<ChatClient> oldChatClient = null;
//            while(true) {
//                try {
//                    ArrayList<ChatClient> chatClientArrayList = chatServant.getChatClients();
//                    System.out.println("hihihi");
//                    if (chatClientArrayList != null) {
//                        System.out.println("byebyebye");
//                        System.out.println(chatClientArrayList);
//                        for (int i = 0; i < chatClientArrayList.size(); i++) {
//
//                            System.out.println("进入chatClient的list打印啦");
//
//                            ChatClient tempClient = chatClientArrayList.get(i);
//                            String messagePrint = tempClient.getChatContent();
//                            System.out.println("messagePrint: " + messagePrint);
//                            if (messagePrint != null) {
//                                System.out.println("print次数：" + i);
//                                System.out.println(tempClient.getUserName() + "： " + messagePrint);
//                            }
//                        }
//                    }
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//}






//
//        while (true) {
//            try {
//                if (gsonServant.receiveMessage() != null) {
//                    String chatContent = gsonServant.receiveMessage();
//
//                    System.out.println(chatContent);
//
//                    chatServant.shareMsg(userName, chatContent);
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }

            //调出clientList print each one
//            try {
//                ArrayList<ChatClient> chatClientArrayList = chatServant.getChatClients();
//                System.out.println("hihihi");
//                if (chatClientArrayList != null) {
//                    System.out.println("byebyebye");
//                    for (int i = 0; i < chatClientArrayList.size(); i++) {
//
////                        System.out.println("进入chatClient的list打印啦");
//
//                        ChatClient tempClient = chatClientArrayList.get(i);
//                        String messagePrint = tempClient.getChatContent();
////                    setText(messagePrint);
//                        System.out.println(messagePrint);
//                    }
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }        }




