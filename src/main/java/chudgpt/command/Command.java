package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Represents a command that can be executed by the ChudGPT application. */
public abstract class Command {

    /**
     * Returns whether this command should terminate the application.
     *
     * @return {@code true} if the application should exit.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Executes this command against the current application state.
     *
     * @param tasks current task list.
     * @param ui application user interface.
     * @param storage application task storage.
     * @throws ChudException if command execution fails due to invalid task data.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException;
}
