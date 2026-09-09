package chudgpt.task;

/** Common state and display behavior shared by all supported task types. */
public abstract class Task {
    /** Whether this task is complete. */
    protected boolean isCompleted;

    /** Description of this task. */
    protected final String description;

    protected Task(String description) {
        this.description = description;
        isCompleted = false;
    }

    /**
     * Changes the completion status of the task.
     *
     * @param isCompleted the target status
     */
    public Task setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
        return this;
    }

    /** Returns the completion status of a task */
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", isCompleted ? "X" : " ", description);
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    public abstract String toSaveMessage();
}