import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
/**
 * ServerInterface.java
 * the server as a GUI
 * @author Angelina Zhang
 * created 2018-12-11
 * last modified 2018-12-15
 */

public class ServerInterface extends JFrame implements ActionListener, WindowListener {


    private static final long serialVersionUID = 1L;
    private JButton stopStart;
    private JTextArea chat,event,privateChat,userStatus;
    private JTextField tPortNumber;
    private Server server;
    private String outputPath = "output/clients.txt";
    private Hashtable<String, ChatClient> ccHashtable = new Hashtable<String, ChatClient>();
    private static final int LOGGEDIN = 1, OFFLINE =0;
    private String status;

    /**
     * ServerInterface
     * GUI for server
     * @param port
     */
    ServerInterface(int port) {
        //title
        super("harmony server");
        server = null;
        JPanel north = new JPanel();

        //add place to change port number
        north.add(new JLabel("Port number: "));
        tPortNumber = new JTextField("  " + port);
        north.add(tPortNumber);

        //button to start/ stop server
        stopStart = new JButton("Start");
        stopStart.addActionListener(this);
        north.add(stopStart);
        add(north, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3,1));
        //show chat history
        chat = new JTextArea(40,20);
        chat.setEditable(false);
        chat.setBackground(new Color(255,231,234));
        appendRoom("Chat room.\n");
        center.add(new JScrollPane(chat));

        //show private chat private
        privateChat = new JTextArea(40,20);
        privateChat.setEditable(false);
        privateChat.setBackground(new Color(255,231,234));
        appendPrivateChat("Private Chat.\n");
        center.add(new JScrollPane(privateChat));

        //show event logs
        event = new JTextArea(40,20);
        event.setEditable(false);
        event.setBackground(new Color(255,231,234));
        appendEvent("Events log.\n");
        center.add(new JScrollPane(event));
        add(center,BorderLayout.CENTER);
        JPanel west = new JPanel(new GridLayout(1,1));

        //show all users and status
        userStatus = new JTextArea(80,10);
        userStatus.setEditable(false);
        userStatus.setBackground(new Color(255,231,234));
        appendStatus("User Status.\n");
        west.add(new JScrollPane(userStatus));
        initUserStatus();
        add(west,BorderLayout.WEST);
        addWindowListener(this);
        setSize(400, 600);
        setVisible(true);
    }

    /**
     * getChatUserList
     * get a list of all chat users
     * @return ccHashtable
     */
    public Hashtable<String, ChatClient> getChatUserList(){
        return this.ccHashtable;
    }


    /**
     * getChatUserStatus
     * determine whether the user is online or offline
     */
    void getChatUserStatus()
    {
        //save all user that has logged on to text file
        try{
            File file = new File(outputPath);

            if (!file.exists()){

                file.createNewFile();

            }else{
                if(ccHashtable.size()==0){
                    FileReader fileReader = new FileReader(outputPath);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line;
                    ChatClient cc;
                    while ((line = bufferedReader.readLine()) !=null){
                        if(line.length()>0){
                            cc = new ChatClient(line,ChatClient.OFFLINE);
                            this.ccHashtable.put(line,cc);
                        }
                    }
                    bufferedReader.close();
                    fileReader.close();
                }
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * saveUserStatus
     * save a new username to text file
     */
    public void saveUserStatus()
    {
        try{
            Enumeration names;
            String key;
            FileWriter statusfileWriter = new FileWriter(outputPath);
            BufferedWriter statusbufferedWriter = new BufferedWriter(statusfileWriter);
            if (this.ccHashtable.size()>0){
                names = ccHashtable.keys();
                while(names.hasMoreElements()) {
                    key = (String) names.nextElement();
                    statusbufferedWriter.write(key+"\n");
                    statusbufferedWriter.newLine();
                }
            }
            statusbufferedWriter.close();
            statusfileWriter.close();

        }catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * appendRoom
     * adds to chat room
     * @param str
     */
    void appendRoom(String str) {
        chat.append(str);
        chat.setCaretPosition(chat.getText().length() - 1);
    }

    /**
     * appendEvent
     * adds to event log
     * @param str
     */
    void appendEvent(String str) {
        event.append(str);
        event.setCaretPosition(chat.getText().length() - 1);
    }

    /**
     * appendStatus
     * adds to user status
     * @param str
     */
    void appendStatus(String str){
        userStatus.append(str);
        //userStatus.setCaretPosition(userStatus.getText().length() - 1);
    }

    /**
     * appendPrivateChat
     * adds to private chat
     * @param str
     */
    void appendPrivateChat(String str) {
        privateChat.append(str);
        privateChat.setCaretPosition(chat.getText().length() - 1);
    }

    /**
     * initUserStatus
     * generates list of all users with status
     */
    void initUserStatus() {
        getChatUserStatus();
        if (ccHashtable.size()>=0){
            Enumeration names;
            String key;
            names = ccHashtable.keys();
            ChatClient cc;
            while(names.hasMoreElements()) {
                key = (String) names.nextElement();

                cc = ccHashtable.get(key);
                if((cc.getStatus())==LOGGEDIN){
                    status = "(Online)";
                } else if ((cc.getStatus())==OFFLINE){
                    status = "(Offline)";
                }
                userStatus.append(cc.getUsername() + " " +status+"\n");
            }
        }
    }

    /**
     * refreshUserStatus
     * updates the user status list
     * @param isLogoff
     */
    void refreshUserStatus(Boolean isLogoff) {
        userStatus.setText("");
        if (ccHashtable.size()>=0){
            Enumeration names;
            String key;
            names = ccHashtable.keys();
            ChatClient cc;
            while(names.hasMoreElements()) {
                key = (String) names.nextElement();
                cc = ccHashtable.get(key);
                if (isLogoff){
                    cc.setStatus(ChatClient.OFFLINE);
                    ccHashtable.put(key,cc);
                }
                if((cc.getStatus())==LOGGEDIN){
                    System.out.println("test");
                    status = "(Online)";
                } else if ((cc.getStatus())==OFFLINE){
                    status = "(Offline)";
                }
                userStatus.append(cc.getUsername() + " " +status+"\n");
            }
        }
    }

    /**
     * actionPerformed
     * action listener
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o== stopStart){
            JButton button1 = (JButton) o;
            //stops the server
            if (button1.getText().toLowerCase().equals("start")){
                if(server != null) {
                    server.stop();
                    server = null;
                    tPortNumber.setEditable(true);
                    stopStart.setText("Start");
                    return;
                }
                int port;
                try {
                    port = Integer.parseInt(tPortNumber.getText().trim());
                }
                catch(Exception er) {
                    appendEvent("Invalid port number");
                    return;
                }

                server = new Server(port, this);
                new ServerRunning().start();
                stopStart.setText("Stop");
                tPortNumber.setEditable(false);
            }else{
                if(server != null) {
                    try {
                        saveUserStatus();
                        server.stop();
                    }
                    catch(Exception eClose) {
                    }
                    server = null;
                }
            }

        }
    }

    /**
     * windowClosing
     * closes the server when the window is closed
     * @param e
     */
    public void windowClosing(WindowEvent e) {

        //Saves User Status
        if(server != null) {
            try {
                saveUserStatus();
                server.stop();
            }
            catch(Exception eClose) {
            }
            server = null;
        }

        dispose();
        System.exit(0);
    }

    /**
     * Other used methods from windowListener interface
     * @param e
     */
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    /**
     * ServerRunning
     * a thread to run the server
     */
    class ServerRunning extends Thread {
        public void run() {
            server.start();
            stopStart.setText("Start");
            tPortNumber.setEditable(true);
            appendEvent("Server crashed\n");
            server = null;
        }
    }

}