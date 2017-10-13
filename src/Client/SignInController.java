package Client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SignInController {


    @FXML
    private TextField userName;

    @FXML
    private TextField passWord;

    public void initialize(){

    }

    public void signIn(){
        String user = userName.getText();
        String encrypt = passWord.getText();

    }

    public void singUp(){
        String userRegister = userName.getText();
        String passwordRe = passWord.getText();

    }
}
