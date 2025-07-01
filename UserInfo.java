// IDEIA APENAS
public class UserInfo{
    private String userName;
    private boolean isAdmin;
    private String salaAtual;

    public UserInfo (String userName){
        this.setUserName(userName);
        this.isAdmin = false;
        this.salaAtual = null;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName (String userName) {
        this.userName = userName;
    }

    public boolean IsAdmin(){
        return this.isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getSalaAtual() {
        return this.salaAtual;
    }
    public void setCurrentRoomName (String salaAtual){
        this.salaAtual = salaAtual;
    }
}