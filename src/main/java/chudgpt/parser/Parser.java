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
                return new AddTaskCommand(parseDeadlineTask(params));
            case ("event"):
                return new AddTaskCommand(parseEventTask(params));
            case ("delete"):
                return new DeleteTaskCommand(parseIndex(params));
            default:
                throw new ChudException("Invalid command.");
        }
    }

    public Task parseDeadlineTask(String params) throws ChudException {
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

    public Task parseEventTask(String params) throws ChudException {
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

    public static int parseIndex(String params) throws ChudException {
        try {
            return Integer.parseInt(params) - 1;
        } catch (NumberFormatException e) {
            throw new ChudException("Task number must be a number.");
        }
    }

    public static LocalDate parseDate(String value) throws ChudException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new ChudException("OOPS!!! Input a date in the format yyyy-mm-dd");
        }
    }
}