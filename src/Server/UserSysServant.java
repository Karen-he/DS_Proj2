package Server;

import RMIInterfaces.UserSysInterface;
import com.google.gson.Gson;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class UserSysServant extends UnicastRemoteObject implements UserSysInterface{


    public UserSysServant() throws RemoteException{
    }

    public boolean checkPassword(String username, String password)throws RemoteException{
        boolean validPassword = false;
        String jsonpack = "";
        ArrayList<String> keywords = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        keywords.add("checkPassword");
        contents.add(username);
        keywords.add("password");
        contents.add(password);
        GsonServant gsonServant = new GsonServant();
        jsonpack = gsonServant.sendGson(keywords, contents);
        sendToServer(jsonpack);
        return validPassword;
    }

    private void sendToServer(String jsonpack) {
        
    }

    public String registerUser(String username, String password)throws RemoteException{
        String jsonpack = "";
        ArrayList<String> keywords = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        keywords.add("registerUser");
        User newUser = new User(username);
        Gson gson = new Gson();
        String newuser = gson.toJson(newUser);
        contents.add(newuser);
        keywords.add("password");
        contents.add(password);
        GsonServant gsonServant = new GsonServant();
        jsonpack = gsonServant.sendGson(keywords, contents);
        return jsonpack;
    }

    public void sendBack(boolean validPassword) {
    }
}
