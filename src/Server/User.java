package Server;

public class User {
    private String username;
    private int userID;
    private String email;
    private boolean managerTag;

    public User(String username, int userID, String email, boolean managerTag) {
        this.username = username;
        this.userID = userID;
        this.email = email;
        this.managerTag = managerTag;
    }

    @Override
    public String toString() {
        return userID + ','
                + username + ','
                + email + ','
                + managerTag + ',';
    }

    public String getUsername(){
        return username;
    }

    public int getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setManagerTag(boolean managerTag) {
        this.managerTag = managerTag;
    }
}
