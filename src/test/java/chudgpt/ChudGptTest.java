package chudgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests responses produced for GUI interactions. */
public class ChudGptTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_validCommand_returnsCommandResponse() {
        ChudGpt chudGpt = createChudGpt();

        String response = chudGpt.getResponse("hi");

        assertEquals("Hi! I'm ChudGPT. How can I help you?", response);
    }

    @Test
    public void getResponse_invalidCommand_returnsFormattedError() {
        ChudGpt chudGpt = createChudGpt();

        String response = chudGpt.getResponse("unknown");

        assertEquals("OOPS!!! I've run into an error :( I'm such a chud...\nDetails:\n  Invalid command.",
                response);
    }

    @Test
    public void getResponse_addThenList_returnsUpdatedTaskList() {
        ChudGpt chudGpt = createChudGpt();

        chudGpt.getResponse("todo read book");

        assertEquals("Here are the tasks in your list:\n1. [T][ ] read book",
                chudGpt.getResponse("list"));
    }

    @Test
    public void getWelcomeMessage_newApplication_returnsWelcomeMessage() {
        String welcomeMessage = createChudGpt().getWelcomeMessage();

        assertTrue(welcomeMessage.contains("Hello! I'm ChudGPT."));
        assertTrue(welcomeMessage.contains("What can I do for you?"));
    }

    @Test
    public void getResponse_exitCommand_applicationHasExited() {
        ChudGpt chudGpt = createChudGpt();

        chudGpt.getResponse("bye");

        assertTrue(chudGpt.hasExited());
    }

    /** Returns a ChudGPT instance backed by a temporary save file. */
    private ChudGpt createChudGpt() {
        return new ChudGpt(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
