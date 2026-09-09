package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/**
 * Represents a command that adds a task to the current task list.
 */
public class AddTaskCommand extends Command {
    /** The task that will be added when this command executes. */
    private final Task taskToAdd;

    /**
     * Creates an add-task command.
     *
     * @param taskToAdd the task to add to the task list
     */
    public AddTaskCommand(Task taskToAdd) {
        this.taskToAdd = taskToAdd;
    }

    /**
     * Adds the task, saves the updated task list, and displays a confirmation message.
     *
     * @param tasks the current task list
     * @param ui the UI handler used to display the confirmation
     * @param storage the storage handler used to save the updated list
     */
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.addTask(taskToAdd);
        storage.save(tasks);
        ui.showAddTaskMessage(taskToAdd, tasks.size());
    }
}
