import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Login");
        setSize(450, 300); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        
        Font poppinsFont = new Font("Poppins", Font.PLAIN, 14);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10)); 
        panel.setBackground(new Color(0, 0, 139)); // Set dark blue background color
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE); 
        usernameLabel.setFont(poppinsFont);
        usernameField = new JTextField();
        usernameField.setFont(poppinsFont);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); 
        passwordLabel.setFont(poppinsFont);
        passwordField = new JPasswordField();
        passwordField.setFont(poppinsFont);
        JButton loginButton = new JButton("Login");
        loginButton.setFont(poppinsFont);
        JButton registerButton = new JButton("Register");
        registerButton.setFont(poppinsFont);

       
        Dimension buttonSize = new Dimension(100, 30);
        loginButton.setPreferredSize(buttonSize);
        registerButton.setPreferredSize(buttonSize);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

              
                if (DatabaseHandler.loginUser(username, password)) {
                    dispose(); 
                    new MainWindow(); 
                } else {
                    
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterFrame();
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        setContentPane(panel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}



