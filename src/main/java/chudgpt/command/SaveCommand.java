package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Saves the current task list. */
public class SaveCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        storage.save(tasks);
        ui.showSaveMessage();
    }
}
