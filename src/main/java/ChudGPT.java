import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * The entry point for the ChudGPT chatbot application.
 */
public class ChudGPT {
    private static int taskCount = 0;
    private static final Task[] tasks = new Task[100];

    private static String listOut() {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            message.append("\n").append(i + 1).append(". ").append(tasks[i]);
        }

        return message.toString();
    }

    private static boolean addTask(String task) {
        if (taskCount == tasks.length) {
            return false;
        }
        tasks[taskCount] = new Task(task);
        ++taskCount;
        return true;
    }

    private static String changeTaskStatus(String command, boolean completed) {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            return "Usage: " + (completed ? "mark" : "unmark") + " <task number>";
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "Task number must be a number.";
        }

        int index = taskNumber - 1;
        if (index < 0 || index >= taskCount) {
            return "Invalid task number.";
        }

        tasks[index].setCompleted(completed);
        String prefix = completed
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return prefix + "\n  " + tasks[index];
    }

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        String logo;
        try (InputStream logoStream = ChudGPT.class.getResourceAsStream("/logo.txt")) {
            if (logoStream == null) {
                throw new IOException("Could not find logo.txt on the classpath.");
            }
            logo = new String(logoStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        System.out.println("____________________________________________________________");
        System.out.println(logo);
        System.out.println("Hello! I'm ChudGPT.\nWhat can I do for you?");
        System.out.println("____________________________________________________________");


        while (input.hasNextLine()) {
            String message = input.nextLine();

            if (message.isBlank()) {
                continue;
            }

            String command = message.trim().toLowerCase();
            if (command.equalsIgnoreCase("bye")) {
                break;
            } else if (command.equalsIgnoreCase("hi")) {
                message = "Hi, I'm ChudGPT. How can I help you?";
            } else if (command.equalsIgnoreCase("list")) {
                message = listOut();
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                message = changeTaskStatus(command, true);
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                message = changeTaskStatus(command, false);
            } else {
                if (addTask(message)) {
                    message = "added: " + command;
                } else {
                    message = "I can't store more than 100 tasks.";
                }
            }


            System.out.println("____________________________________________________________");
            System.out.println(message);
            System.out.println("____________________________________________________________");
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }

    private static class Task {
        private boolean completed;
        private final String task;
        public Task(String task) {
            this.task = task;
            completed = false;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s", completed ? "X" : " ", task);
        }
    }
}
