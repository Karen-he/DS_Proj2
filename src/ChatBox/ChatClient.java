package ChatBox;

import Client.WBController;
import RMIInterfaces.ChatClientInterface;
import RMIInterfaces.ChatServerInterface;
import RMIInterfaces.ClientServer;
import RMIInterfaces.ServerInterface;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ChatClient implements ChatClientInterface, Serializable  {

    //receiveGson include peerID/peer Username, chat content
    //private int peerId;

    private String userName;
    private ChatServerInterface chatServant;
    private ServerInterface gsonServant;


    private ClientServer wbController;

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
        System.out.println("msgPrint:" +msgPrint);
        setChatContent("msgPrint: " + msgPrint);
    }


}

   /*
     * retrieve Msg from server
     * add listener to receive chat content
     */

//    public void run() {
//        // Broadcast messgae when input in GUI
//        new Thread(() -> {
//            while (true) {
//                try {
//                    if (gsonServant.receiveMessage() != null) {
//
//                        String chatContent = gsonServant.receiveMessage();
//
//                        System.out.println(chatContent);
//
//                        chatServant.shareMsg(userName, chatContent);
//                    }
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }


//
//
//    public void printAll{
//            new Thread(() -> {
//                try {
//
//                    HashMap<String, String> chatRecords = chatServant.getChatRecords();
//                    System.out.println("hihihi");
//
//                    if (chatRecords != null) {
//
//                        System.out.println("byebyebye");
//
//                        System.out.println(chatRecords);
//
//                        Iterator it = chatRecords.entrySet().iterator();
//
//                        while (it.hasNext()) {
//
//                            HashMap.Entry pair = (HashMap.Entry) it.next();
//
//                            System.out.println("进入chatClient的list打印啦");
//
//                            String messagePrint = pair.getKey() + ": " + pair.getValue();
//
//                            WBController.appendToMessage(messagePrint);
//                        }
//                        chatServant.clearRecords();
//                    }
//
//
//            }
//        new Thread(() -> {
//            while(true) {
//                try {
//                    ArrayList<ChatClient> chatClientArrayList = chatServant.getChatClients();
//                    System.out.println("hihihi");
//                    if (chatClientArrayList != null) {
//                        System.out.println("byebyebye");
//                        for (int i = 0; i < chatClientArrayList.size(); i++) {
//
////                          System.out.println("进入chatClient的list打印啦");
//
//                            ChatClient tempClient = chatClientArrayList.get(i);
//                            String messagePrint = tempClient.getMessage();
//
////                          setText(messagePrint);
//                          System.out.println("print次数：" + i);
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
//                        String messagePrint = tempClient.getMessage();
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




