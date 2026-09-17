package chudgpt.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import chudgpt.command.AddTaskCommand;
import chudgpt.command.ChangeTaskPriorityCommand;
import chudgpt.command.ChangeTaskStatusCommand;
import chudgpt.command.Command;
import chudgpt.command.DeleteTaskCommand;
import chudgpt.command.ExitCommand;
import chudgpt.command.HiCommand;
import chudgpt.command.ListCommand;
import chudgpt.command.SaveCommand;
import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Deadline;
import chudgpt.task.Event;
import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Tests command, task, index, and date parsing behavior. */
public class ParserTest {

    private final Parser parser = new Parser();

    @TempDir
    private Path temporaryDirectory;

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
    public void parseDeadlineCommand_validInput_deadlineReturnedWithParsedValues() throws ChudException {
        Task task = parseTaskCommand("deadline return book /by 2026-09-09");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ][P:NONE   ] return book (by: 2026-09-09)", deadline.toString());
    }

    @Test
    public void parseDeadlineCommand_missingByArgument_exceptionThrown() {
        assertChudException("OOPS!!! There must be a /by argument passed in.", () ->
                parser.parse("deadline return book"));
    }

    @Test
    public void parseDeadlineCommand_emptyDescriptionOrDate_exceptionThrown() {
        String expectedMessage = "OOPS!!! The description of a deadline cannot be empty";

        assertChudException(expectedMessage, () ->
                parser.parse("deadline  /by 2026-09-09"));
        assertChudException(expectedMessage, () ->
                parser.parse("deadline return book /by "));
    }

    @Test
    public void parseEventCommand_validInput_eventReturnedWithParsedValues() throws ChudException {
        Task task = parseTaskCommand("event project meeting /from 2026-09-09 /to 2026-09-10");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("[E][ ][P:NONE   ] project meeting (from: 2026-09-09 to: 2026-09-10)",
                event.toString());
    }

    @Test
    public void parseEventCommand_missingOrMisorderedArguments_exceptionThrown() {
        String expectedMessage = "OOPS!!! Specify the /from argument before the /to argument";

        assertChudException(expectedMessage, () ->
                parser.parse("event project meeting"));
        assertChudException(expectedMessage, () ->
                parser.parse("event project meeting /from 2026-09-09"));
        assertChudException(expectedMessage, () ->
                parser.parse("event project meeting /to 2026-09-10 /from 2026-09-09"));
    }

    @Test
    public void parseEventCommand_emptyDescriptionOrDate_exceptionThrown() {
        String expectedMessage = """
                OOPS!!! The description, start and end date of an event cannot be empty
                """;

        assertChudException(expectedMessage, () ->
                parser.parse("event  /from 2026-09-09 /to 2026-09-10"));
        assertChudException(expectedMessage, () ->
                parser.parse("event project meeting /from /to 2026-09-10"));
        assertChudException(expectedMessage, () ->
                parser.parse("event project meeting /from 2026-09-09 /to "));
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

    @Test
    public void parse_creationPriorities_supportsAllTaskTypesAndWhitespace() throws ChudException {
        assertEquals(Priority.EXTREME, parseTaskCommand("  ToDo Read Book /PRIORITY   ExTrEmE  ").getPriority());
        assertEquals("[D][ ][P:HIGH   ] return book (by: 2026-10-01)",
                parseTaskCommand("deadline return book /by 2026-10-01 /priority high").toString());
        assertEquals("[E][ ][P:LOW    ] meeting (from: 2026-10-01 to: 2026-10-02)",
                parseTaskCommand("event meeting /from 2026-10-01 /to 2026-10-02 /priority low").toString());
        assertEquals(Priority.NONE, parseTaskCommand("todo read book /priority none").getPriority());
        assertEquals(Priority.MEDIUM, parseTaskCommand("todo read book\t/priority\tmedium").getPriority());
    }

    @Test
    public void parse_priorityTokenBoundaries_preservesOrdinaryDescriptionText() throws ChudException {
        assertEquals("[T][ ][P:NONE   ] document/priority high /priority-high",
                parseTaskCommand("todo document/priority high /priority-high").toString());
        assertEquals("[T][ ][P:NONE   ] mixed case", parseTaskCommand("todo Mixed CASE").toString());
        assertEquals("[T][ ][P:NONE   ] ", parseTaskCommand("todo").toString());
    }

    @Test
    public void parse_malformedPriorityClause_reportsSpecificSyntaxError() {
        for (String command : new String[]{"todo read /priority",
            "deadline book /by 2026-10-01 /priority",
            "event meeting /from 2026-10-01 /to 2026-10-02 /priority"}) {
            assertChudException("The /priority argument requires a priority value.", () -> parser.parse(command));
        }
        for (String command : new String[]{"todo read /priority high /priority low",
            "todo read /priority high /priority"}) {
            assertChudException("The /priority argument can only be specified once.", () -> parser.parse(command));
        }
        for (String command : new String[]{"todo read /priority high extra",
            "deadline book /priority high /by 2026-10-01",
            "event meeting /priority high /from 2026-10-01 /to 2026-10-02",
            "todo discuss /priority handling in docs"}) {
            assertChudException("The /priority argument must be the final argument.", () -> parser.parse(command));
        }
    }

    @Test
    public void parse_listCommand_rejectsUnsupportedArguments() throws ChudException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(ListCommand.class, parser.parse("LIST /SORT PRIORITY"));
        for (String command : new String[]{"list priority", "list /sort", "list /sort date",
            "list /sort priority extra"}) {
            assertChudException("Usage: list or list /sort priority.", () -> parser.parse(command));
        }
    }

    @Test
    public void parse_unsupportedPriority_reportsAllowedValues() {
        String expected = "Priority must be one of: EXTREME, HIGH, MEDIUM, LOW, NONE.";
        for (String command : new String[]{"todo read /priority 1", "priority 1 urgent",
            "deadline book /by 2026-10-01 /priority h",
            "event meeting /from 2026-10-01 /to 2026-10-02 /priority med"}) {
            assertChudException(expected, () -> parser.parse(command));
        }
    }

    @Test
    public void parse_priorityCommand_validatesShapeBeforeValues() throws ChudException {
        assertInstanceOf(ChangeTaskPriorityCommand.class, parser.parse(" PRIORITY  2  HiGh "));
        for (String command : new String[]{"priority", "priority 1", "priority high", "priority 1 high extra"}) {
            assertChudException("Usage: priority <task number> <priority>.", () -> parser.parse(command));
        }
        for (String value : new String[]{"one", "1.5", "2147483648"}) {
            assertChudException("Task number must be a number.", () -> parser.parse("priority " + value + " high"));
        }
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

    /**
     * Parses a task command through the public parser API and returns the created task.
     *
     * @param command the task command to parse and execute
     * @return the task created by the command
     * @throws ChudException if the command is invalid or the task cannot be retrieved
     */
    private Task parseTaskCommand(String command) throws ChudException {
        TaskList tasks = new TaskList();
        Command parsedCommand = parser.parse(command);
        parsedCommand.execute(tasks, new Ui(),
                new Storage(temporaryDirectory.resolve("save.txt").toString()));
        return tasks.getTask(0);
    }
}
