package Server;


import java.util.ArrayList;
        import java.util.Hashtable;

public class PaintsDatabase {

    private Hashtable paintsDatabase;

    public PaintsDatabase(Hashtable paintsDatabase){
        this.paintsDatabase = paintsDatabase;
    }

    public void setPaintsDatabase(Hashtable paintsDatabase, ArrayList<String> paintJason, int i){
        paintsDatabase.put(i, paintJason);
        this.paintsDatabase = paintsDatabase;

    }

    public Hashtable getPaintsDatabase(Hashtable paintsDatabase){
        return this.paintsDatabase;
    }
}