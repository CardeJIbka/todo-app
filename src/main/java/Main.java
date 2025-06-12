import dao.DatabaseHandler;
import model.Task;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static DatabaseHandler dbHandler = new DatabaseHandler();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    showAllTasks();
                    break;
                case 3:
                    showTaskById();
                    break;
                case 4:
                    updateTask();
                    break;
                case 5:
                    deleteTask();
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }

        dbHandler.closeConnection();
        System.out.println("Программа завершена.");
    }

    private static void printMenu() {
        System.out.println("\n=== Список дел ===");
        System.out.println("1. Добавить задачу");
        System.out.println("2. Показать все задачи");
        System.out.println("3. Показать задачу по ID");
        System.out.println("4. Обновить задачу");
        System.out.println("5. Удалить задачу");
        System.out.println("6. Выход");
    }

    private static void addTask() {
        System.out.println("\n--- Добавление новой задачи ---");
        String title = getStringInput("Введите заголовок: ");
        String description = getStringInput("Введите описание: ");

        Task task = new Task(title, description);
        if (dbHandler.addTask(task)) {
            System.out.println("Задача успешно добавлена!");
        } else {
            System.out.println("Не удалось добавить задачу.");
        }
    }

    private static void showAllTasks() {
        System.out.println("\n--- Список всех задач ---");
        List<Task> tasks = dbHandler.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст.");
        } else {
            for (Task task : tasks) {
                System.out.println(task);
            }
        }
    }

    private static void showTaskById() {
        System.out.println("\n--- Просмотр задачи по ID ---");
        int id = getIntInput("Введите ID задачи: ");
        Task task = dbHandler.getTaskById(id);

        if (task != null) {
            System.out.println("\nДетали задачи:");
            System.out.println("ID: " + task.getId());
            System.out.println("Заголовок: " + task.getTitle());
            System.out.println("Описание: " + task.getDescription());
            System.out.println("Статус: " + task.getStatus());
            System.out.println("Дата создания: " + task.getCreatedAt());
        } else {
            System.out.println("Задача с ID " + id + " не найдена.");
        }
    }

    private static void updateTask() {
        System.out.println("\n--- Обновление задачи ---");
        int id = getIntInput("Введите ID задачи для обновления: ");
        Task task = dbHandler.getTaskById(id);

        if (task == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
            return;
        }

        System.out.println("Текущие данные задачи:");
        System.out.println("1. Заголовок: " + task.getTitle());
        System.out.println("2. Описание: " + task.getDescription());
        System.out.println("3. Статус: " + task.getStatus());

        System.out.println("\nВведите новые данные (оставьте пустым, чтобы не изменять):");
        String title = getStringInput("Новый заголовок: ", false);
        String description = getStringInput("Новое описание: ", false);
        String status = getStatusInput();

        if (title != null && !title.isEmpty()) task.setTitle(title);
        if (description != null && !description.isEmpty()) task.setDescription(description);
        if (status != null) task.setStatus(status);

        if (dbHandler.updateTask(task)) {
            System.out.println("Задача успешно обновлена!");
        } else {
            System.out.println("Не удалось обновить задачу.");
        }
    }

    private static void deleteTask() {
        System.out.println("\n--- Удаление задачи ---");
        int id = getIntInput("Введите ID задачи для удаления: ");

        if (dbHandler.deleteTask(id)) {
            System.out.println("Задача успешно удалена!");
        } else {
            System.out.println("Не удалось удалить задачу. Возможно, указанный ID не существует.");
        }
    }

    // Вспомогательные методы для ввода
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private static String getStringInput(String prompt) {
        return getStringInput(prompt, true);
    }

    private static String getStringInput(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!required || !input.isEmpty()) {
                return input;
            }
            System.out.println("Это поле обязательно для заполнения.");
        }
    }

    private static String getStatusInput() {
        System.out.println("Выберите статус:");
        System.out.println("1. NEW");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. DONE");
        System.out.println("0. Оставить текущий");

        int choice = getIntInput("Ваш выбор: ");

        switch (choice) {
            case 1: return "NEW";
            case 2: return "IN_PROGRESS";
            case 3: return "DONE";
            default: return null;
        }
    }
}