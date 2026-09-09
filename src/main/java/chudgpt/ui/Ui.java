package chudgpt.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import chudgpt.ChudGPT;
import chudgpt.exception.ChudException;
import chudgpt.task.Task;
import chudgpt.task.TaskList;

/** Handles console input and output for the ChudGPT application. */
public class Ui {
    private final Scanner input = new Scanner(System.in);

    /** Shows the welcome message. */
    public void showWelcomeMessage() {
        showLine();
        try {
            showLogo();
        } catch (ChudException e) {
            System.out.println("OOPS!!! I can't find the logo :( I'm such a chud...");
        }
        System.out.println("Hello! I'm ChudGPT.\nWhat can I do for you?");
        showLine();
    }

    /**
     * Displays the ChudGPT logo.
     *
     * @throws ChudException if the logo resource cannot be read.
     */
    public void showLogo() throws ChudException {
        String logo;
        try (InputStream logoStream = ChudGPT.class.getResourceAsStream("/logo.txt")) {
            if (logoStream == null) {
                throw new ChudException("Could not find logo.txt on the classpath.");
            }
            logo = new String(logoStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ChudException(e.getMessage());
        }

        System.out.println(logo);
    }

    /**
     * Lists the current tasks.
     *
     * @param tasks the current tasks in your list.
     */
    public void listTasks(TaskList tasks) {
        if (tasks.size() == 0) {
            System.out.println("You have no tasks in your list! Try adding some");
            return;
        }
        System.out.println("Here are the tasks in your list:\n" + tasks);
    }

    /**
     * Lists the matched tasks.
     *
     * @param matches the matched tasks that were found.
     */
    public void listMatches(TaskList matches) {
        if (matches.size() == 0) {
            System.out.println("I couldn't find any tasks that contains that keyword.");
            return;
        }

        System.out.println("Here are the matching tasks in your list:\n" + matches);
    }

    /** Displays the greeting message. */
    public void showHiMessage() {
        System.out.println("Hi! I'm ChudGPT. How can I help you?");
    }

    /** Displays the goodbye message. */
    public void showByeMessage() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays the message after adding a task.
     *
     * @param task the task that was added.
     * @param size the updated size of the task list.
     */
    public void showAddTaskMessage(Task task, int size) {
        System.out.println("Got it. I've added this task:\n  " + task);
        showListSizeMessage(size);
    }

    /**
     * Displays the message after deleting a task.
     *
     * @param task the task that was deleted.
     * @param size the updated size of the task list.
     */
    public void showDeleteTaskMessage(Task task, int size) {
        System.out.println("Got it. Noted. I've removed this task:\n  " + task);
        showListSizeMessage(size);
    }

    /**
     * Displays the message after changing a task's completion status.
     *
     * @param task task whose status was changed.
     */
    public void showUpdateTaskMessage(Task task) {
        System.out.println(task.isCompleted()
                ? "Nice! I've marked this task as completed!"
                : "OK, I've marked this task as incomplete." + "\n  " +
                task);
    }

    /** Displays the message showing updated task list size. */
    private void showListSizeMessage(int size) {
        System.out.println("Now you have " + size + " tasks in your list.");
    }

    /** Displays the save message. */
    public void showSaveMessage() {
        System.out.println("I've saved your current list of tasks.");
    }

    /**
     * Displays an error message.
     *
     * @param errMessage the message associated with the error.
     */
    public void showErrorMessage(String errMessage) {
        System.out.println("OOPS!!! I've run into an error :( I'm such a chud...\nDetails:\n  " + errMessage);
    }

    /**
     * Reads the next command from standard input.
     *
     * @return next command entered by the user.
     */
    public String readCommand() {
        return input.nextLine();
    }

    /** Shows the divider line. */
    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    /** Shows the error message when unable to load the save file. */
    public void showLoadError() {
        System.out.println("OOPS!!! I couldn't retrieve the save file :( I'm such a chud...");
    }
}
