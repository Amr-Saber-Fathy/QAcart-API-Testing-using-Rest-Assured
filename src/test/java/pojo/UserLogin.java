package pojo;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLogin {

    private String email;
    private String password;

    //Default Constructor
    public UserLogin(){}

    //1 Parameter Constructor
    public UserLogin(String email){
        this.setEmail(email);
    }
    //2 Parameters Constructor
    public UserLogin(String email, String password){
        this(email);
        this.setPassword(password);
    }
}
