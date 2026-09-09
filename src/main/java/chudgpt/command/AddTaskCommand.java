package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Adds one task to the current task list. */
public class AddTaskCommand extends Command {
    private final Task taskToAdd;

    /**
     * Creates a command that adds the specified task.
     *
     * @param taskToAdd task to add when the command executes.
     */
    public AddTaskCommand(Task taskToAdd) {
        this.taskToAdd = taskToAdd;
    }

    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.addTask(taskToAdd);
        storage.save(tasks);
        ui.showAddTaskMessage(taskToAdd, tasks.size());
    }
}
