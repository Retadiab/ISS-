import java.io.File;

public class Account {
    public  String title;
    public String userName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
    public String description;
    public File file;
    public  String id;
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public boolean addNewAcct(Account acct) {
        return true;
    }
    public Account viewAcct(String title) {
        Account acct = new Account();
        return acct;
    }
    public boolean editAcct(String title) {
        Account acct = new Account();
        return true;
    }
    public boolean deleteAcct(String title) {
        return true;
    }

}
