package chudgpt.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chudgpt.exception.ChudException;
import chudgpt.task.Task;

/** Tests loading valid and malformed saved task records. */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_validTaskTypes_tasksLoadedWithCompletionStatus() throws IOException, ChudException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(saveFile, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-09-20",
                "E | 1 | project meeting | 2026-09-21 | 2026-09-22"));

        List<Task> tasks = new Storage(saveFile.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] return book (by: 2026-09-20)", tasks.get(1).toString());
        assertEquals("[E][X] project meeting (from: 2026-09-21 to: 2026-09-22)", tasks.get(2).toString());
    }

    @Test
    public void load_malformedRecords_onlyValidTasksLoaded() throws IOException, ChudException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(saveFile, List.of(
                "missing fields",
                "T | 2 | invalid status",
                "T | 0 | ",
                "D | 0 | missing date",
                "E | 0 | missing end | 2026-09-21",
                "X | 0 | unknown type",
                "T | 0 | valid task"));

        List<Task> tasks = new Storage(saveFile.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] valid task", tasks.get(0).toString());
    }
}
