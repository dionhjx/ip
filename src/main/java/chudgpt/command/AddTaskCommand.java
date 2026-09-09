package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public class AddTaskCommand extends Command {
    private Task taskToAdd;

    public AddTaskCommand(Task taskToAdd) {
        this.taskToAdd = taskToAdd;
    }

    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.addTask(taskToAdd);
        storage.save(tasks);
        ui.showAddTaskMessage(taskToAdd, tasks.size());
    }
}
