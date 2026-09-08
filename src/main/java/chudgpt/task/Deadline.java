package chudgpt.task;

/** A task that must be completed by a specified time. */
public class Deadline extends Task {
    protected final String submitBy;

    public Deadline(String task, String submitBy) {
        super(task);
        this.submitBy = submitBy;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), submitBy);
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    @Override
    public String toSaveMessage() {
        return String.format("D | %d | %s | %s", isCompleted ? 1 : 0, task, submitBy);
    }
}
