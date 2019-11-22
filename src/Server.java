import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Server.java
 * makes a server
 * @author Angelina Zhang
 * created 2018-12-11
 * last modified 2018-12-15
 */

public class Server {
    private static int uniqueId;
    private ArrayList<ClientThread> users;
    private ServerInterface serverGUI;
    private SimpleDateFormat dateAndTime;
    private int port;
    private boolean maintain;
    private ServerSocket serverSocket;
    private Hashtable<String, ChatClient> ccHashtable = new Hashtable<String, ChatClient>();

    /**
     * Server
     * constructor
     * @param port
     */
    public Server(int port) {
        this(port, null);
    }

    /**
     * Server
     * constructor
     * @param port
     * @param serverGUI
     */
    public Server(int port, ServerInterface serverGUI) {
        this.serverGUI = serverGUI;
        this.port = port;
        dateAndTime = new SimpleDateFormat("HH:mm:ss");
        users = new ArrayList<ClientThread>();
    }

    /**
     * start
     * start running the server
     */
    public void start() {
        maintain = true;
        try
        {
            serverSocket = new ServerSocket(port);
            while(maintain)
            {
                display("Server waiting for Clients on port " + port + ".");
                Socket socket = serverSocket.accept();  	// accept connection
                if(!maintain)
                    break;
                ClientThread t = new ClientThread(socket);  // make a thread of it
                users.add(t);// save it in the ArrayList
                if(serverGUI !=null){
                    ccHashtable = serverGUI.getChatUserList();
                    ChatClient cc;
                    if (ccHashtable.get(t.username)==null){
                        cc = new ChatClient(t.username,ChatClient.ONLINE);
                        ccHashtable.put(t.username,cc);
                    }else{
                        cc = ccHashtable.get(t.username);
                        cc.setStatus(ChatClient.ONLINE);
                        ccHashtable.put(t.username,cc);
                    }
                    serverGUI.refreshUserStatus(false);
                }
                t.start();
            }
            try {
                serverSocket.close();
                for(int i = 0; i < users.size(); ++i) {
                    ClientThread tc = users.get(i);
                    try {
                        tc.listenInput.close();
                        tc.listenOutput.close();
                        tc.socket.close();
                    }
                    catch(IOException ioE) {
                    }
                }
            }
            catch(Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        }
        catch (IOException e) {
            String mserverGUI = dateAndTime.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(mserverGUI);
        }
    }

    /**
     * stop
     * stops the thread
     */
    protected void stop() {
        maintain = false;
        try {
            serverSocket.close();
            for(int i = 0; i < users.size(); ++i) {
                ClientThread tc = users.get(i);
                try {
                    tc.listenInput.close();
                    tc.listenOutput.close();
                    tc.socket.close();
                }
                catch(IOException ioE) {
                }
            }
        }
        catch(Exception e) {
            display("Exception closing the server and clients: " + e);
        }

        try {
            new Socket("localhost", port);
        }
        catch(Exception e) {
        }
    }

    /**
     * display
     * display event log with time
     * @param mserverGUI
     */
    private void display(String mserverGUI) {
        String time = dateAndTime.format(new Date()) + " " + mserverGUI;
        if(serverGUI == null)
            System.out.println(time);
        else
            serverGUI.appendEvent(time + "\n");
    }

    /**
     * broadcast
     * shows message to all users
     * @param message
     */
    private synchronized void broadcast(String message) {
        String time = dateAndTime.format(new Date());
        String messageLf = time + " " + message + "\n";
        ChatMessage sm;
        try {
            if (serverGUI == null)
                System.out.print(messageLf);
            else
                serverGUI.appendRoom(messageLf);
            for (int i = users.size(); --i >= 0; ) {
                ClientThread ct = users.get(i);
                sm = new ChatMessage(ChatMessage.MESSAGE, messageLf);
                if (!ct.writeMserverGUIObj(sm)) {
                    users.remove(i);
                    display("Disconnected Client " + ct.username + " removed from list.");
                }
            }
        }catch(Exception e)
        {
            System.out.println(e.getMessage());
            serverGUI.appendEvent("Exception:" + e.getMessage());
        }
    }

    /**
     * privateChat
     * only shows message to selected users
     * @param cm
     */
    private void privateChat(ChatMessage cm)
    {
        if (cm==null){return;}
        String chatusername ;
        String time = dateAndTime.format(new Date());
        String messageLf = time + " " + cm.getMessage() + "\n";
        ChatMessage sm;
        try {
            if (serverGUI == null)
                System.out.print(messageLf);
            else
                serverGUI.appendPrivateChat(messageLf);     // append in the room window
            if (cm.getReceivers().size() > 0) {
                chatusername = cm.getReceivers().get(0);
                for (int i = users.size(); --i >= 0; ) {
                    ClientThread ct = users.get(i);
                    if (chatusername.equals(ct.username)) {
                        sm = new ChatMessage(ChatMessage.MESSAGE, messageLf);
                        if (!ct.writeMserverGUIObj(sm)) {
                            users.remove(i);
                            display("Disconnected Client " + ct.username + ".");
                        }
                        break;
                    }
                }
            }
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
            serverGUI.appendEvent("exception:" + e.getMessage());
        }
    }

    /**
     * remove
     * log out of the server
     * @param id
     */
    synchronized void remove(int id) {
        for(int i = 0; i < users.size(); ++i) {
            ClientThread ct = users.get(i);
            // found it
            if(ct.id == id) {
                users.remove(i);
                return;
            }
        }
    }

    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {
        int portNumber = 1500;
        switch(args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Server [portNumber]");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: > java Server [portNumber]");
                return;

        }
        Server server = new Server(portNumber);
        server.start();
    }

    /**
     * ClientThread
     * makes a new thread for every client
     */
    class ClientThread extends Thread {
        // the socket where to listen/talk
        Socket socket;
        ObjectInputStream listenInput;
        ObjectOutputStream listenOutput;
        int id;
        String username;
        ChatMessage cm;
        String date;
        ChatMessage sm;
        ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            System.out.println("Attempting Input/Output Streams");
            try
            {
                listenOutput = new ObjectOutputStream(socket.getOutputStream());
                listenInput  = new ObjectInputStream(socket.getInputStream());
                username = (String) listenInput.readObject();
                display(username + " just connected.");
            }
            catch (IOException e) {
                display("Exception during attempt: Input/output Streams: " + e);
                return;
            }
            catch (ClassNotFoundException e) {
            }
            date = new Date().toString() + "\n";
        }

        /**
         * run
         * keep running
         */
        public void run() {
            boolean maintain = true;
            while(maintain) {

                //Error here
                try {
                    cm = (ChatMessage) listenInput.readObject();
                }
                catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {
                    break;
                }
                String message = cm.getMessage();

                switch(cm.getType()) {
                    case ChatMessage.MESSAGE:
                        broadcast(username + ": " + message);
                        break;
                    case ChatMessage.PRIVATEMserverGUI:
                        cm.setMessage(username + ": " +message );
                        privateChat(cm);
                        break;
                    case ChatMessage.LOGOUT:
                        display(username + " disconnected from the server.");
                        writeMserverGUIObj(new ChatMessage(1, username+" has logged out of the server."));
                        if (serverGUI !=null){
                            serverGUI.refreshUserStatus(true);
                        }
                        maintain = false;
                        break;
                    case ChatMessage.USERLIST:
                        // scan al the users connected
                        writeMserverGUIObj(new ChatMessage(1,"Current users connected at " + dateAndTime.format(new Date()) + "\n"));
                        for(int i = 0; i < users.size(); ++i) {
                            ClientThread ct = users.get(i);
                            writeMserverGUIObj(new ChatMessage(1, (i+1) + ") " + ct.username + " live since " + ct.date));
                        }

                        Enumeration names;
                        String key;
                        try{
                            if(ccHashtable.size()>0)
                            {
                                names = ccHashtable.keys();
                                while(names.hasMoreElements()) {
                                    key = (String) names.nextElement();
                                    ChatClient cc;
                                    cc = ccHashtable.get(key);
                                    sm = new ChatMessage(ChatMessage.USERNAME,cc.getUsername() + " : " + cc.getStatus());
                                    writeMserverGUIObj(sm);
                                }
                            }
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;

                }
            }
            remove(id);
            close();
        }


        /**
         * close
         * close all streams
         */
        private void close() {
            try {
                if(listenOutput != null) listenOutput.close();
            }
            catch(Exception e) {}
            try {
                if(listenInput != null) listenInput.close();
            }
            catch(Exception e) {};
            try {
                if(socket != null) socket.close();
            }
            catch (Exception e) {}
        }
        /**
         * writeMserverGUIObj
         * writes a string to the client output stream
         * @param mserverGUI
         * @return
         */
        private boolean writeMserverGUIObj(ChatMessage mserverGUI) {
            if(!socket.isConnected()) {
                close();
                return false;
            }
            try {

                listenOutput.writeObject(mserverGUI);
            }
            catch(IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
                System.out.println(e.getMessage().toString());
            }
            return true;
        }
    }
}