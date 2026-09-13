package chudgpt;

import chudgpt.command.Command;
import chudgpt.exception.ChudException;
import chudgpt.parser.Parser;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/**
 * The entry point for the ChudGPT chatbot application.
 */
public class ChudGpt {
    private final TaskList tasks;
    private final Parser parser;
    private final Ui ui;
    /** Relative location of the task data, kept portable across operating systems. */
    private final Storage storage;
    private boolean hasExited;

    /**
     * Creates a ChudGPT application backed by the specified data file.
     *
     * @param filePath path to the file used to load and save tasks.
     */
    public ChudGpt(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (ChudException e) {
            ui.display(ui.getLoadErrorMessage());
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
    }

    /** Runs the command-line application. */
    public void run() {
        ui.display(ui.getWelcomeMessage());

        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            ui.display(ui.getDivider());
            CommandResult result = executeCommand(input);
            updateExitStatus(result);
            ui.display(result.response());
            ui.display(ui.getDivider());
            isExit = hasExited;
        }
    }

    /**
     * Returns ChudGPT's response to one user command.
     *
     * @param input the command entered by the user.
     * @return ChudGPT's response.
     */
    public String getResponse(String input) {
        CommandResult result = executeCommand(input);
        updateExitStatus(result);
        return result.response();
    }

    /**
     * Returns whether ChudGPT has received an exit command.
     *
     * @return {@code true} if ChudGPT has received an exit command.
     */
    public boolean hasExited() {
        return hasExited;
    }

    /**
     * Returns ChudGPT's welcome message.
     *
     * @return ChudGPT's welcome message.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /** Executes one command and returns its response and exit status. */
    private CommandResult executeCommand(String input) {
        try {
            Command command = parser.parse(input);
            String response = command.execute(tasks, ui, storage);
            return new CommandResult(response, command.isExit());
        } catch (ChudException e) {
            return new CommandResult(ui.getErrorMessage(e.getMessage()), false);
        }
    }

    /** Updates the application exit status from a command result. */
    private void updateExitStatus(CommandResult result) {
        hasExited = hasExited || result.isExit();
    }

    /** Stores the outcome of executing one command. */
    private record CommandResult(String response, boolean isExit) {
    }

    /**
     * Starts the ChudGPT command-line application.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new ChudGpt("data/tasks.txt").run();
    }
}
