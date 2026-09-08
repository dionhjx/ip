package chudgpt.storage;

import chudgpt.task.Deadline;
import chudgpt.task.Event;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saves tasks to and loads tasks from the application's data file. */
public class Storage {
    private final Path saveFile;

    /**
     * Creates storage backed by the specified file.
     *
     * @param saveFile the file used to store tasks
     */
    public Storage(Path saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * Saves the current task list.
     *
     * @param tasks the tasks to save
     * @return whether saving succeeded
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
     * Loads saved tasks into the supplied task list.
     *
     * @param tasks the task list to populate
     */
    public void loadInto(TaskList tasks) {
        if (!Files.exists(saveFile)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(saveFile, StandardCharsets.UTF_8)) {
                Task task = parseSavedTask(line);
                if (task != null) {
                    tasks.addTask(task);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading task list from file.");
        }
    }

    /**
     * Returns the task represented by a saved task record, or {@code null} if malformed.
     *
     * @param line the serialized task record
     * @return the parsed task, or {@code null} when the record cannot be parsed
     */
    private Task parseSavedTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            return null;
        }

        boolean isCompleted;
        if (parts[1].equals("0")) {
            isCompleted = false;
        } else if (parts[1].equals("1")) {
            isCompleted = true;
        } else {
            return null;
        }

        Task task;
        if (parts[0].equals("T") && parts.length == 3 && !parts[2].isBlank()) {
            task = new ToDo(parts[2]);
        } else if (parts[0].equals("D") && parts.length == 4
                && !parts[2].isBlank() && !parts[3].isBlank()) {
            task = new Deadline(parts[2], parts[3]);
        } else if (parts[0].equals("E") && parts.length == 5
                && !parts[2].isBlank() && !parts[3].isBlank() && !parts[4].isBlank()) {
            task = new Event(parts[2], parts[3], parts[4]);
        } else {
            return null;
        }

        task.setCompleted(isCompleted);
        return task;
    }
}
