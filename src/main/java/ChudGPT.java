import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The entry point for the Chud-GPT chatbot application.
 */
public class ChudGPT {
    public static void main(String[] args) throws IOException {
        String logo = Files.readString(Path.of("logo.txt"));
        System.out.println("____________________________________________________________");
        System.out.println(logo);
        System.out.println("Hello! I'm ChudGPT.\nWhat can I do for you?");
        System.out.println("____________________________________________________________");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");


    }
}
