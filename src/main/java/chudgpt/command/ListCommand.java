package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/**
 * Represents a command that displays all tasks in the current task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the current task list through the UI handler.
     *
     * @param tasks the current task list to display
     * @param ui the UI handler used to display the tasks
     * @param storage the storage handler, which is not used by this command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.listTasks(tasks);
    }
}
