/*  [IntroFrame.java]
    JFrame handling the entry menu image for the program
    Author: Brian Zhang
    ICS4UE
    Date: 12/04/18
 */

//Imports
import javax.swing.*;
import java.awt.*;


public class IntroFrame extends JFrame {

    //Base Constructor
    public IntroFrame() {

        //Base Dimensions
        setSize(600, 600);
        setLocationRelativeTo(null);
        setTitle("loading...");

        //Decorations
        setResizable(true);
        setUndecorated(false);


        JPanel panel = new JPanel();
        panel.setLayout(null);

        //Initialize the Logo
        JLabel logo = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("ripplelogo.gif"))));
        JLabel loading = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("loading.png"))));
        JLabel backdrop = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("halfy.gif"))));

        //Splash quotes
        JLabel quoteOne = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("quote1.png"))));
        JLabel quoteTwo = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("quote2.png"))));
        JLabel quoteThree = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("quote3.png"))));
        JLabel quoteFour = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("quote4.png"))));
        JLabel quoteFive = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("quote5.png"))));
        JLabel quoteSix = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("quote6.png"))));
        JLabel quoteSeven = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("quote7.png"))));
        JLabel quoteEight = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("quote8.png"))));

        //Manual label integration (setLocation and setSize)
        panel.add(logo);
        logo.setLocation(160, 50);
        logo.setSize(260, 260);

        panel.add(loading);
        loading.setLocation(0, 315);
        loading.setSize(600, 100);

        //Quote integration
        int r = (int) Math.floor(Math.random() * 8);
        switch (r) {
            case 0:
                panel.add(quoteOne);
                quoteOne.setLocation(0, 415);
                quoteOne.setSize(600, 100);
                break;
            case 1:
                panel.add(quoteTwo);
                quoteTwo.setLocation(0, 415);
                quoteTwo.setSize(600, 100);
                break;
            case 2:
                panel.add(quoteThree);
                quoteThree.setLocation(0, 415);
                quoteThree.setSize(600, 100);
                break;
            case 3:
                panel.add(quoteFour);
                quoteFour.setLocation(0, 415);
                quoteFour.setSize(600, 100);
                break;
            case 4:
                panel.add(quoteFive);
                quoteFive.setLocation(0, 415);
                quoteFive.setSize(600, 100);
                break;
            case 5:
                panel.add(quoteSix);
                quoteSix.setLocation(0, 415);
                quoteSix.setSize(600, 100);
                break;
            case 6:
                panel.add(quoteSeven);
                quoteSeven.setLocation(0, 415);
                quoteSeven.setSize(600, 100);
                break;
            case 7:
                panel.add(quoteEight);
                quoteEight.setLocation(0, 415);
                quoteEight.setSize(600, 100);
                break;
        }

        //Background
        panel.add(backdrop);
        backdrop.setLocation(0, 0);
        backdrop.setSize(600, 600);

        //Setup JFrame components
        add(panel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            Thread.sleep(750);
            new ClientLogin();
            new ServerGUI(1500);
            dispose();
        } catch (InterruptedException e) {
        }

    }

    public static void main(String[] args) {
        new IntroFrame();
    }
}

