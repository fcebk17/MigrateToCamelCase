package ntou.cse.soselab.example;

public class User {
    private String name_info;
    private String Email_Info;
    private String PasswordInfo;

    public User() {
    }

    public User(String name_info, String Email_Info, String passwordInfo) {
        this.name_info = name_info;
        this.Email_Info = Email_Info;
        this.PasswordInfo = passwordInfo;
    }

    public String getName() {
        return name_info;
    }

    public String getEmail() {
        return Email_Info;
    }

    public String getPassword() {
        return PasswordInfo;
    }

    public void setName(String name_info) {
        this.name_info = name_info;
    }

    public void setEmail(String Email_Info) {
        this.Email_Info = Email_Info;
    }

    public void setPassword(String passwordInfo) {
        this.PasswordInfo = passwordInfo;
    }
}
