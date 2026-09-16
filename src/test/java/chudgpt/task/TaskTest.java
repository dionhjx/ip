package chudgpt.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests assumptions required when constructing tasks. */
public class TaskTest {
    private static final LocalDate SAMPLE_DATE = LocalDate.of(2026, 9, 16);

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
}
