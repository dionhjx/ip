package chudgpt.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import chudgpt.command.AddTaskCommand;
import chudgpt.command.ChangeTaskStatusCommand;
import chudgpt.command.Command;
import chudgpt.command.DeleteTaskCommand;
import chudgpt.command.ExitCommand;
import chudgpt.command.HiCommand;
import chudgpt.command.ListCommand;
import chudgpt.command.SaveCommand;
import chudgpt.exception.ChudException;
import chudgpt.task.Deadline;
import chudgpt.task.Event;
import chudgpt.task.Task;

/** Tests command, task, index, and date parsing behavior. */
public class ParserTest {

    private final Parser parser = new Parser();

    @Test
    public void parse_supportedCommands_returnsExpectedCommandTypes() throws ChudException {
        assertInstanceOf(HiCommand.class, parser.parse("hi"));
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(SaveCommand.class, parser.parse("save"));
        assertInstanceOf(AddTaskCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddTaskCommand.class,
                parser.parse("deadline return book /by 2026-09-09"));
        assertInstanceOf(AddTaskCommand.class,
                parser.parse("event project meeting /from 2026-09-09 /to 2026-09-10"));
        assertInstanceOf(DeleteTaskCommand.class, parser.parse("delete 1"));
        assertInstanceOf(ChangeTaskStatusCommand.class, parser.parse("mark 1"));
        assertInstanceOf(ChangeTaskStatusCommand.class, parser.parse("unmark 1"));
    }

    @Test
    public void parse_commandNameCaseAndSpacingAreIgnored() throws ChudException {
        assertInstanceOf(HiCommand.class, parser.parse("  HI  "));
        assertInstanceOf(AddTaskCommand.class, parser.parse("  ToDo   read book  "));
    }

    @Test
    public void parse_nullOrBlankCommand_exceptionThrown() {
        assertChudException("Please enter a command.", () -> parser.parse(null));
        assertChudException("Please enter a command.", () -> parser.parse(""));
        assertChudException("Please enter a command.", () -> parser.parse("   "));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertChudException("Invalid command.", () -> parser.parse("archive task"));
    }

    @Test
    public void parse_invalidTaskNumber_exceptionThrown() {
        assertChudException("Task number must be a number.", () -> parser.parse("delete one"));
        assertChudException("Task number must be a number.", () -> parser.parse("mark 1.5"));
        assertChudException("Task number must be a number.", () -> parser.parse("unmark 2147483648"));
    }

    @Test
    public void buildDeadlineTask_validInput_deadlineReturnedWithParsedValues() throws ChudException {
        Task task = parser.parseDeadlineTask("return book /by 2026-09-09");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: 2026-09-09)", deadline.toString());
    }

    @Test
    public void buildDeadlineTask_missingByArgument_exceptionThrown() {
        assertChudException("OOPS!!! There must be a /by argument passed in.",
                () -> parser.parseDeadlineTask("return book"));
    }

    @Test
    public void buildDeadlineTask_emptyDescriptionOrDate_exceptionThrown() {
        String expectedMessage = "OOPS!!! The description of a deadline cannot be empty";

        assertChudException(expectedMessage,
                () -> parser.parseDeadlineTask(" /by 2026-09-09"));
        assertChudException(expectedMessage,
                () -> parser.parseDeadlineTask("return book /by "));
    }

    @Test
    public void buildEventTask_validInput_eventReturnedWithParsedValues() throws ChudException {
        Task task = parser.parseEventTask("project meeting /from 2026-09-09 /to 2026-09-10");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] project meeting (from: 2026-09-09 to: 2026-09-10)",
                event.toString());
    }

    @Test
    public void buildEventTask_missingOrMisorderedArguments_exceptionThrown() {
        String expectedMessage = "OOPS!!! Specify the /from argument before the /to argument";

        assertChudException(expectedMessage,
                () -> parser.parseEventTask("project meeting"));
        assertChudException(expectedMessage,
                () -> parser.parseEventTask("project meeting /from 2026-09-09"));
        assertChudException(expectedMessage,
                () -> parser.parseEventTask("project meeting /to 2026-09-10 /from 2026-09-09"));
    }

    @Test
    public void buildEventTask_emptyDescriptionOrDate_exceptionThrown() {
        String expectedMessage = """
                OOPS!!! The description, start and end date of an event cannot be empty
                """;

        assertChudException(expectedMessage,
                () -> parser.parseEventTask(" /from 2026-09-09 /to 2026-09-10"));
        assertChudException(expectedMessage,
                () -> parser.parseEventTask("project meeting /from /to 2026-09-10"));
        assertChudException(expectedMessage,
                () -> parser.parseEventTask("project meeting /from 2026-09-09 /to "));
    }

    @Test
    public void parseIndex_validNumber_zeroBasedIndexReturned() throws ChudException {
        assertEquals(0, Parser.parseIndex("1"));
        assertEquals(6, Parser.parseIndex("7"));
    }

    @Test
    public void parseIndex_nonNumber_exceptionThrown() {
        String expectedMessage = "Task number must be a number.";

        assertChudException(expectedMessage, () -> Parser.parseIndex("one"));
        assertChudException(expectedMessage, () -> Parser.parseIndex(""));
        assertChudException(expectedMessage, () -> Parser.parseIndex("2147483648"));
    }

    @Test
    public void parseDate_validLeapDay_localDateReturned() throws ChudException {
        assertEquals(LocalDate.of(2024, 2, 29), Parser.parseDate("2024-02-29"));
    }

    @Test
    public void parseDate_invalidDateOrFormat_exceptionThrown() {
        String expectedMessage = "OOPS!!! Input a date in the format yyyy-mm-dd";

        assertChudException(expectedMessage, () -> Parser.parseDate("2024-02-30"));
        assertChudException(expectedMessage, () -> Parser.parseDate("29-02-2024"));
        assertChudException(expectedMessage, () -> Parser.parseDate(""));
    }

    /**
     * Asserts that the supplied parser operation raises the expected domain exception.
     *
     * @param expectedMessage expected exception message
     * @param operation parser operation to execute
     */
    private static void assertChudException(String expectedMessage, Executable operation) {
        ChudException exception = assertThrows(ChudException.class, operation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
