package chudgpt.task;

/** Common state and display behavior shared by all supported task types. */
public abstract class Task {
    protected boolean isCompleted;
    protected final String task;

    protected Task(String task) {
        this.task = task;
        isCompleted = false;
    }

    /**
     * Changes the status of the task
     *
     * @param isCompleted the target status
     */
    public Task setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
        return this;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", isCompleted ? "X" : " ", task);
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    public abstract String toSaveMessage();
}