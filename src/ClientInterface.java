/*  [ClientInterface.java]
    JFrame handling messaging for the program and allowing user interaction
    Author: Brian Zhang
    ICS4UE
    Date: 12/04/18
 */

//Import Statements
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class ClientInterface extends JFrame implements ActionListener {

    //Text Label
    private JLabel instructions, section, padding;

    //Chat TextField SetUp
    private JTextField enterField;
    private JTextArea hub;
    private JComboBox Chatuserlist;

    //Button SetUp
    private JButton logout, userlist;

    //Connection Check
    private boolean connected = true;
    private boolean disable = false;

    //Status Variables
    private static final String LOGGEDIN =" 1", OFFLINE =" 0";
    private String status;
    private String [] split;
    private String userStatus;

    //Client object
    private Client client;

    //Receives socket
    ClientInterface(String server, int port, String username) {

        super("harmony");

        //Forward to new client
        client = new Client(server, port, username, this);
        if(!client.start()){
            disable = true;
        }


        //Custom Labels
        Image harmonyLogo = new ImageIcon("harmonynote.png").getImage();
        setIconImage(harmonyLogo);

        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("renogare.otf")));
        } catch (IOException | FontFormatException e) {
            //Handle exception
        }

        //Custom UI Font
        setUIFont(new javax.swing.plaf.FontUIResource("renogare", Font.PLAIN, 14));


        //GUI Layout Management
        //Chatroom Layout
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        hub = new JTextArea("|write something meaningful today.|\n", 80, 80);
        JPanel centerPanel = new JPanel(new GridLayout(1, 1));


        JScrollPane sc = new JScrollPane(hub);
        centerPanel.add(sc);

        hub.setEditable(true);
        hub.setBackground(new Color(255, 231, 234));
        add(centerPanel, BorderLayout.CENTER);

        //Buttons

        logout = new JButton("|logout|");
        logout.setForeground(new Color(219, 116, 113));
        userlist = new JButton("|userlist|");

        //Action Listener
        logout.addActionListener(this);
        userlist.addActionListener(this);

        JPanel lowerPanel = new JPanel(new GridLayout(3,2,0,20));

        // the Label and the TextField
        instructions = new JLabel("|enter your message:|", SwingConstants.CENTER);
        section = new JLabel("|pm someone:|", SwingConstants.CENTER);
        padding = new JLabel("Logged in as: " +username, SwingConstants.CENTER);
        padding.setForeground(new Color(113, 183, 131));
        inputPanel.add(section);
        lowerPanel.add(instructions);


        //New Data
        Chatuserlist = new JComboBox();
        inputPanel.add(Chatuserlist);
        Chatuserlist.setEnabled(true);
        Chatuserlist.addItem("");


        enterField = new JTextField("");
        enterField.addActionListener(this);
        enterField.setBackground(new Color(255, 231, 234));
        lowerPanel.add(enterField);
        lowerPanel.add(inputPanel);

        lowerPanel.add(padding);
        lowerPanel.add(logout);
        lowerPanel.add(userlist);
        add(lowerPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        enterField.requestFocus();

        //Connection Disable Check
        if(disable == true){
            enterField.setEnabled(false);
            logout.setEnabled(false);
            userlist.setEnabled(false);
            instructions.setText("retry connection");
            instructions.setForeground(Color.RED);
            hub.setText("retry connection");
            hub.setForeground(Color.RED);
            padding.setText("retry connection");
            padding.setForeground(Color.RED);
            hub.setBackground(Color.BLACK);
            enterField.setBackground(Color.BLACK);
        }
    }

    //Updates text within TextArea
    public void append(String str) {
        if(hub != null) {
            hub.append(str);
            hub.setCaretPosition(hub.getText().length() - 1);
        }
    }

    //Updates selectable users within PM
    public void appendUserList(String str){
        if(((DefaultComboBoxModel)this.Chatuserlist.getModel()).getIndexOf(str) == -1) {
            split = str.split(":");
            status = split[1];
            if (status.equals(LOGGEDIN)){
                userStatus = split[0]+" (Online)";
            }else if(status.equals(OFFLINE)){
                userStatus = split[0]+" (Offline)";
            }

            this.Chatuserlist.addItem(userStatus);
        }
    }

    //Reset Connectors in UserList
    private void resetStatus()
    {
        if (Chatuserlist.getItemCount()!=0) {
            Chatuserlist.removeAllItems();
        }
        connectionFailed();
    }

    //Override ActionListener
    public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        boolean isPrivate = false;

        String chatmessages = enterField.getText();
        String selectedUserName = "";
        ChatMessage cm = new ChatMessage(ChatMessage.MESSAGE,chatmessages);

        if(button == logout) {
            //Custom Logout Handle
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
            resetStatus();
            return;
        }

        if((button == userlist)&&(connected)) {
            //Custom userlist handle
            client.sendMessage(new ChatMessage(ChatMessage.USERLIST, ""));
            return;
        }

        //Enter Key Press
        if(connected) {
            //Forward Message (Message)
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, enterField.getText()));
            enterField.setText("");
            return;
        }


    }

    public void connectionFailed() {

        //Reset all storage variables
        logout.setEnabled(false);
        userlist.setEnabled(false);
        enterField.removeActionListener(this);

        connected = false;
    }

    //Try Custom Font
    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }
}
