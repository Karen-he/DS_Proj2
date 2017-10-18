package RMIInterfaces;

import com.google.gson.JsonObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserSysInterface extends Remote {
    String listenRequestList() throws RemoteException;
    void addApprove(String userName, boolean approve) throws RemoteException;
    void sendRequest(String userName) throws RemoteException;
    String checkApproval(String userName) throws RemoteException;

}
