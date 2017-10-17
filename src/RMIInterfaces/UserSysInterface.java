package RMIInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserSysInterface extends Remote {
    boolean listenRequestList() throws RemoteException;
    void addApprove(String userName, boolean approve) throws RemoteException;
    void sendRequest(String userName) throws RemoteException;
    String checkApproval(String userName) throws RemoteException;

}
