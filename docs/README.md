# ChudGPT User Guide

ChudGPT is a command-line chatbot that stores tasks in memory, supports ToDos, deadlines, and events, lets you mark them as done, and exits when the user enters `bye`.

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

## Development workflow

After changing the application code:

1. Review and update [`test/ui-test-plan.md`](../test/ui-test-plan.md) if the intended console inputs or outputs changed.
2. Invoke the project-specific `test-ui` skill to run the UI tests. It uses Java 25, prints the console input/output transcript, and stops at the first failure.
3. Update this guide with any user-visible behavior or usage changes.

From the project root, the underlying test runner can be started with:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

## Adding tasks

Use one of the following commands. Date and time values are stored as text exactly as entered. The list supports up to 100 tasks.

### ToDos

Enter `todo <description>` for a task without a date or time:

```text
todo borrow book
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
```

For compatibility, a non-empty command without a recognized task prefix is also stored as a ToDo.

### Deadlines

Enter `deadline <description> /by <date/time>` for a task that must be completed by a specific date or time:

```text
deadline return book /by Sunday
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
____________________________________________________________
```

### Events

Enter `event <description> /from <start> /to <end>` for a task with a start and end date/time:

```text
event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
____________________________________________________________
```

The `deadline` and `event` commands require non-empty descriptions and date/time values. Blank lines are ignored.

## Viewing tasks

Enter `list` to view all your tasks. The command is case-insensitive and may have surrounding spaces.
```text
list
____________________________________________________________
1. [T][ ] read book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
```

## Marking tasks as done

Enter `mark <task number>` to mark a task as done. Task numbers start at 1.

```text
mark 2
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
____________________________________________________________
```

Enter `unmark <task number>` to mark a completed task as not done.

```text
unmark 2
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Sunday)
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
