package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public class DeleteTaskCommand extends Command {
    private int taskIndex;

    public DeleteTaskCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Task deletedTask = tasks.deleteTask(taskIndex);
        ui.showDeleteTaskMessage(deletedTask, tasks.size());
        storage.save(tasks);
    }
}
