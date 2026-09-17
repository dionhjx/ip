package chudgpt.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests assumptions required when constructing tasks. */
public class TaskTest {
    private static final LocalDate SAMPLE_DATE = LocalDate.of(2026, 9, 16);
    private static final LocalDate LATER_DATE = LocalDate.of(2026, 9, 17);

    @Test
    public void constructor_allTaskTypes_defaultToNone() {
        for (Task task : List.of(new ToDo("book"), new Deadline("book", SAMPLE_DATE),
                new Event("meeting", SAMPLE_DATE, LATER_DATE))) {
            assertEquals(Priority.NONE, task.getPriority());
        }
    }

    @Test
    public void setPriority_changeRepeatAndClear_preservesOtherDetails() {
        Task task = new Deadline("return book", SAMPLE_DATE).setCompleted(true);

        assertSame(task, task.setPriority(Priority.HIGH));
        task.setPriority(Priority.HIGH);
        assertEquals("[D][X][P:HIGH   ] return book (by: 2026-09-16)", task.toString());
        task.setPriority(Priority.NONE);

        assertTrue(task.isCompleted());
        assertEquals("[D][X][P:NONE   ] return book (by: 2026-09-16)", task.toString());
        assertThrows(NullPointerException.class, () -> task.setPriority(null));
        assertEquals(Priority.NONE, task.getPriority());
    }

    @Test
    public void toSaveMessage_allTaskTypes_includesCanonicalPriority() {
        assertEquals("T | 0 | NONE | read book", new ToDo("read book").toSaveMessage());
        assertEquals("D | 1 | EXTREME | report | 2026-09-16",
                new Deadline("report", SAMPLE_DATE).setPriority(Priority.EXTREME).setCompleted(true).toSaveMessage());
        assertEquals("E | 0 | LOW | meeting | 2026-09-16 | 2026-09-17",
                new Event("meeting", SAMPLE_DATE, LATER_DATE).setPriority(Priority.LOW).toSaveMessage());
        assertEquals("[E][ ][P:LOW    ] meeting (from: 2026-09-16 to: 2026-09-17)",
                new Event("meeting", SAMPLE_DATE, LATER_DATE).setPriority(Priority.LOW).toString());
    }

    @Test
    public void constructor_nullDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new ToDo(null));
    }

    @Test
    public void deadlineConstructor_nullDate_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    public void eventConstructor_nullStartDate_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Event("meeting", null, SAMPLE_DATE));
    }

    @Test
    public void eventConstructor_nullEndDate_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Event("meeting", SAMPLE_DATE, null));
    }

    @Test
    public void eventConstructor_nonIncreasingDates_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Event("meeting", SAMPLE_DATE, SAMPLE_DATE));
        assertThrows(AssertionError.class, () -> new Event("meeting", LATER_DATE, SAMPLE_DATE));
    }
}
