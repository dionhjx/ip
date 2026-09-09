package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public class ExitCommand extends Command {
    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        storage.save(tasks);
        ui.showByeMessage();
    }
}
