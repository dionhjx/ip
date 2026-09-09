package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/**
 * Represents a command that removes a task from the current task list.
 */
public class DeleteTaskCommand extends Command {
    /** The one-based position of the task to remove. */
    private int taskIndex;

    /**
     * Creates a delete-task command.
     *
     * @param taskIndex the one-based position of the task to remove
     */
    public DeleteTaskCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Deletes the selected task, displays a confirmation message, and saves the task list.
     *
     * @param tasks the current task list
     * @param ui the UI handler used to display the deleted task
     * @param storage the storage handler used to save the updated list
     * @throws ChudException if the task index is invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Task deletedTask = tasks.deleteTask(taskIndex);
        ui.showDeleteTaskMessage(deletedTask, tasks.size());
        storage.save(tasks);
    }
}
