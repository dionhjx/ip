package chudgpt.task;

import java.time.LocalDate;

/** A task that takes place during a specified time range. */
public class Event extends Task {
    /** Start date of this event. */
    protected final LocalDate start;

    /** End date of this event. */
    protected final LocalDate end;

    /**
     * Creates an event task with the specified description and date range.
     *
     * @param task task description.
     * @param start event start date.
     * @param end event end date.
     */
    public Event(String task, LocalDate start, LocalDate end) {
        super(task);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), start, end);
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    @Override
    public String toSaveMessage() {
        return String.format("E | %d | %s | %s | %s", isCompleted ? 1 : 0, task, start, end);
    }
}
