package ntou.cse.soselab.example;

public class User {
    private String nameInfo;
    private String emailInfo;
    private String passwordInfo;
    private String ID;

    public User() {
    }

    public User(String name_info, String Email_Info, String passwordInfo) {
        this.nameInfo = name_info;
        this.emailInfo = Email_Info;
        this.passwordInfo = passwordInfo;
    }

    public String getName() {
        return nameInfo;
    }

    public String getEmail() {
        return emailInfo;
    }

    public String getPassword() {
        return passwordInfo;
    }

    public void setName(String name_info) {
        this.nameInfo = name_info;
    }

    public void setEmail(String Email_Info) {
        this.emailInfo = Email_Info;
    }

    public void setPassword(String passwordInfo) {
        this.passwordInfo = passwordInfo;
    }
}
