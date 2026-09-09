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
public class ChudGPT {
    private final TaskList tasks;
    private final Parser parser;
    private final Ui ui;
    /** Relative location of the task data, kept portable across operating systems. */
    private final Storage storage;

    /**
     * Creates a ChudGPT application backed by the specified data file.
     *
     * @param filePath path to the file used to load and save tasks.
     */
    public ChudGPT(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (ChudException e) {
            ui.showLoadError();
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
    }

    /** Runs the command-line application. */
    public void run() {
        ui.showWelcomeMessage();

        boolean isExit = false;
        while (!isExit) {
            try {
                String command = ui.readCommand();
                ui.showLine();
                Command c = parser.parse(command);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (ChudException e) {
                ui.showErrorMessage(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts the ChudGPT command-line application.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new ChudGPT("data/tasks.txt").run();
    }
}