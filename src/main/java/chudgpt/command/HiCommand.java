package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Displays a greeting from ChudGPT. */
public class HiCommand extends Command {
    /**
     * Displays the greeting through the UI handler.
     *
     * @param tasks the current task list, which is not modified
     * @param ui the UI handler used to display the greeting
     * @param storage the storage handler, which is not used by this command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHiMessage();
    }
}
