package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Changes the completion status of one task. */
public class ChangeTaskStatusCommand extends Command {
    private final int taskIndex;
    private final boolean isCompleted;

    /**
     * Creates a command that changes a task to the specified completion status.
     *
     * @param taskIndex zero-based index of the task to update.
     * @param isCompleted target completion status.
     */
    public ChangeTaskStatusCommand(int taskIndex, boolean isCompleted) {
        this.taskIndex = taskIndex;
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
