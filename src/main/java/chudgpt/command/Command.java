package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public abstract class Command {

    /** Returns whether the command is an exit command */
    public boolean isExit() {
        return false;
    }

    /**
     * Executes the command
     *
     * @param tasks the current task list
     * @param ui the UI handler
     * @param storage the storage handler
     * @throws ChudException if an error occurs
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException;
}
