import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * The entry point for the ChudGPT chatbot application.
 */
public class ChudGPT {
    private static int taskCount = 0;
    private static final ArrayList<Task> tasks = new ArrayList<>();

    private static String listOut() {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            message.append("\n").append(i + 1).append(". ").append(tasks.get(i));
        }

        return message.toString();
    }

    /**
     * Processes a command entered by the user.
     *
     * <p>The command may add a task, list tasks, or change a task's completion
     * status. The date and time portions of task commands are deliberately kept
     * as strings because this level of the project does not require date
     * parsing.</p>
     *
     * @param command the complete command entered by the user
     * @return the response to display after processing the command
     */
    private static String handleCommand(String command) {
        String taskCommand = command.trim();
        String lowerCaseCommand = taskCommand.toLowerCase(Locale.ROOT);

        if (lowerCaseCommand.equals("list")) {
            return listOut();
        } else if (lowerCaseCommand.equals("mark") || lowerCaseCommand.startsWith("mark ")) {
            return changeTaskStatus(taskCommand, true);
        } else if (lowerCaseCommand.equals("unmark") || lowerCaseCommand.startsWith("unmark ")) {
            return changeTaskStatus(taskCommand, false);
        } else if (lowerCaseCommand.equals("delete") || lowerCaseCommand.startsWith("delete ")) {
            return deleteTask(taskCommand);
        }

        try {
            Task task;

            if (lowerCaseCommand.equals("todo") || lowerCaseCommand.startsWith("todo ")) {
                String description = taskCommand.substring(4).trim();
                if (description.isEmpty()) {
                    throw new IllegalArgumentException("OOPS!!! The description of a todo cannot be empty.");
                }
                task = new ToDo(description);
            } else if (lowerCaseCommand.equals("deadline")
                    || lowerCaseCommand.startsWith("deadline ")) {
                int byIndex = lowerCaseCommand.indexOf("/by");
                if (byIndex < 0) {
                        throw new IllegalArgumentException("OOPS!!! There must be a /by argument passed in.");
                }

                String description = taskCommand.substring(8, byIndex).trim();
                String submitBy = taskCommand.substring(byIndex + 3).trim();
                if (description.isEmpty() || submitBy.isEmpty()) {
                    throw new IllegalArgumentException("OOPS!!! The description of a deadline cannot be empty");
                }
                task = new Deadline(description, submitBy);
            } else if (lowerCaseCommand.equals("event") || lowerCaseCommand.startsWith("event ")) {
                int fromIndex = lowerCaseCommand.indexOf("/from");
                int toIndex = lowerCaseCommand.indexOf("/to", fromIndex + 5);
                if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
                    throw new IllegalArgumentException("OOPS!!! Specify the /from argument before the /to argument");
                }

                String description = taskCommand.substring(5, fromIndex).trim();
                String start = taskCommand.substring(fromIndex + 5, toIndex).trim();
                String end = taskCommand.substring(toIndex + 3).trim();
                if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    throw new IllegalArgumentException("OOPS!!! The description, start and end date of an event cannot be empty");
                }
                task = new Event(description, start, end);
            } else {
                throw new IllegalArgumentException("OOPS!!! I'm sorry, but I don't know what that means :( I'm such a chud...");
            }

            tasks.add(task);
            ++taskCount;
            return "Got it. I've added this task:\n  " + task
                    + "\nNow you have " + taskCount + " tasks in the list.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
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

        tasks.get(index).setCompleted(completed);
        String prefix = completed
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return prefix + "\n  " + tasks.get(index);
    }

    private static String deleteTask(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            return "Usage: delete <task number>";
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

        String message = String.format("Noted. I've removed this task:\n  %s\nNow you have %d tasks in the list",
                tasks.get(index), taskCount - 1);
        tasks.remove(index);
        --taskCount;

        return message;
    }

    static void main(String[] args) throws IOException {
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

            String command = message.trim().toLowerCase(Locale.ROOT);
            if (command.equalsIgnoreCase("bye")) {
                break;
            } else if (command.startsWith("hi")) {
                message = "Hi, I'm ChudGPT. How can I help you?";
            } else {
                message = handleCommand(message);
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

    private static class ToDo extends Task {
        public ToDo(String task) {
            super(task);
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    private static class Deadline extends Task {
        private final String submitBy;

        public Deadline(String task, String submitBy) {
            super(task);
            this.submitBy = submitBy;
        }

        @Override
        public String toString() {
            return String.format("[D]%s (by: %s)", super.toString(), submitBy);
        }
    }

    private static class Event extends Task {
        private final String start;
        private final String end;

        public Event(String task, String start, String end) {
            super(task);
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("[E]%s (from: %s to: %s)", super.toString(), start, end);
        }
    }
}
