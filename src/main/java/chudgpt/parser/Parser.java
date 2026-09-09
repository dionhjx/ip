package chudgpt.parser;

import chudgpt.command.*;
import chudgpt.exception.ChudException;
import chudgpt.task.Deadline;
import chudgpt.task.Event;
import chudgpt.task.Task;
import chudgpt.task.ToDo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Parses task commands and task-number arguments entered by the user. */
public class Parser {

    /**
     * Parses a command entered by the user
     *
     * @param command the string entered by the user
     * @return the command the string corresponds to
     * @throws ChudException if an unknown command is input
     */
    public Command parse(String command) throws ChudException {
        if (command == null || command.trim().isEmpty()) {
            throw new ChudException("Please enter a command.");
        }

        String[] parts = command.toLowerCase(Locale.ROOT).trim().split("\\s+", 2);

        String commandName = parts[0];
        String params = "";
        if (parts.length > 1) {
            params = parts[1];
        }

        switch (commandName) {
            case ("hi"):
                return new HiCommand();
            case ("bye"):
                return new ExitCommand();
            case ("list"):
                return new ListCommand();
            case ("save"):
                return new SaveCommand();
            case ("todo"):
                return new AddTaskCommand(new ToDo(params));
            case ("deadline"):
                return new AddTaskCommand(buildDeadlineTask(params));
            case ("event"):
                return new AddTaskCommand(buildEventTask(params));
            case ("delete"):
                return new DeleteTaskCommand(parseIndex(params));
            case ("mark"):
                return new ChangeTaskStatusCommand(parseIndex(params), true);
            case ("unmark"):
                return new ChangeTaskStatusCommand(parseIndex(params), false);
            default:
                throw new ChudException("Invalid command.");
        }
    }

    private Task buildDeadlineTask(String params) throws ChudException {
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

    private Task buildEventTask(String params) throws ChudException {
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
     * Parses a string representing a one-indexed index
     *
     * @param params the string representing the one-indexed index
     * @return the zero-indexed index
     * @throws ChudException if the string cannot be parsed
     */
    public static int parseIndex(String params) throws ChudException {
        try {
            return Integer.parseInt(params) - 1;
        } catch (NumberFormatException e) {
            throw new ChudException("Task number must be a number.");
        }
    }

    /**
     * Parses a string representing a date
     *
     * @param value the string representing the date
     * @return the date
     * @throws ChudException if the string cannot be parsed
     */
    public static LocalDate parseDate(String value) throws ChudException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new ChudException("OOPS!!! Input a date in the format yyyy-mm-dd");
        }
    }
}