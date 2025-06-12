package dao;

import model.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseHandler {
    private Connection connection;

    public DatabaseHandler() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            // Загрузка конфигурации из файла
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("src/main/resources/config.properties");
            props.load(in);
            in.close();

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Установка соединения
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Успешное подключение к базе данных.");
        } catch (SQLException | IOException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    // Добавление задачи
    public boolean addTask(Task task) {
        String sql = "INSERT INTO tasks(title, description, status) VALUES(?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении задачи: " + e.getMessage());
            return false;
        }
    }

    // Получение всех задач
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getInt("id"));
                task.setTitle(resultSet.getString("title"));
                task.setDescription(resultSet.getString("description"));
                task.setStatus(resultSet.getString("status"));
                task.setCreatedAt(resultSet.getTimestamp("created_at"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении задач: " + e.getMessage());
        }
        return tasks;
    }

    // Получение задачи по ID
    public Task getTaskById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        Task task = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                task = new Task();
                task.setId(resultSet.getInt("id"));
                task.setTitle(resultSet.getString("title"));
                task.setDescription(resultSet.getString("description"));
                task.setStatus(resultSet.getString("status"));
                task.setCreatedAt(resultSet.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении задачи: " + e.getMessage());
        }
        return task;
    }

    // Обновление задачи
    public boolean updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus());
            statement.setInt(4, task.getId());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении задачи: " + e.getMessage());
            return false;
        }
    }

    // Удаление задачи
    public boolean deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении задачи: " + e.getMessage());
            return false;
        }
    }

    // Закрытие соединения
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}