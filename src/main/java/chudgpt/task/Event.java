package chudgpt.task;

import java.time.LocalDate;

/** A task that takes place during a specified time range. */
public class Event extends Task {
    protected final LocalDate start;
    protected final LocalDate end;

    /**
     * Creates an event object, with a specified start and end date
     *
     * @param description the description of the event
     * @param start the start date of the event
     * @param end the end date of the event
     */
    public Event(String description, LocalDate start, LocalDate end) {
        super(description);
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
        return String.format("E | %d | %s | %s | %s", isCompleted ? 1 : 0, description, start, end);
    }
}