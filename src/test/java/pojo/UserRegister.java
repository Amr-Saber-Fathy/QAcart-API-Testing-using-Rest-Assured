package pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegister {

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    //Default Constructor
    public UserRegister(){}

    //1 Parameter Constructor
    public UserRegister(String email){
        this.setEmail(email);
    }
    //2 Parameters Constructor
    public UserRegister(String email, String password){
        this(email);
        this.setPassword(password);
    }
    //3 Parameters Constructor
    public UserRegister(String email, String password, String firstName){
        this(email, password);
        this.setFirstName(firstName);
    }
    //4 Parameters Constructor
    public UserRegister(String email, String password, String firstName, String lastName){
        this(email, password, firstName);
        this.setLastName(lastName);
    }
}
