import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterFrame() {
        setTitle("Register");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        
        Font poppinsFont = new Font("Poppins", Font.PLAIN, 14);

       
        Color darkBlue = new Color(0, 0, 139);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        mainPanel.setBackground(darkBlue);

        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10)); 
        formPanel.setBackground(darkBlue); 

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE); 
        nameLabel.setFont(poppinsFont); 
        nameField = new JTextField();
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE); 
        usernameLabel.setFont(poppinsFont); 
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); 
        passwordLabel.setFont(poppinsFont); 
        passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");
        registerButton.setFont(poppinsFont); 
        JButton backButton = new JButton("Back");
        backButton.setFont(poppinsFont); 

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                boolean success = DatabaseHandler.registerUser(name, username, password);
                if (success) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Registration successful!");
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                new LoginFrame(); 
            }
        });

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(registerButton);
        formPanel.add(backButton);

        
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        mainPanel.add(formPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new RegisterFrame();
    }
}
