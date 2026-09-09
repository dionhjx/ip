package chudgpt.task;

import java.time.LocalDate;

/** A task that must be completed by a specified time. */
public class Deadline extends Task {
    protected final LocalDate by;

    public Deadline(String task, LocalDate submitBy) {
        super(task);
        this.by = submitBy;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), by);
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    @Override
    public String toSaveMessage() {
        return String.format("D | %d | %s | %s", isCompleted ? 1 : 0, description, by);
    }
}
