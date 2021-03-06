package Server;

import RMIInterfaces.UserSysInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class UserSysServant extends UnicastRemoteObject implements UserSysInterface {
    private ArrayList<String> approvalRequistList;
    private HashMap<String,String> approveResult;
    private ArrayList<String> kickList;

    protected UserSysServant() throws RemoteException {
        this.approveResult = new HashMap<>();
        this.approvalRequistList = new ArrayList<>();
        this.kickList = new ArrayList<>();
    }
    //clients use this method to listen if
    public String listenRequestList() throws RemoteException{
        boolean empty = approvalRequistList.isEmpty();
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        jsonObject.addProperty("isEmpty",empty);
        if(!empty){
            String userName = approvalRequistList.get(0);
            jsonObject.addProperty("userName",userName);
        }
        String jsonPack = gson.toJson(jsonObject);
        return jsonPack;
    }

    public void addApprove(String userName, boolean approval) throws RemoteException{
        String approve = "N";
        if(approval){
            approve = "Y";
        }
        approvalRequistList.remove(userName);
        approveResult.put(userName,approve);
    }

    public void sendRequest(String userName) throws RemoteException{
        approvalRequistList.add(userName);
    }

    public String checkApproval(String userName) throws RemoteException{
        String approve = approveResult.get(userName);
        if(approve!=null){
            approveResult.remove(userName);
        }
        return approve;
    }

    public void kick(String username)throws RemoteException{
        kickList.add(username);
    }

    public boolean checkKick(String userName) throws RemoteException{
        boolean inKickList  = kickList.contains(userName);
        if(inKickList){
            kickList.remove(userName);
        }
        return  inKickList;
    }

}
