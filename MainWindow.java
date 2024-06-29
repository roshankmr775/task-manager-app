import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

public class MainWindow extends JFrame {
    private JTable taskTable;
    private DefaultTableModel tableModel;

    private static final String URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "roshan@1";
    private static final Color DARK_BLUE = new Color(0, 0, 139);

    public MainWindow() {
        setTitle("To-Do List");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Font defaultFont = new Font("Poppins", Font.PLAIN, 14);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("Welcome to Your To-Do List");
        titleLabel.setFont(new Font(defaultFont.getName(), Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(DARK_BLUE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(defaultFont);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });
        buttonPanel.add(logoutButton);

        JButton addButton = new JButton("Add Task");
        addButton.setFont(defaultFont);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = JOptionPane.showInputDialog(MainWindow.this, "Enter task:");
                if (task != null && !task.trim().isEmpty()) {
                    addTaskToDatabase(task);
                }
            }
        });
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Task");
        deleteButton.setFont(defaultFont);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTaskFromDatabase();
            }
        });
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        String[] columnNames = {"Serial Number", "Task Description", "Creation Time", "Completion Percentage"};
        tableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(tableModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) return Double.class;
                return super.getColumnClass(column);
            }
        };
        taskTable.setFont(defaultFont);
        taskTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);

        populateTaskTable();
        displayTasksWithCompletionPercentage(); // Added to display tasks with completion percentage
        
        // Customize cell renderer for the "Completion Percentage" column
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Double completionPercentage = (Double) value;
                Color color;
                if (completionPercentage < 50) {
                    color = Color.RED;
                } else if (completionPercentage < 90) {
                    color = Color.ORANGE;
                } else {
                    color = Color.GREEN;
                }
                component.setForeground(color); // Set text color based on completion percentage
                return component;
            }
        };
        taskTable.getColumnModel().getColumn(3).setCellRenderer(renderer);
    }

    private void populateTaskTable() {
        tableModel.setRowCount(0);
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM tasks";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            int serialNumber = 1;

            while (resultSet.next()) {
                String taskDescription = resultSet.getString("task_description");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                double completionPercentage = resultSet.getDouble("completion_percentage");
                Object[] rowData = {serialNumber++, taskDescription, createdAt, completionPercentage};
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database");
        }
    }

    private void addTaskToDatabase(String task) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String queryCheck = "SELECT * FROM tasks WHERE task_description = ?";
            PreparedStatement statementCheck = connection.prepareStatement(queryCheck);
            statementCheck.setString(1, task);
            ResultSet resultSet = statementCheck.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Task already exists!");
                return;
            }
    
            String query = "INSERT INTO tasks (task_description) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, task);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                populateTaskTable(); // Refresh the task table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add task to database");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database");
        }
    }
    
    private void deleteTaskFromDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                String taskDescription = (String) tableModel.getValueAt(selectedRow, 1);
                String query = "DELETE FROM tasks WHERE task_description = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, taskDescription);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete task from database");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a task to delete");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete task from database");
        }
    }

    private void updateTaskCompletionPercentage(int taskId, double completionPercentage) {
        boolean updated = DatabaseHandler.updateCompletionPercentage(taskId, completionPercentage);
        if (updated) {
            System.out.println("Completion percentage updated for task " + taskId);
            // Refresh the task table after updating completion percentage
            populateTaskTable();
        } else {
            System.out.println("Failed to update completion percentage for task " + taskId);
        }
    }
    

    private void displayTasksWithCompletionPercentage() {
        Map<String, Double> tasksWithCompletionPercentage = DatabaseHandler.getTasksWithCompletionPercentage();
        for (Map.Entry<String, Double> entry : tasksWithCompletionPercentage.entrySet()) {
            String taskDescription = entry.getKey();
            double completionPercentage = entry.getValue();
            String color;
            if (completionPercentage < 50) {
                color = "red";
            } else if (completionPercentage < 90) {
                color = "orange";
            } else {
                color = "green";
            }
            // Use the color to render the box for the task
            System.out.println("Task: " + taskDescription + ", Completion Percentage: " + completionPercentage + ", Color: " + color);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }
}
