package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Displays all tasks in the current task list. */
public class ListCommand extends Command {
    /**
     * Returns the current task list message created by the UI handler.
     *
     * @param tasks the current task list to display
     * @param ui the UI handler used to create the task-list message
     * @param storage the storage handler, which is not used by this command
     * @return the response listing the tasks
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.getTaskListMessage(tasks);
    }
}
