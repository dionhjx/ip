package chudgpt.task;

/** A task without a deadline or event times. */
public class ToDo extends Task {
    /**
     * Creates a ToDo with the specified description.
     *
     * @param task task description.
     */
    public ToDo(String task) {
        super(task);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    @Override
    public String toSaveMessage() {
        return String.format("T | %d | %s", isCompleted ? 1 : 0, task);
    }
}
