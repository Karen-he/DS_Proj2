package Server;

import RMIInterfaces.UserSysInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class UserSysServant extends UnicastRemoteObject implements UserSysInterface {
    private ArrayList<String> approvalRequistList;
    private HashMap<String,Boolean> approveResult;

    public boolean listenRequestList() throws RemoteException{
        boolean empty = approvalRequistList.isEmpty();
        return empty;
    }

    public

}
