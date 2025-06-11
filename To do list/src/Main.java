import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Task {
    String name;
    LocalDate dueDate;

    Task(String name, LocalDate dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    long daysUntilDue() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    String getUrgencyLevel() {
        long days = daysUntilDue();
        if (days <= 2) return "High";
        else if (days <= 5) return "Medium";
        else return "Low";
    }

    @Override
    public String toString() {
        return name + " - Due: " + dueDate + " (" + getUrgencyLevel() + ")";
    }
}

class TaskManager {
    private final ArrayList<Task> tasks = new ArrayList<>();

    void loadTasksFromFile(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String name = parts[0].trim();
                LocalDate dueDate = LocalDate.parse(parts[1].trim());
                tasks.add(new Task(name, dueDate));
            }
        }
    }

    void sortTasksByUrgency() {
        tasks.sort(Comparator.comparingLong(Task::daysUntilDue));
    }

    ArrayList<Task> getTasks() {
        return tasks;
    }
}

public class Main extends JFrame {
    private final JTextArea displayArea = new JTextArea(20, 40);
    private final TaskManager taskManager = new TaskManager();

    public Main() {
        setTitle("Task Urgency Sorter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton loadButton = new JButton("Load Tasks");
        loadButton.addActionListener(e -> loadAndDisplayTasks());

        add(new JScrollPane(displayArea), BorderLayout.CENTER);
        add(loadButton, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadAndDisplayTasks() {
        try {
            taskManager.loadTasksFromFile("tasks.txt");
            taskManager.sortTasksByUrgency();
            displayTasks();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load tasks: " + e.getMessage());
        }
    }

    private void displayTasks() {
        displayArea.setText("");
        for (Task task : taskManager.getTasks()) {
            displayArea.append(task + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}