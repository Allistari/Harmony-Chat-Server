public class ChatClient {
    private int status;
    private String username;
    private String IPAdress;
    static final int OFFLINE = 0, ONLINE = 1;
    public ChatClient(String username, int status, String IPAdress){
        this.username =username;
        this.IPAdress = IPAdress;
        this.status = status;

    }
    public int getStatus(){
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIPAdress() {
        return IPAdress;
    }

    public String getUsername() {
        return username;
    }
}
