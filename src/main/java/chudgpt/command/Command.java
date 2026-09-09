package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public abstract class Command {

    public boolean isExit() {
        return false;
    }

    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException;
}
