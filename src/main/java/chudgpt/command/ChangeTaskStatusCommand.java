package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public class ChangeTaskStatusCommand extends Command {
    private int taskIndex;
    private boolean isCompleted;

    public ChangeTaskStatusCommand(int taskIndex, Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Task updatedTask = tasks.updateTask(taskIndex, isCompleted);
        ui.showUpdateTaskMessage(updatedTask);
        storage.save(tasks);
    }
}
