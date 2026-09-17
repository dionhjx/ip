package chudgpt.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import chudgpt.exception.ChudException;

/** Tests task deletion behavior and its index boundaries. */
public class TaskListTest {

    @Test
    public void addTask_newTask_taskAddedAndReturned() throws ChudException {
        TaskList taskList = new TaskList();
        ToDo task = new ToDo("new task");

        Task addedTask = taskList.addTask(task);

        assertSame(task, addedTask);
        assertEquals(1, taskList.size());
        assertSame(task, taskList.getTask(0));
    }

    @Test
    public void constructor_taskListInput_tasksCopied() {
        ToDo task = new ToDo("task");
        List<Task> sourceTasks = List.of(task);

        TaskList taskList = new TaskList(sourceTasks);
        taskList.addTask(new ToDo("another task"));

        assertEquals(1, sourceTasks.size());
        assertEquals(2, taskList.size());
    }

    @Test
    public void constructor_nullTaskList_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    public void constructor_taskListContainingNull_assertionErrorThrown() {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(sourceTasks));
    }

    @Test
    public void addTask_nullTask_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.addTask(null));
    }

    @Test
    public void getTask_validIndex_taskAtIndexReturned() throws ChudException {
        ToDo firstTask = new ToDo("first");
        ToDo secondTask = new ToDo("second");
        TaskList taskList = taskListOf(firstTask, secondTask);

        assertSame(firstTask, taskList.getTask(0));
        assertSame(secondTask, taskList.getTask(1));
    }

    @Test
    public void getTask_invalidIndex_exceptionThrown() {
        TaskList taskList = taskListOf(new ToDo("task"));

        ChudException negativeIndexException = assertThrows(ChudException.class, () ->
                taskList.getTask(-1));
        ChudException indexAfterLastException = assertThrows(ChudException.class, () ->
                taskList.getTask(taskList.size()));

        assertEquals("Index out of bounds", negativeIndexException.getMessage());
        assertEquals("Index out of bounds", indexAfterLastException.getMessage());
        assertEquals(1, taskList.size());
    }

    @Test
    public void updateTask_validIndex_completionStatusUpdatedAndTaskReturned() throws ChudException {
        ToDo task = new ToDo("task");
        TaskList taskList = taskListOf(task);

        Task updatedTask = taskList.updateTask(0, true);

        assertSame(task, updatedTask);
        assertTrue(task.isCompleted());

        taskList.updateTask(0, false);

        assertFalse(task.isCompleted());
    }

    @Test
    public void updateTask_invalidIndex_exceptionThrown() {
        ToDo task = new ToDo("task");
        TaskList taskList = taskListOf(task);

        ChudException negativeIndexException = assertThrows(ChudException.class, () ->
                taskList.updateTask(-1, true));
        ChudException indexAfterLastException = assertThrows(ChudException.class, () ->
                taskList.updateTask(taskList.size(), true));

        assertEquals("Index out of bounds.", negativeIndexException.getMessage());
        assertEquals("Index out of bounds.", indexAfterLastException.getMessage());
        assertFalse(task.isCompleted());
        assertEquals(1, taskList.size());
    }

    @Test
    public void deleteTask_firstIndex_firstTaskRemoved() throws ChudException {
        ToDo firstTask = new ToDo("first");
        ToDo secondTask = new ToDo("second");
        TaskList taskList = taskListOf(firstTask, secondTask);

        Task removedTask = taskList.deleteTask(0);

        assertSame(firstTask, removedTask);
        assertEquals(1, taskList.size());
        assertSame(secondTask, taskList.getTask(0));
    }

    @Test
    public void deleteTask_middleIndex_middleTaskRemovedAndRemainingTasksShifted() throws ChudException {
        ToDo firstTask = new ToDo("first");
        ToDo middleTask = new ToDo("middle");
        ToDo lastTask = new ToDo("last");
        TaskList taskList = taskListOf(firstTask, middleTask, lastTask);

        Task removedTask = taskList.deleteTask(1);

        assertSame(middleTask, removedTask);
        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.getTask(0));
        assertSame(lastTask, taskList.getTask(1));
    }

    @Test
    public void deleteTask_lastIndex_lastTaskRemoved() throws ChudException {
        ToDo firstTask = new ToDo("first");
        ToDo lastTask = new ToDo("last");
        TaskList taskList = taskListOf(firstTask, lastTask);

        Task removedTask = taskList.deleteTask(1);

        assertSame(lastTask, removedTask);
        assertEquals(1, taskList.size());
        assertSame(firstTask, taskList.getTask(0));
    }

    @Test
    public void deleteTask_emptyList_exceptionThrown() {
        TaskList taskList = new TaskList();

        ChudException exception = assertThrows(ChudException.class, () ->
                taskList.deleteTask(0));

        assertEquals("Index out of bounds", exception.getMessage());
        assertEquals(0, taskList.size());
    }

    @Test
    public void deleteTask_negativeIndex_exceptionThrownAndListUnchanged() throws ChudException {
        ToDo task = new ToDo("task");
        TaskList taskList = taskListOf(task);

        ChudException exception = assertThrows(ChudException.class, () ->
                taskList.deleteTask(-1));

        assertEquals("Index out of bounds", exception.getMessage());
        assertEquals(1, taskList.size());
        assertSame(task, taskList.getTask(0));
    }

    @Test
    public void deleteTask_indexEqualToSize_exceptionThrownAndListUnchanged() throws ChudException {
        ToDo task = new ToDo("task");
        TaskList taskList = taskListOf(task);

        ChudException exception = assertThrows(ChudException.class, () ->
                taskList.deleteTask(taskList.size()));

        assertEquals("Index out of bounds", exception.getMessage());
        assertEquals(1, taskList.size());
        assertSame(task, taskList.getTask(0));
    }

    @Test
    public void toFileFormat_multipleTasks_serializedInOrder() {
        TaskList taskList = taskListOf(new ToDo("unfinished"),
                new ToDo("finished").setCompleted(true));

        String expected = "T | 0 | NONE | unfinished"
                + System.lineSeparator()
                + "T | 1 | NONE | finished";

        assertEquals(expected, taskList.toFileFormat());
    }

    @Test
    public void toString_emptyList_returnsEmptyString() {
        assertEquals("", new TaskList().toString());
    }

    @Test
    public void toString_multipleTasks_numberedAndRenderedInOrder() {
        TaskList taskList = taskListOf(new ToDo("first"),
                new ToDo("second").setCompleted(true));

        String expected = "1. [T][ ][P:NONE   ] first\n2. [T][X][P:NONE   ] second";

        assertEquals(expected, taskList.toString());
    }

    @Test
    public void updatePriority_validAndInvalidIndices_updatesOnlySelectedTask() throws ChudException {
        Task first = new ToDo("first");
        Task second = new ToDo("second").setCompleted(true);
        TaskList tasks = taskListOf(first, second);

        assertSame(second, tasks.updatePriority(1, Priority.EXTREME));
        assertEquals(Priority.NONE, first.getPriority());
        assertEquals(Priority.EXTREME, second.getPriority());
        assertTrue(second.isCompleted());
        String saved = tasks.toFileFormat();
        for (int index : new int[]{-1, 2, Integer.MAX_VALUE}) {
            ChudException exception = assertThrows(ChudException.class, () ->
                    tasks.updatePriority(index, Priority.LOW));
            assertEquals("Index out of bounds.", exception.getMessage());
        }
        assertEquals(saved, tasks.toFileFormat());
        assertThrows(ChudException.class, () -> new TaskList().updatePriority(0, Priority.HIGH));
    }

    @Test
    public void toPrioritySortedString_mixedPriorities_preservesNumbersTiesAndBackingOrder() {
        TaskList tasks = taskListOf(new ToDo("medium").setPriority(Priority.MEDIUM),
                new ToDo("extreme first").setPriority(Priority.EXTREME).setCompleted(true),
                new ToDo("high").setPriority(Priority.HIGH),
                new ToDo("extreme second").setPriority(Priority.EXTREME),
                new ToDo("none"), new ToDo("low").setPriority(Priority.LOW));
        String original = tasks.toString();
        String saved = tasks.toFileFormat();

        assertEquals("2. [T][X][P:EXTREME] extreme first\n"
                + "4. [T][ ][P:EXTREME] extreme second\n"
                + "3. [T][ ][P:HIGH   ] high\n"
                + "1. [T][ ][P:MEDIUM ] medium\n"
                + "6. [T][ ][P:LOW    ] low\n"
                + "5. [T][ ][P:NONE   ] none", tasks.toPrioritySortedString());
        assertEquals(original, tasks.toString());
        assertEquals(saved, tasks.toFileFormat());
    }

    @Test
    public void toPrioritySortedString_emptySingleAndEqualPriorities_preservesNormalDisplay() {
        assertEquals("", new TaskList().toPrioritySortedString());
        TaskList single = taskListOf(new ToDo("one").setPriority(Priority.HIGH));
        assertEquals(single.toString(), single.toPrioritySortedString());
        TaskList equal = taskListOf(new ToDo("one"), new ToDo("two"));
        assertEquals(equal.toString(), equal.toPrioritySortedString());
    }

    @Test
    public void findTasks_prioritizedTasks_searchesOnlyDescriptions() {
        TaskList tasks = taskListOf(new ToDo("report").setPriority(Priority.HIGH), new ToDo("high tide"));
        assertEquals("1. [T][ ][P:NONE   ] high tide", tasks.findTasks("high").toString());
        assertEquals("1. [T][ ][P:HIGH   ] report", tasks.findTasks("REPORT").toString());
    }

    /**
     * Creates a task list containing the supplied tasks in order.
     *
     * @param tasks tasks to add to the new list
     * @return a task list containing the supplied tasks
     */
    private static TaskList taskListOf(Task... tasks) {
        TaskList taskList = new TaskList();
        for (Task task : tasks) {
            taskList.addTask(task);
        }
        return taskList;
    }
}
