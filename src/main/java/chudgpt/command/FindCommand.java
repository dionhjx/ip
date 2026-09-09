package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

public class FindCommand extends Command {
    private String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        if (keyword.isBlank()) {
            throw new ChudException("Please enter a keyword to search for");
        }

        TaskList matches = tasks.findTasks(keyword);
        ui.listMatches(matches);
    }
}
