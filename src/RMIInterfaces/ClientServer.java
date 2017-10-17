package RMIInterfaces;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientServer {
    void appendToMessage(String message);
}
