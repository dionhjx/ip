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

## Adding tasks

Enter any non-empty command. ChudGPT will add the task to your list. Note that the list only supports 100 tasks.

Example:

```text
read book
____________________________________________________________
added: read book
____________________________________________________________
```
Blank lines are ignored.

## Viewing tasks

Enter `list` to view all your tasks. The command is case-insensitive and may have surrounding spaces.
```text
list
____________________________________________________________
1. read book
2. buy drink
____________________________________________________________
```

## Exiting ChudGPT

Enter `bye` to exit. The command is case-insensitive and may have surrounding spaces.

```text
bye
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
