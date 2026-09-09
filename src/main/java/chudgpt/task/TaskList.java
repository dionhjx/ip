package chudgpt.task;

import chudgpt.exception.ChudException;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list */
    public TaskList() {
        tasks = new ArrayList<Task>();
    }

    /** Creates a task list with an existing list
     *
     * @param tasks a list of tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = (ArrayList<Task>) tasks;
    }

    /**
     * Adds a task to the list
     *
     * @param task the task to be added
     * @return the task that was added
     */
    public Task addTask(Task task) {
        tasks.add(task);
        return task;
    }

    /**
     * Removes the specified task from the list
     *
     * @param index the index of the task to be removed
     * @return the task that was removed
     * @throws ChudException if the index is out of range
     */
    public Task deleteTask(int index) throws ChudException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChudException("Index out of bounds");
        }

        return tasks.remove(index);
    }

    /**
     * Returns a specified task from the list
     *
     * @param index the index of the task to be returned
     * @return the selected task
     * @throws ChudException if the index is out of range
     */
    public Task getTask(int index) throws ChudException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChudException("Index out of bounds");
        }

        return tasks.get(index);
    }

    /**
     * Updates the status of a task
     *
     * @param index the index of the task to be updated
     * @param isCompleted the status to update the task to
     * @return the updated task
     * @throws ChudException if the index is out of range
     */
    public Task updateTask(int index, boolean isCompleted) throws ChudException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChudException("Index out of bounds.");
        }

        return tasks.get(index).setCompleted(isCompleted);
    }

    /** Returns the number of tasks in the list */
    public int size() {
        return tasks.size();
    }

    /**
     * Formats all tasks in the list into a single serialized string suitable for file storage.
     *
     * @return a line-separated string representing the tasks in save format.
     */
    public String toFileFormat() {
        return tasks.stream()
                .map(Task::toSaveMessage)
                .reduce("", (a, b) -> a + System.lineSeparator() + b);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(i + 1).append(". ").append(tasks.get(i));
        }

        return sb.toString();
    }
}
