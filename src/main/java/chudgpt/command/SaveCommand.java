package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/**
 * Represents a command that saves the current task list on demand.
 */
public class SaveCommand extends Command {
    /**
     * Saves the current task list and displays a confirmation message.
     *
     * @param tasks the current task list to save
     * @param ui the UI handler used to display the confirmation
     * @param storage the storage handler used to save the task list
     * @throws ChudException if the task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        storage.save(tasks);
        ui.showSaveMessage();
    }
}
