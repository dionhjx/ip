package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/**
 * Represents a command that changes whether a task is marked as completed.
 */
public class ChangeTaskStatusCommand extends Command {
    /** The one-based position of the task whose status will be changed. */
    private int taskIndex;
    /** The completion status to assign to the selected task. */
    private boolean isCompleted;

    /**
     * Creates a task-status command.
     *
     * @param taskIndex the one-based position of the task to update
     * @param isCompleted whether the task should be marked as completed
     */
    public ChangeTaskStatusCommand(int taskIndex, Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    /**
     * Updates the task status, displays the updated task, and saves the task list.
     *
     * @param tasks the current task list
     * @param ui the UI handler used to display the updated task
     * @param storage the storage handler used to save the updated list
     * @throws ChudException if the task index is invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Task updatedTask = tasks.updateTask(taskIndex, isCompleted);
        ui.showUpdateTaskMessage(updatedTask);
        storage.save(tasks);
    }
}
