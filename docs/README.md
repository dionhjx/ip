# ChudGPT User Guide

ChudGPT is a command-line chatbot that echoes non-empty commands and exits when the user enters `bye`.

## Starting ChudGPT

Run `ChudGPT.main()` from `src/main/java/ChudGPT.java`. ChudGPT displays its banner and asks what it can do for the user.

## Saying Hi

Enter `hi` to say hi to ChudGPT!

```text
hi
____________________________________________________________
Hi, I'm ChudGPT. How can I help you?
____________________________________________________________
```

## Echoing commands

Enter any non-empty command. ChudGPT prints the command back to the user.

Example:

```text
list
____________________________________________________________
list
____________________________________________________________
```

Blank lines are ignored.

## Exiting ChudGPT

Enter `bye` to exit. The command is case-insensitive and may have surrounding spaces.

```text
bye
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
