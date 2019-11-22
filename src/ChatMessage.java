import java.io.*;
import java.util.ArrayList;

public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    static final int USERLIST = 0, MESSAGE = 1, LOGOUT = 2 ,PRIVATEMSG = 3,USERNAME = 4,SERVERIMAGE = 5;
    private int type;
    private String message;
    private ArrayList<String> receivers;


    ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
        this.receivers = new ArrayList<String>();
    }

    public int getType() {
        return type;
    }
    public void setType(int msgType){ this.type = msgType;}
    public String getMessage() {
        return message;
    }
    public void setMessage(String msg) {
        message = msg;
    }
    public void setReceivers(ArrayList<String> selectedUserlist){
        receivers = (ArrayList<String>)selectedUserlist.clone();
    }
    public ArrayList<String> getReceivers(){
        return receivers;
    }

}
