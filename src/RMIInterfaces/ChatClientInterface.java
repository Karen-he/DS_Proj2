package RMIInterfaces;

import Client.WBController;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInterface extends Remote {
    void retrieveMsg(String chatContent) throws RemoteException;

}
