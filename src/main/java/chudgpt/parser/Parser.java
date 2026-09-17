package chudgpt.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chudgpt.command.AddTaskCommand;
import chudgpt.command.ChangeTaskPriorityCommand;
import chudgpt.command.ChangeTaskStatusCommand;
import chudgpt.command.Command;
import chudgpt.command.DeleteTaskCommand;
import chudgpt.command.ExitCommand;
import chudgpt.command.FindCommand;
import chudgpt.command.HiCommand;
import chudgpt.command.ListCommand;
import chudgpt.command.SaveCommand;
import chudgpt.exception.ChudException;
import chudgpt.task.Deadline;
import chudgpt.task.Event;
import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.ToDo;

/** Parses task commands and task-number arguments entered by the user. */
public class Parser {
    private static final Pattern PRIORITY_TOKEN = Pattern.compile("(?<!\\S)/priority(?!\\S)");
    private static final String PRIORITY_SYNTAX_MESSAGE =
            "The /priority argument can only be specified once.";
    private static final String PRIORITY_MISSING_ARGUMENT_MESSAGE =
            "The /priority argument requires a priority value.";
    private static final String PRIORITY_WRONG_ORDER_MESSAGE =
            "The /priority argument must be the final argument.";

    /**
     * Parses a user command into an executable command object.
     *
     * @param command raw command entered by the user.
     * @return command represented by the input.
     * @throws ChudException if the input is blank or unsupported.
     */
    public Command parse(String command) throws ChudException {
        if (command == null || command.isBlank()) {
            throw new ChudException("Please enter a command.");
        }

        String[] parts = command.toLowerCase(Locale.ROOT).trim().split("\\s+", 2);

        String commandName = parts[0];
        String params = "";
        if (parts.length > 1) {
            params = parts[1];
        }

        return switch (commandName) {
            case "hi" -> new HiCommand();
            case "bye" -> new ExitCommand();
            case "list" -> createListCommand(params);
            case "save" -> new SaveCommand();
            case "todo", "deadline", "event" -> new AddTaskCommand(createTask(commandName, params));
            case "priority" -> createPriorityCommand(params);
            case "delete" -> new DeleteTaskCommand(parseIndex(params));
            case "mark" -> new ChangeTaskStatusCommand(parseIndex(params), true);
            case "unmark" -> new ChangeTaskStatusCommand(parseIndex(params), false);
            case "find" -> new FindCommand(params);
            default -> throw new ChudException("Invalid command.");
        };
    }

    /** Removes an optional final priority clause before using the existing task parsers. */
    private Task createTask(String commandName, String params) throws ChudException {
        Priority priority = Priority.NONE;
        Matcher matcher = PRIORITY_TOKEN.matcher(params);
        if (matcher.find()) {
            int tokenStart = matcher.start();
            int tokenEnd = matcher.end();

            if (matcher.find()) {
                throw new ChudException(PRIORITY_SYNTAX_MESSAGE);
            }

            String value = params.substring(tokenEnd).trim();
            if (value.isEmpty()) {
                throw new ChudException(PRIORITY_MISSING_ARGUMENT_MESSAGE);
            }

            if (value.matches(".*\\s+.*")) {
                throw new ChudException(PRIORITY_WRONG_ORDER_MESSAGE);
            }

            priority = Priority.parse(value);
            params = params.substring(0, tokenStart).trim();
        }

        Task task = switch (commandName) {
            case "todo" -> new ToDo(params);
            case "deadline" -> createDeadlineTask(params);
            case "event" -> createEventTask(params);
            default -> throw new ChudException("Invalid command.");
        };
        return task.setPriority(priority);
    }

    /** Parses the two supported list command forms. */
    private Command createListCommand(String params) throws ChudException {
        if (params.isEmpty()) {
            return new ListCommand();
        }
        if (params.matches("/sort\\s+priority")) {
            return new ListCommand(true);
        }
        throw new ChudException("Usage: list or list /sort priority.");
    }

    /** Parses a task number and priority without changing or saving any task. */
    private Command createPriorityCommand(String params) throws ChudException {
        String[] arguments = params.split("\\s+");
        if (arguments.length != 2) {
            throw new ChudException("Usage: priority <task number> <priority>.");
        }
        int index = parseIndex(arguments[0]);
        Priority priority = Priority.parse(arguments[1]);
        return new ChangeTaskPriorityCommand(index, priority);
    }

    /**
     * Parses a deadline task from its description and due date.
     *
     * @param params task description followed by a {@code /by} date.
     * @return parsed deadline task.
     * @throws ChudException if the deadline arguments are missing or invalid.
     */
    public Task createDeadlineTask(String params) throws ChudException {
        int byIndex = params.indexOf("/by");
        if (byIndex < 0) {
            throw new ChudException("OOPS!!! There must be a /by argument passed in.");
        }

        String description = params.substring(0, byIndex).trim();
        String submitBy = params.substring(byIndex + 3).trim();

        if (description.isEmpty() || submitBy.isEmpty()) {
            throw new ChudException("OOPS!!! The description of a deadline cannot be empty");
        }
        return new Deadline(description, parseDate(submitBy));
    }

    /**
     * Parses an event task from its description and date range.
     *
     * @param params task description followed by {@code /from} and {@code /to} dates.
     * @return parsed event task.
     * @throws ChudException if the event arguments are missing or invalid.
     */
    public Task createEventTask(String params) throws ChudException {
        int fromIndex = params.indexOf("/from");
        int toIndex = params.indexOf("/to", fromIndex + 5);
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new ChudException("OOPS!!! Specify the /from argument before the /to argument");
        }

        String description = params.substring(0, fromIndex).trim();
        String start = params.substring(fromIndex + 5, toIndex).trim();
        String end = params.substring(toIndex + 3).trim();
        if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw new ChudException("""
                        OOPS!!! The description, start and end date of an event cannot be empty
                        """);
        }
        return new Event(description, parseDate(start), parseDate(end));
    }

    /**
     * Converts a one-based task number into a zero-based list index.
     *
     * @param params task number entered by the user.
     * @return zero-based task index.
     * @throws ChudException if the value is not an integer.
     */
    public static int parseIndex(String params) throws ChudException {
        try {
            return Integer.parseInt(params) - 1;
        } catch (NumberFormatException e) {
            throw new ChudException("Task number must be a number.");
        }
    }

    /**
     * Parses an ISO-8601 calendar date.
     *
     * @param value date in {@code yyyy-mm-dd} format.
     * @return parsed calendar date.
     * @throws ChudException if the value is not a valid date.
     */
    public static LocalDate parseDate(String value) throws ChudException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new ChudException("OOPS!!! Input a date in the format yyyy-mm-dd");
        }
    }
}
