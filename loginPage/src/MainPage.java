import AdminPage.AdminPanel;
import UserPage.MainBilling;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class MainPage {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put( "Button.arc", 999 );
            UIManager.put( "Component.arc", 999 );
            UIManager.put( "ProgressBar.arc", 999 );
            UIManager.put( "TextComponent.arc", 999 );
            UIManager.put("Button.background", new Color(30, 144, 255));  // DodgerBlue
            UIManager.put("Button.foreground", Color.WHITE);

        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        JFrame frame = new JFrame(" Login Form");
        frame.setSize(400, 400); // Bigger frame to center panel inside
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        // Create panel (form container)
        JPanel panel = new JPanel();
        panel.setSize(400, 400);
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
//        panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 10, true)); // Rounded border

        // Center the panel in the frame
        int x = (frame.getWidth() - panel.getWidth()) / 2;
        int y = (frame.getHeight() - panel.getHeight()) / 2;
        panel.setBounds(x, y, 400, 400);

        // Title
        JLabel title = new JLabel("Login");
        title.setFont(new Font("MV Boil", Font.BOLD, 12));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(150, 30, 100, 30);
        panel.add(title);

        // Username label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("MV Boil", Font.BOLD, 12));
        userLabel.setBounds(50, 90, 100, 25);
        panel.add(userLabel);

        // Username input
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("MV Boil", Font.BOLD, 12));
        usernameField.setBounds(50, 120, 300, 30);
        panel.add(usernameField);

        // Password label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("MV Boil", Font.BOLD, 12));
        passLabel.setBounds(50, 170, 100, 25);
        panel.add(passLabel);

        // Password input
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordField.setBounds(50, 200, 300, 30);
        panel.add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(243, 33, 33));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        loginButton.setBounds(130, 270, 140, 40); // center in panel
        panel.add(loginButton);

        // Button action
        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            UIManager.put("OptionPane.messageFont", new Font("MV Boil", Font.BOLD, 20));

            if (username.equals("1") && password.equals("1")) {
                 JOptionPane.showMessageDialog(frame, "Admin Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new AdminPanel();
            }
            else if(username.equals("2") && password.equals("2")) {
                JOptionPane.showMessageDialog(frame, "User Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new MainBilling();

            }
            else if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username or Password is Empty", "Login Failed", JOptionPane.ERROR_MESSAGE);

            }

            else {
                JOptionPane.showMessageDialog(frame, "Wrong Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
