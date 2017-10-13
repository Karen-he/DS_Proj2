package RMIInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientServer extends Remote{
    String sendGson(String send) throws RemoteException;
    String receiveGson() throws RemoteException;
}
