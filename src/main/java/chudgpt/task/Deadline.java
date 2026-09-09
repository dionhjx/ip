package chudgpt.task;

import java.time.LocalDate;

/** A task that must be completed by a specified time. */
public class Deadline extends Task {
    protected final LocalDate by;

    /**
     * Creates a deadline task which contains a date to submit by
     *
     * @param description the description of the task
     * @param submitBy the date to submit the task by
     */
    public Deadline(String description, LocalDate submitBy) {
        super(description);
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
