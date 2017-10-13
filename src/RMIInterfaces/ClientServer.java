package RMIInterfaces;

import com.google.gson.Gson;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientServer extends Remote{
    public Gson sendGson(String send) throws RemoteException;
    public String receiveGson(Gson receive) throws RemoteException;
}
