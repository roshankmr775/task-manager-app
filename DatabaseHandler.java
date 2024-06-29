import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {
    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE_NAME = "mydatabase";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "roshan@1";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME;

    private DatabaseHandler() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static boolean registerUser(String name, String username, String password) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO users (name, username, password) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                statement.setString(2, username);
                statement.setString(3, password);
                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            // Log the exception
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loginUser(String username, String password) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            // Log the exception
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addTask(String description, int priority, int userId) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO tasks (task_description, priority, user_id) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, description);
                statement.setInt(2, priority);
                statement.setInt(3, userId);
                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            // Log the exception
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateCompletionPercentage(int taskId, double completionPercentage) {
        try (Connection connection = getConnection()) {
            String query = "UPDATE tasks SET completion_percentage = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDouble(1, completionPercentage);
                statement.setInt(2, taskId);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Completion percentage updated for task " + taskId);
                    return true;
                } else {
                    System.out.println("No task found with ID " + taskId);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating completion percentage for task " + taskId + ": " + e.getMessage());
            return false;
        }
    }
    
    

    public static Map<String, Double> getTasksWithCompletionPercentage() {
        Map<String, Double> tasksWithCompletionPercentage = new HashMap<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT task_description, completion_percentage FROM tasks";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String taskDescription = resultSet.getString("task_description");
                    double completionPercentage = resultSet.getDouble("completion_percentage");
                    tasksWithCompletionPercentage.put(taskDescription, completionPercentage);
                }
            }
        } catch (SQLException e) {
            // Log the exception
            e.printStackTrace();
        }
        return tasksWithCompletionPercentage;
    }

    public static void createTasksTable() {
        try (Connection connection = getConnection()) {
            String query = "CREATE TABLE IF NOT EXISTS tasks (id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "task_description VARCHAR(255) NOT NULL, priority INT, " +
                    "user_id INT, completion_percentage DOUBLE DEFAULT 0.0)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            // Log the exception
            e.printStackTrace();
        }
    }
}
