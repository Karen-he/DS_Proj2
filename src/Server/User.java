package Server;

public class User {
    private String username;
    private int userID;
    private boolean managerTag;

    public User(String username, int userID, boolean managerTag) {
        this.username = username;
        this.userID = userID;
        this.managerTag = managerTag;
    }

    public User(String username) {
        this.username = username;
        this.managerTag = false;
        this.userID = -1;
    }

    public String getUsername(){
        return username;
    }

    public int getUserID() {
        return userID;
    }

    public boolean isManagerTag() {
        return managerTag;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setManagerTag(boolean managerTag) {
        this.managerTag = managerTag;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userID=" + userID +
                ", managerTag=" + managerTag +
                '}';
    }
}
