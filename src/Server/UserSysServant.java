package Server;

import RMIInterfaces.UserSysInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserSysServant extends UnicastRemoteObject implements UserSysInterface{


    public UserSysServant() throws RemoteException{
    }

    public boolean checkPassword(String username, String password)throws RemoteException{
        boolean validPassword = false;
        return validPassword;
    }

    public String[] registerUser(String username, String password)throws RemoteException{
        String[] userData = new String[2];
        return userData;
    }

}
