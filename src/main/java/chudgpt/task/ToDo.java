package chudgpt.task;

/** A task without a deadline or event times. */
public class ToDo extends Task {

    /**
     * Creates a To-Do task
     *
     * @param description the description of the task
     */
    public ToDo(String description) {
        super(description);
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
        return String.format("T | %d | %s", isCompleted ? 1 : 0, description);
    }
}