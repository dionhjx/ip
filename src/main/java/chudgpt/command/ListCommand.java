package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.listTasks(tasks);
    }
}
