import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
/*
 * The server as a GUI
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;
    private JButton stopStart;
    private JTextArea chat,event,privateChat,userStatus;
    private JTextField tPortNumber;
    private Server server;
    private String outputPath = "output/clients.txt";
    private Hashtable<String, ChatClient> ccHashtable = new Hashtable<String, ChatClient>();

    ServerGUI(int port) {
        super("harmony server");
        server = null;
        JPanel north = new JPanel();
        north.add(new JLabel("Port number: "));
        tPortNumber = new JTextField("  " + port);
        north.add(tPortNumber);
        stopStart = new JButton("Start");
        stopStart.addActionListener(this);
        north.add(stopStart);
        add(north, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3,1));
        chat = new JTextArea(40,20);
        chat.setEditable(false);
        chat.setBackground(new Color(255,231,234));
        appendRoom("Chat room.\n");
        center.add(new JScrollPane(chat));
        privateChat = new JTextArea(40,20);
        privateChat.setEditable(false);
        privateChat.setBackground(new Color(255,231,234));

        appendPrivateChat("Private Chat.\n");
        center.add(new JScrollPane(privateChat));
        event = new JTextArea(40,20);
        event.setEditable(false);
        event.setBackground(new Color(255,231,234));
        appendEvent("Events log.\n");
        center.add(new JScrollPane(event));
        add(center,BorderLayout.CENTER);

        JPanel west = new JPanel(new GridLayout(1,1));
        userStatus = new JTextArea(80,10);
        userStatus.setEditable(false);
        userStatus.setBackground(new Color(255,231,234));

        west.add(new JScrollPane(userStatus));
        initUserStatus();
        add(west,BorderLayout.WEST);
        addWindowListener(this);
        setSize(400, 600);
        setVisible(true);
    }

    public void setChatUserlist(Hashtable<String, ChatClient> userlist){
        this.ccHashtable= (Hashtable<String, ChatClient>)userlist.clone();
    }
    public Hashtable<String, ChatClient> getChatUserlist(){
        return this.ccHashtable;
    }


    void getChatUserStatus()
    {
        File file = new File(outputPath);
        try{
            if (!file.exists()){
                file.createNewFile();
            }else{
                if(ccHashtable.size()==0){
                    FileReader fileReader = new FileReader(outputPath);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line;
                    int i=0;
                    ChatClient cc;
                    while ((line = bufferedReader.readLine()) !=null){
                        if(line.length()>0){
                            cc = new ChatClient(line,ChatClient.OFFLINE,"");
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
    void appendRoom(String str) {
        chat.append(str);
        chat.setCaretPosition(chat.getText().length() - 1);
    }
    void appendEvent(String str) {
        event.append(str);
        event.setCaretPosition(chat.getText().length() - 1);

    }
    void appendPrivateChat(String str) {
        privateChat.append(str);
        privateChat.setCaretPosition(chat.getText().length() - 1);
    }
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
                userStatus.append(cc.getUsername() + " " + cc.getStatus()+"\n");
            }
        }
    }
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
                userStatus.append(cc.getUsername() + " " + cc.getStatus()+"\n");
            }
        }
    }


    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o== stopStart){
            JButton button1 = (JButton) o;
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

    public void windowClosing(WindowEvent e) {
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

    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    /*
     * A thread to run the Server
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
