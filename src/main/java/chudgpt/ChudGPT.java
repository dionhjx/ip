package chudgpt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * The entry point for the ChudGPT chatbot application.
 */
public class ChudGPT {
    /** Relative location of the task data, kept portable across operating systems. */
    private static final Path SAVE_FILE = Path.of("data", "save.txt");
    private static int taskCount = 0;
    private static final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Lists out the existing tasks, with additional information like
     * completed status.
     *
     * @return a string that lists out all tasks.
     */
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
     * The command may add a task, list tasks, or change a task's completion
     * status. The date and time portions of task commands are deliberately kept
     * as strings because this level of the project does not require date
     * parsing.
     *
     * @param command the complete command entered by the user
     * @return the response to display after processing the command.
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
        } else if (lowerCaseCommand.equals("save") || lowerCaseCommand.startsWith("save ")) {
            return saveToFile();
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
                    throw new IllegalArgumentException("""
                            OOPS!!! The description, start and end date of an event cannot be empty
                            """);
                }
                task = new Event(description, start, end);
            } else {
                throw new IllegalArgumentException("""
                    OOPS!!! I'm sorry, but I don't know what that means :( I'm such a chud...
                """);
            }

            tasks.add(task);
            ++taskCount;
            saveToFile();
            return "Got it. I've added this task:\n  " + task
                    + "\nNow you have " + taskCount + " tasks in the list.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    /**
     * Changes the status of a task.
     *
     * @param command the command entered by the user
     * @param isCompleted the target status to update the task to
     * @return the message ChudGPT should print out to the user.
     */
    private static String changeTaskStatus(String command, boolean isCompleted) {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            return "Usage: " + (isCompleted ? "mark" : "unmark") + " <task number>";
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

        tasks.get(index).setCompleted(isCompleted);
        saveToFile();
        String prefix = isCompleted
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return prefix + "\n  " + tasks.get(index);
    }

    /**
     * Deletes a task from the list.
     *
     * @param command the command entered by the user
     * @return the message ChudGPT should print out to the user.
     */
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
        saveToFile();

        return message;
    }

    /** Writes the current task list in the structured save format. */
    private static String saveToFile() {
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            StringBuilder message = new StringBuilder();
            for (Task task : tasks) {
                message.append(task.toSaveMessage()).append(System.lineSeparator());
            }
            Files.writeString(SAVE_FILE, message.toString(), StandardCharsets.UTF_8);
            return "I've saved your current list of tasks.";
        } catch (IOException e) {
            System.err.println("Error saving task list to file.");
            return "OOPS!!! I couldn't save your current list of tasks :( I'm such a chud...";
        }
    }

    /**
     * Loads saved tasks if the save file exists.
     *
     * <p>A missing file is expected when ChudGPT is run for the first time, so
     * it is treated as an empty task list. Malformed lines are skipped so one
     * damaged entry does not prevent the chatbot from starting.</p>
     */
    private static void loadFromFile() {
        if (!Files.exists(SAVE_FILE)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(SAVE_FILE, StandardCharsets.UTF_8)) {
                Task task = parseSavedTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            taskCount = tasks.size();
        } catch (IOException e) {
            System.err.println("Error loading task list from file.");
        }
    }

    /**
     * Returns the task represented by a saved task record, or {@code null} if the record is malformed.
     *
     * @param line the serialized task record read from the save file.
     * @return the parsed task, or {@code null} when the record cannot be parsed.
     */
    private static Task parseSavedTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            return null;
        }

        boolean isCompleted;
        if (parts[1].equals("0")) {
            isCompleted = false;
        } else if (parts[1].equals("1")) {
            isCompleted = true;
        } else {
            return null;
        }

        Task task;
        if (parts[0].equals("T") && parts.length == 3 && !parts[2].isBlank()) {
            task = new ToDo(parts[2]);
        } else if (parts[0].equals("D") && parts.length == 4
                && !parts[2].isBlank() && !parts[3].isBlank()) {
            task = new Deadline(parts[2], parts[3]);
        } else if (parts[0].equals("E") && parts.length == 5
                && !parts[2].isBlank() && !parts[3].isBlank() && !parts[4].isBlank()) {
            task = new Event(parts[2], parts[3], parts[4]);
        } else {
            return null;
        }

        task.setCompleted(isCompleted);
        return task;
    }

    /**
     * Starts the ChudGPT command-line application.
     *
     * @param args command-line arguments, which are not used.
     * @throws IOException if the logo resource cannot be read.
     */
    public static void main(String[] args) throws IOException {
        loadFromFile();
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

    /** Common state and display behavior shared by all supported task types. */
    private abstract static class Task {
        protected boolean isCompleted;
        protected final String task;

        private Task(String task) {
            this.task = task;
            isCompleted = false;
        }

        public void setCompleted(boolean isCompleted) {
            this.isCompleted = isCompleted;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s", isCompleted ? "X" : " ", task);
        }

        /**
         * Converts a task to its representation in the save file.
         *
         * @return representation of the task in the save file.
         */
        public abstract String toSaveMessage();
    }

    /** A task without a deadline or event times. */
    private static class ToDo extends Task {
        private ToDo(String task) {
            super(task);
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }

        @Override
        public String toSaveMessage() {
            return String.format("T | %d | %s", isCompleted ? 1 : 0, task);
        }
    }

    /** A task that must be completed by a specified time. */
    private static class Deadline extends Task {
        private final String submitBy;

        private Deadline(String task, String submitBy) {
            super(task);
            this.submitBy = submitBy;
        }

        @Override
        public String toString() {
            return String.format("[D]%s (by: %s)", super.toString(), submitBy);
        }

        @Override
        public String toSaveMessage() {
            return String.format("D | %d | %s | %s", isCompleted ? 1 : 0, task, submitBy);
        }
    }

    /** A task that takes place during a specified time range. */
    private static class Event extends Task {
        private final String start;
        private final String end;

        private Event(String task, String start, String end) {
            super(task);
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("[E]%s (from: %s to: %s)", super.toString(), start, end);
        }

        @Override
        public String toSaveMessage() {
            return String.format("E | %d | %s | %s | %s", isCompleted ? 1 : 0, task, start, end);
        }
    }
}
