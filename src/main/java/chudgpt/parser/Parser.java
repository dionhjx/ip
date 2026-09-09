package chudgpt.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

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
import chudgpt.task.ToDo;

/** Parses task commands and task-number arguments entered by the user. */
public class Parser {

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

        switch (commandName) {
            case "hi":
                return new HiCommand();
            case "bye":
                return new ExitCommand();
            case "list":
                return new ListCommand();
            case "save":
                return new SaveCommand();
            case "todo":
                return new AddTaskCommand(new ToDo(params));
            case "deadline":
                return new AddTaskCommand(createDeadlineTask(params));
            case "event":
                return new AddTaskCommand(createEventTask(params));
            case "delete":
                return new DeleteTaskCommand(parseIndex(params));
            case "mark":
                return new ChangeTaskStatusCommand(parseIndex(params), true);
            case "unmark":
                return new ChangeTaskStatusCommand(parseIndex(params), false);
            default:
                throw new ChudException("Invalid command.");
        }
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