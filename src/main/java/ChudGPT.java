import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
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

    /**
     * Adds a task described by a user command.
     *
     * <p>The date and time portions are deliberately kept as strings because
     * this level of the project does not require date parsing.</p>
     *
     * @param command the complete task command entered by the user
     * @return the response to display after processing the command
     */
    private static String addTask(String command) {
        if (taskCount == tasks.length) {
            return "I can't store more than 100 tasks.";
        }

        String taskCommand = command.trim();
        String lowerCaseCommand = taskCommand.toLowerCase(Locale.ROOT);
        Task task;

        if (lowerCaseCommand.equals("todo") || lowerCaseCommand.startsWith("todo ")) {
            String description = taskCommand.substring(4).trim();
            if (description.isEmpty()) {
                return "Usage: todo <description>";
            }
            task = new ToDo(description);
        } else if (lowerCaseCommand.equals("deadline")
                || lowerCaseCommand.startsWith("deadline ")) {
            int byIndex = lowerCaseCommand.indexOf("/by");
            if (byIndex < 0) {
                return "Usage: deadline <description> /by <date/time>";
            }

            String description = taskCommand.substring(8, byIndex).trim();
            String submitBy = taskCommand.substring(byIndex + 3).trim();
            if (description.isEmpty() || submitBy.isEmpty()) {
                return "Usage: deadline <description> /by <date/time>";
            }
            task = new Deadline(description, submitBy);
        } else if (lowerCaseCommand.equals("event") || lowerCaseCommand.startsWith("event ")) {
            int fromIndex = lowerCaseCommand.indexOf("/from");
            int toIndex = lowerCaseCommand.indexOf("/to", fromIndex + 5);
            if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
                return "Usage: event <description> /from <start> /to <end>";
            }

            String description = taskCommand.substring(5, fromIndex).trim();
            String start = taskCommand.substring(fromIndex + 5, toIndex).trim();
            String end = taskCommand.substring(toIndex + 3).trim();
            if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
                return "Usage: event <description> /from <start> /to <end>";
            }
            task = new Event(description, start, end);
        } else {
            // Keep accepting the original free-form task syntax as a ToDo.
            task = new ToDo(taskCommand);
        }

        tasks[taskCount] = task;
        ++taskCount;
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
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

            String command = message.trim().toLowerCase(Locale.ROOT);
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
                message = addTask(message);
            }


            System.out.println("____________________________________________________________");
            System.out.println(message);
            System.out.println("____________________________________________________________");
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }

    /** A task with a description and completion state. */
    private static class Task {
        private boolean completed;
        /** The user-provided task description. */
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

    /** A task without an attached date or time. */
    private static class ToDo extends Task {
        public ToDo(String task) {
            super(task);
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    /** A task that must be completed by a user-provided date or time. */
    private static class Deadline extends Task {
        /** The raw date or time by which the task should be completed. */
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

    /** A task with user-provided start and end date/time strings. */
    private static class Event extends Task {
        /** The raw start date or time for the event. */
        private final String start;
        /** The raw end date or time for the event. */
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
