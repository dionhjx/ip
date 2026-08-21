import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * The entry point for the ChudGPT chatbot application.
 */
public class ChudGPT {
    private static int taskCount     = 0;
    private static final String[] tasks = new String[100];

    private static String listOut() {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < taskCount; i++) {
            if (i > 0) {
                message.append("\n");
            }
            message.append(i + 1).append(". ").append(tasks[i]);
        }

        return message.toString();
    }

    private static void addTask(String task) {
        if (task.isBlank()) return;

        if (taskCount == tasks.length) {
            System.out.println("Too much tasks to store.");
            return;
        }
        tasks[taskCount] = task;
        ++taskCount;
    }

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        String logo;
        try (InputStream logoStream = ChudGPT.class.getResourceAsStream("/logo.txt")) {
            if (logoStream == null) {
                throw new IOException("Could not find logo.txt on the classpath.");
            }
            logo = new String(logoStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        System.out.println("____________________________________________________________");
        System.out.println(logo);
        System.out.println("Hello! I'm ChudGPT.\nWhat can I do for you?");
        System.out.println("____________________________________________________________");


        while (input.hasNextLine()) {
            String message = input.nextLine();

            if (message.isBlank()) {
                continue;
            }

            String command = message.trim();
            if (command.equalsIgnoreCase("bye")) {
                break;
            } else if (command.equalsIgnoreCase("hi")) {
                message = "Hi, I'm ChudGPT. How can I help you?";
            } else if (command.equalsIgnoreCase("list")) {
                message = listOut();
            } else {
                addTask(message);
                message = "added: " + command;
            }


            System.out.println("____________________________________________________________");
            System.out.println(message);
            System.out.println("____________________________________________________________");
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }
}
