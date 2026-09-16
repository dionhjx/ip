package chudgpt.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import chudgpt.exception.ChudException;
import chudgpt.parser.Parser;
import chudgpt.task.Deadline;
import chudgpt.task.Event;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;

/** Saves tasks to and loads tasks from the application's data file. */
public class Storage {
    private final Path saveFile;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath the file path used to store tasks.
     */
    public Storage(String filePath) {
        this.saveFile = Path.of(filePath);
    }

    /**
     * Saves the current task list.
     *
     * @param tasks the tasks to save.
     * @return whether saving succeeded.
     */
    public boolean save(TaskList tasks) {
        try {
            Files.createDirectories(saveFile.getParent());
            Files.writeString(saveFile, tasks.toFileFormat(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving task list to file.");
            return false;
        }
    }

    /**
     * Reads the save file and creates a task list from the saved tasks.
     *
     * @return the task list.
     */
    public List<Task> load() throws ChudException {
        if (!Files.exists(saveFile)) {
            return new ArrayList<>();
        }
        List<Task> tasks = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(saveFile, StandardCharsets.UTF_8)) {
                Task task = parseSavedTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new ChudException("Error loading task list from file.");
        }

        return tasks;
    }

    /**
     * Returns the task represented by a saved task record, or {@code null} if malformed.
     *
     * @param line the serialized task record.
     * @return the parsed task, or {@code null} when the record cannot be parsed.
     */
    private Task parseSavedTask(String line) throws ChudException {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            return null;
        }

        Boolean isCompleted = parseCompletionStatus(parts[1]);
        if (isCompleted == null) {
            return null;
        }

        Task task = createTask(parts);
        if (task == null) {
            return null;
        }

        task.setCompleted(isCompleted);
        return task;
    }

    /** Returns the completion status represented by a saved value, or {@code null} if invalid. */
    private Boolean parseCompletionStatus(String value) {
        return switch (value) {
            case "0" -> false;
            case "1" -> true;
            default -> null;
        };
    }

    /** Returns the task represented by the saved fields, or {@code null} if malformed. */
    private Task createTask(String[] parts) throws ChudException {
        return switch (parts[0]) {
            case "T" -> createToDo(parts);
            case "D" -> createDeadline(parts);
            case "E" -> createEvent(parts);
            default -> null;
        };
    }

    /** Returns a to-do task when its saved fields are valid. */
    private Task createToDo(String[] parts) {
        return parts.length == 3 && !parts[2].isBlank()
                ? new ToDo(parts[2])
                : null;
    }

    /** Returns a deadline task when its saved fields are valid. */
    private Task createDeadline(String[] parts) throws ChudException {
        return parts.length == 4 && !parts[2].isBlank() && !parts[3].isBlank()
                ? new Deadline(parts[2], Parser.parseDate(parts[3]))
                : null;
    }

    /** Returns an event task when its saved fields are valid. */
    private Task createEvent(String[] parts) throws ChudException {
        return parts.length == 5 && !parts[2].isBlank() && !parts[3].isBlank() && !parts[4].isBlank()
                ? new Event(parts[2], Parser.parseDate(parts[3]), Parser.parseDate(parts[4]))
                : null;
    }
}
