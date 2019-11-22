/**
 * ChatClient.java
 * Chat Client object
 * @author Angelina Zhang
 * created 2018-12-11
 * last modified 2018-12-15
 */


public class ChatClient {
    private int status;
    private String username;
    static final int OFFLINE = 0, ONLINE = 1;

    public ChatClient(String username, int status){
        this.username =username;
        this.status = status;

    }

    /**
     * getStatus
     * @return status
     */
    public int getStatus(){
        return this.status;
    }

    /**
     * setStatus
     * sets the status
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * setUsername
     * sets the username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getUsername
     * @return username
     */
    public String getUsername() {
        return username;
    }
}