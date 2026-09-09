package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public class HiCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHiMessage();
    }
}
