# ChudGPT User Guide

ChudGPT is a command-line chatbot that stores tasks in memory, lets you mark them as done, and exits when the user enters `bye`.

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

## Marking tasks as done

Enter `mark <task number>` to mark a task as done. Task numbers start at 1.

```text
mark 2
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
```

Enter `unmark <task number>` to mark a completed task as not done.

```text
unmark 2
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] return book
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
