package Server;


import java.util.ArrayList;
        import java.util.Hashtable;

public class PaintsDatabase {

    private Hashtable paintsDatabase;

    public PaintsDatabase(){

        this.paintsDatabase = new Hashtable();
    }

    public void addPaintsDatabase(ArrayList<String> paintJason, int i){
        paintsDatabase.put(i, paintJason);

    }

    public Hashtable getPaintsDatabase(){
        return this.paintsDatabase;
    }

    public void clearDatabase(Hashtable paintsDatabase){
        paintsDatabase.clear();
    }
}