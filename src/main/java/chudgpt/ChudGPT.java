package chudgpt;

import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.exception.ChudException;
import chudgpt.parser.Parser;
import chudgpt.storage.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;

/**
 * The entry point for the ChudGPT chatbot application.
 */
public class ChudGPT {
    private static final TaskList tasks = new TaskList();
    private static final Parser parser = new Parser();
    /** Relative location of the task data, kept portable across operating systems. */
    private static final Storage storage = new Storage(Path.of("data", "save.txt"));

    /**
     * Lists out the existing tasks, with additional information like
     * completed status.
     *
     * @return a string that lists out all tasks.
     */
    private static String listOut() {
        if (tasks.size() == 0) {
            return "Here are the tasks in your list:";
        }
        return "Here are the tasks in your list:\n" + tasks;
    }

    /**
     * Processes a command entered by the user.
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
            Task task = parser.parseTask(command);

            tasks.addTask(task);
            saveToFile();
            return "Got it. I've added this task:\n  " + task
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        } catch (ChudException e) {
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
        int index;
        try {
            index = parser.parseTaskIndex(command, isCompleted ? "mark" : "unmark");
        } catch (ChudException e) {
            return e.getMessage();
        }

        try {
            Task updatedTask = tasks.getTask(index);
            updatedTask.setCompleted(isCompleted);
            saveToFile();

            String prefix = isCompleted
                    ? "Nice! I've marked this task as done:"
                    : "OK, I've marked this task as not done yet:";
            return prefix + "\n  " + updatedTask;
        } catch (ChudException e) {
            return "Invalid task number.";
        }
    }

    /**
     * Deletes a task from the list.
     *
     * @param command the command entered by the user
     * @return the message ChudGPT should print out to the user.
     */
    private static String deleteTask(String command) {
        int index;
        try {
            index = parser.parseTaskIndex(command, "delete");
        } catch (ChudException e) {
            return e.getMessage();
        }

        try {
            Task deletedTask = tasks.deleteTask(index);
            saveToFile();

            return String.format("Noted. I've removed this task:\n  %s\nNow you have %d tasks in the list",
                    deletedTask, tasks.size());
        } catch (ChudException e) {
            return "Invalid task number.";
        }
    }

    /** Writes the current task list in the structured save format. */
    private static String saveToFile() {
        if (storage.save(tasks)) {
            return "I've saved your current list of tasks.";
        }
        return "OOPS!!! I couldn't save your current list of tasks :( I'm such a chud...";
    }

    /**
     * Starts the ChudGPT command-line application.
     *
     * @param args command-line arguments, which are not used.
     * @throws ChudException if the logo resource cannot be read.
     */
    public static void main(String[] args) throws ChudException {
        storage.loadInto(tasks);
        Scanner input = new Scanner(System.in);

        String logo;
        try (InputStream logoStream = ChudGPT.class.getResourceAsStream("/logo.txt")) {
            if (logoStream == null) {
                throw new ChudException("Could not find logo.txt on the classpath.");
            }
            logo = new String(logoStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ChudException(e.getMessage());
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
}