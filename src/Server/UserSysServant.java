package Server;

import RMIInterfaces.UserSysInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class UserSysServant extends UnicastRemoteObject implements UserSysInterface {
    private ArrayList<String> approvalRequistList;
    private HashMap<String,Integer> approveResult;

    protected UserSysServant() throws RemoteException {
        this.approveResult = new HashMap<>();
        this.approvalRequistList = new ArrayList<>();
    }

    public boolean listenRequestList() throws RemoteException{
        boolean empty = approvalRequistList.isEmpty();
        return empty;
    }

    public void addApprove(String userName, boolean approval) throws RemoteException{
        int approve = 0;
        if(approval){
            approve = 1;
        }
        approveResult.put(userName,approve);
    }

    public void sendRequest(String userName) throws RemoteException{
        approvalRequistList.add(userName);
    }

    public int checkApproval(String userName) throws RemoteException{
        int approve = approveResult.get(userName);
        return  approve;
    }

}
