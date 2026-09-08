package chudgpt.parser;

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
     * Parses a command that creates a task.
     *
     * @param command the complete task command
     * @return the task represented by the command
     * @throws ChudException if the command is invalid
     */
    public Task parseTask(String command) throws ChudException {
        String taskCommand = command.trim();
        String lowerCaseCommand = taskCommand.toLowerCase(Locale.ROOT);

        if (lowerCaseCommand.equals("todo") || lowerCaseCommand.startsWith("todo ")) {
            String description = taskCommand.substring(4).trim();
            if (description.isEmpty()) {
                throw new ChudException("OOPS!!! The description of a todo cannot be empty.");
            }
            return new ToDo(description);
        }

        if (lowerCaseCommand.equals("deadline") || lowerCaseCommand.startsWith("deadline ")) {
            int byIndex = lowerCaseCommand.indexOf("/by");
            if (byIndex < 0) {
                throw new ChudException("OOPS!!! There must be a /by argument passed in.");
            }

            String description = taskCommand.substring(8, byIndex).trim();
            String submitBy = taskCommand.substring(byIndex + 3).trim();
            if (description.isEmpty() || submitBy.isEmpty()) {
                throw new ChudException("OOPS!!! The description of a deadline cannot be empty");
            }
            return new Deadline(description, parseDate(submitBy));
        }

        if (lowerCaseCommand.equals("event") || lowerCaseCommand.startsWith("event ")) {
            int fromIndex = lowerCaseCommand.indexOf("/from");
            int toIndex = lowerCaseCommand.indexOf("/to", fromIndex + 5);
            if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
                throw new ChudException("OOPS!!! Specify the /from argument before the /to argument");
            }

            String description = taskCommand.substring(5, fromIndex).trim();
            String start = taskCommand.substring(fromIndex + 5, toIndex).trim();
            String end = taskCommand.substring(toIndex + 3).trim();
            if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
                throw new ChudException("""
                        OOPS!!! The description, start and end date of an event cannot be empty
                        """);
            }
            return new Event(description, parseDate(start), parseDate(end));
        }

        throw new ChudException("OOPS!!! I'm sorry, but I don't know what that means :(. I'm such a chud...");
    }

    /**
     * Parses a command containing a one-based task number and returns its zero-based index.
     *
     * @param command the command containing the task number
     * @param commandName the command name used in usage messages
     * @return the zero-based task index
     * @throws ChudException if the command does not contain a valid number
     */
    public int parseTaskIndex(String command, String commandName) throws ChudException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new ChudException("Usage: " + commandName + " <task number>");
        }

        try {
            return Integer.parseInt(parts[1]) - 1;
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