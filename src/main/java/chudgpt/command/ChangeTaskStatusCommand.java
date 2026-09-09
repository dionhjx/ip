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

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Task updatedTask = tasks.updateTask(taskIndex, isCompleted);
        ui.showUpdateTaskMessage(updatedTask);
        storage.save(tasks);
    }
}
