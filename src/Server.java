import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    private static int uniqueId;
    private ArrayList<ClientThread> al;
    private ServerGUI sg;
    private SimpleDateFormat sdf;
    private int port;
    private boolean keepGoing;
    private ServerSocket serverSocket;
    private Hashtable<String, ChatClient> ccHashtable = new Hashtable<String, ChatClient>();

    public Server(int port) {
        this(port, null);
    }

    public Server(int port, ServerGUI sg) {
        this.sg = sg;
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        al = new ArrayList<ClientThread>();
    }

    public void start() {
        keepGoing = true;
        String key;
        try
        {
            serverSocket = new ServerSocket(port);
            while(keepGoing)
            {
                display("Server waiting for Clients on port " + port + ".");
                Socket socket = serverSocket.accept();  	// accept connection
                if(!keepGoing)
                    break;
                ClientThread t = new ClientThread(socket);  // make a thread of it
                al.add(t);// save it in the ArrayList
                if(sg !=null){
                    ccHashtable = sg.getChatUserlist();
                    ChatClient cc;
                    if (ccHashtable.get(t.username)==null){
                        cc = new ChatClient(t.username,ChatClient.ONLINE,"");
                        ccHashtable.put(t.username,cc);
                    }else{
                        cc = ccHashtable.get(t.username);
                        cc.setStatus(ChatClient.ONLINE);
                        ccHashtable.put(t.username,cc);
                    }
                    sg.refreshUserStatus(false);
                }
                t.start();
            }
            try {
                serverSocket.close();
                for(int i = 0; i < al.size(); ++i) {
                    ClientThread tc = al.get(i);
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
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
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    protected void stop() {
        keepGoing = false;
        try {
            serverSocket.close();
            for(int i = 0; i < al.size(); ++i) {
                ClientThread tc = al.get(i);
                try {
                    tc.sInput.close();
                    tc.sOutput.close();
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

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        if(sg == null)
            System.out.println(time);
        else
            sg.appendEvent(time + "\n");
    }
    private synchronized void broadcast(String message) {
        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";
        ChatMessage sm;
        try {
            if (sg == null)
                System.out.print(messageLf);
            else
                sg.appendRoom(messageLf);
            for (int i = al.size(); --i >= 0; ) {
                ClientThread ct = al.get(i);
                sm = new ChatMessage(ChatMessage.MESSAGE, messageLf);
                if (!ct.writeMsgObj(sm)) {
                    al.remove(i);
                    display("Disconnected Client " + ct.username + " removed from list.");
                }
            }
        }catch(Exception e)
        {
            System.out.println(e.getMessage());
            sg.appendEvent("Exception:" + e.getMessage());
        }
    }

    private void privateChat(ChatMessage cm)
    {
        if (cm==null){return;}
        String chatusername ;
        String time = sdf.format(new Date());
        String messageLf = time + " " + cm.getMessage() + "\n";
        ChatMessage sm;
        try {
            if (sg == null)
                System.out.print(messageLf);
            else
                sg.appendPrivateChat(messageLf);     // append in the room window
            if (cm.getReceivers().size() > 0) {
                chatusername = cm.getReceivers().get(0);
                for (int i = al.size(); --i >= 0; ) {
                    ClientThread ct = al.get(i);
                    if (chatusername.equals(ct.username)) {
                        sm = new ChatMessage(ChatMessage.MESSAGE, messageLf);
                        if (!ct.writeMsgObj(sm)) {
                            al.remove(i);
                            display("Disconnected Client " + ct.username + ".");
                        }
                        break;
                    }
                }
            }
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
            sg.appendEvent("exception:" + e.getMessage());
        }
    }
    synchronized void remove(int id) {
        for(int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            // found it
            if(ct.id == id) {
                al.remove(i);
                return;
            }
        }
    }

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

    /** One instance of this thread will run for each client */
    class ClientThread extends Thread {
        // the socket where to listen/talk
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;
        String date;
        ChatMessage sm;
        ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            System.out.println("Thread trying to create Object Input/Output Streams");
            try
            {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                display(username + " just connected.");
            }
            catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            catch (ClassNotFoundException e) {
            }
            date = new Date().toString() + "\n";
        }

        public void run() {
            boolean keepGoing = true;
            while(keepGoing) {

                //Error here
                try {
                    cm = (ChatMessage) sInput.readObject();
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
                    case ChatMessage.PRIVATEMSG:
                        cm.setMessage(username + ": " +message );
                        privateChat(cm);
                        break;
                    case ChatMessage.LOGOUT:
                        display(username + " disconnected with a LOGOUT message.");
                        if (sg !=null){
                            sg.refreshUserStatus(true);
                        }
                        keepGoing = false;
                        break;
                    case ChatMessage.USERLIST:
                        // scan al the users connected
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
                                    sm = new ChatMessage(ChatMessage.USERNAME,cc.getUsername() + ";" + cc.getStatus());
                                    writeMsgObj(sm);
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


        private void close() {
            try {
                if(sOutput != null) sOutput.close();
            }
            catch(Exception e) {}
            try {
                if(sInput != null) sInput.close();
            }
            catch(Exception e) {};
            try {
                if(socket != null) socket.close();
            }
            catch (Exception e) {}
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsgObj(ChatMessage msg) {
            if(!socket.isConnected()) {
                close();
                return false;
            }
            try {

                sOutput.writeObject(msg);
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
