package Server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class test {
    public static void main(String[] args){
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        String jsonpack = "";
        User test = new User("test", 11, false);
        String newuser = gson.toJson(test);
        jsonObject.addProperty("registerUser", newuser);
        jsonObject.addProperty("testPassword", "12345678");
        jsonpack = gson.toJson(jsonObject);
        JsonElement jsonElement = new JsonParser().parse(jsonpack);
        jsonObject = jsonElement.getAsJsonObject();
        System.out.println(jsonObject.keySet());
        System.out.println("registerUser: "+jsonObject.get("registerUser"));
        System.out.println("testPassword: "+jsonObject.get("testPassword"));
        User obj2 = gson.fromJson(jsonObject.get("registerUser").getAsString(), User.class);
        System.out.println(obj2);
    }
}
