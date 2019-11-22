/*  [ClientLogin.java]
    JFrame handling basic connection to the server
    Author: Brian Zhang
    ICS4UE
    Date: 12/04/18
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ClientLogin extends JFrame implements ActionListener {

    //UserName retrieval
    private JLabel text;

    private JTextField enterField;
    private JTextField serverField, portField;

    private JLabel text2;
    private JPasswordField checkField;
    private JLabel text3;

    //Password for entry
    private String password = "harvardlawschool";

    //Button SetUp
    private JButton login;

    //Constructor extending JFrame
    ClientLogin(){

        super("harmony login");

        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("renogare.otf")));
        } catch (IOException | FontFormatException e) {
        }

        //Custom UI Font
        setUIFont(new javax.swing.plaf.FontUIResource("renogare", Font.PLAIN, 12));

        //GUI Layout Management [CardLayout]
        JPanel upperPanel = new JPanel(new GridLayout(8,1,0,50));
        JPanel dataPanel = new JPanel(new GridLayout(1,1,0,0));

        //Spacer
        upperPanel.add(new JLabel(""));

        serverField = new JTextField();
        serverField.setBackground(new Color(254, 234, 255));
        portField = new JTextField();
        portField.setBackground(new Color(254, 234, 255));
        portField.setHorizontalAlignment(SwingConstants.RIGHT);

        dataPanel.add(new JLabel("|server address|:  "));
        dataPanel.add(serverField);
        dataPanel.add(new JLabel("|port number|:  ",SwingConstants.RIGHT));
        dataPanel.add(portField);
        dataPanel.add(new JLabel(""));


        upperPanel.add(dataPanel);

        // the Label and the TextField
        text = new JLabel("|enter your username below|", SwingConstants.CENTER);
        text.setFont(new javax.swing.plaf.FontUIResource("renogare", Font.PLAIN, 18));

        upperPanel.add(text);
        enterField = new JTextField("anon");
        enterField.setBackground(new Color(255, 231, 234));
        upperPanel.add(enterField);
        text2 = new JLabel("|enter password below|", SwingConstants.CENTER);
        text2.setFont(new javax.swing.plaf.FontUIResource("renogare", Font.PLAIN, 18));
        upperPanel.add(text2);
        checkField = new JPasswordField("");
        checkField.setEchoChar('*');
        checkField.setBackground(new Color(255, 231, 234));
        upperPanel.add(checkField);
        text3 = new JLabel("", SwingConstants.CENTER);
        upperPanel.add(text3);
        add(upperPanel, BorderLayout.NORTH);

        //Buttons
        login = new JButton("|login|");
        //Action Listener
        login.addActionListener(this);

        JPanel southPanel = new JPanel();
        southPanel.add(login);
        add(southPanel, BorderLayout.SOUTH);

        //Frame Setup
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        enterField.requestFocus();
    }

    public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();

        if(button == login) {
            String username = enterField.getText().trim();
            if(username.length() == 0) {
                return;
            }
            String server = serverField.getText().trim();
            if(server.length() == 0) {
                return;
            }

            String portNumber = portField.getText().trim();
            if(portNumber.length() == 0) {
                return;
            }
            int port = 0;
            try {
                port = Integer.parseInt(portNumber);
            }
            catch(Exception en) {
                return;
            }

            //Server Connected Check

            //Password Check
            String codeCheck = checkField.getText().trim();
            if(!codeCheck.matches(password)) {
                text3.setText("!incorrect password!");
                return;
            }

            //Forward collected information to ClientInterface
            ClientInterface next = new ClientInterface(server,port,username);
            dispose();
        }

    }

    //Custom Font Method
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
