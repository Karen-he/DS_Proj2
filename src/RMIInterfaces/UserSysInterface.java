package RMIInterfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserSysInterface extends Remote{
    boolean checkPassword(String username, String password)throws RemoteException;
    String[] registerUser(String username, String password)throws RemoteException;
}
