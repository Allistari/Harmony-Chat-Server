/*  [Client.java]
    Custom socket retrieval for connection to server and communication
    Author: Brian Zhang
    ICS4UE
    Date: 12/04/18
 */

//Import Statements
import java.net.*;
import java.io.*;

public class Client  {

    //Input Output Streams
    private ObjectInputStream listenInput;
    private ObjectOutputStream listenOutput;
    private Socket socket;

    //GUI Connection
    private ClientInterface template;
    private boolean abort = true;

    //String containers
    private String server, username;
    private int port;

    Client(String server, int port, String username, ClientInterface template) {
        this.server = server;
        this.port = port;
        this.username = username;

        this.template = template;
    }

    public boolean start() {
        //Attempt to connect to socket
        try {
            socket = new Socket(server, port);
        }
        catch(Exception ec) {

            //Can null the server connection
            display("error connecting to server: " + ec);
            System.out.println("error connecting to server");
            abort = true;
            return false;
        }

        String mserverGUI = "connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(mserverGUI);
        display(mserverGUI);

        try
        {
            listenInput  = new ObjectInputStream(socket.getInputStream());
            listenOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            display("exception creating new input/output Streams: " + eIO);
            return false;
        }

        new ListenFromServer().start();
        try
        {
            listenOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("exception doing login : " + eIO);
            disconnect();
            return false;
        }
        return true;
    }

    private void display(String mserverGUI) {
        template.append(mserverGUI + "\n");
    }

    //Forwards messages via ChatMessage
    public void sendMessage(ChatMessage mserverGUI) {
        try {
            listenOutput.writeObject(mserverGUI);
        }
        catch(IOException e) {
            display("exception writing to server: " + e);
        }
    }

    public boolean getConnectionStatus(){
        return abort;
    }

    //Manage connection to sockets
    private void disconnect() {
        try {
            if(listenInput != null) listenInput.close();
        }
        catch(Exception e) {}
        try {
            if(listenOutput != null) listenOutput.close();
        }
        catch(Exception e) {}
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {}

        template.connectionFailed();

    }

    //Maintains connection with server
    class ListenFromServer extends Thread {
        boolean maintain = true;
        ChatMessage chatmserverGUI;
        public void run() {
            try {
                while (maintain) {
                    try {
                        chatmserverGUI = (ChatMessage) listenInput.readObject();
                    } catch (EOFException ex) {
                        break;
                    } catch (IOException e) {
                        display("Server has closed the connection: ");
                        template.connectionFailed();
                        break;
                    }
                    catch (ClassNotFoundException e2) {
                    }


                    if (chatmserverGUI != null) {
                        if (chatmserverGUI.getType() == ChatMessage.USERNAME) {
                            template.appendUserList(chatmserverGUI.getMessage());
                        } else if (chatmserverGUI.getType() == ChatMessage.MESSAGE) {
                            template.append(chatmserverGUI.getMessage());
                        }
                    }

                }
        }
        finally {
            try {
                listenInput.close();
            }
            catch(IOException ex) {
                {
                    System.err.println("An IOException was caught: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    }
}
