package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/**
 * Represents a command that saves the current task list and exits the application.
 */
public class ExitCommand extends Command {
    /**
     * Identifies this command as the command that terminates the application loop.
     *
     * @return {@code true} because this command exits the application
     */
    @Override
    public boolean isExit() {
        return true;
    }

    /**
     * Saves the current task list and displays the exit message.
     *
     * @param tasks the current task list
     * @param ui the UI handler used to display the exit message
     * @param storage the storage handler used to save the task list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        storage.save(tasks);
        ui.showByeMessage();
    }
}
