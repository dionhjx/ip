import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * The entry point for the ChudGPT chatbot application.
 */
public class ChudGPT {
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
            String command = message.trim();
            if (command.equalsIgnoreCase("bye")) {
                break;
            } else if (command.equalsIgnoreCase("hi")) {
                message = "Hi, I'm ChudGPT. How can I help you?";
            }
            if (message.isBlank()) {
                continue;
            }

            System.out.println("____________________________________________________________");
            System.out.println(message);
            System.out.println("____________________________________________________________");
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
    }
}
