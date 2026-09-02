# UI Test Plan

## Application

- Main class: `chudgpt.ChudGPT`
- Source root: `src/main/java`
- Resource root: `src/main/resources`
- Java requirement: Java 25
- Comparison: exact stdout after normalizing line endings; the final newline is significant
- Each test case runs in an isolated temporary working directory so saved tasks from one case do not affect another.

Each test case starts a fresh process. The `Inputs` block contains one console command per line. The `Expected output` block is the complete stdout from that session.

## Test Case 1: Greet and exit

Aim: Verify that the application responds to `hi` and exits cleanly on `bye`.

Inputs:

```text
hi
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Hi, I'm ChudGPT. How can I help you?
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 2: Add, list, and complete a ToDo

Aim: Verify that a ToDo is added, shown by `list`, and marked complete with `mark 1`.

Inputs:

```text
todo read book
list
mark 1
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 3: Add deadlines and events

Aim: Verify that deadline and event descriptions keep their date/time values as entered and display the correct task types.

Inputs:

```text
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [D][ ] return book (by: Sunday)
2. [E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 4: Reject an invalid task number

Aim: Verify that marking a task number that does not exist reports an error without changing the task list.

Inputs:

```text
mark 1
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Invalid task number.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 5: Handle empty and unsupported task commands

Aim: Verify that an empty `todo` command and an unsupported task command return validation messages without adding a task.

Inputs:

```text
todo
buy groceries
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :(. I'm such a chud...
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 6: Load saved tasks at startup

Aim: Verify that tasks are loaded from the relative save file when the chatbot starts.

Initial save file:

```text
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2-4pm | Aug 6th 3-4pm
```

Inputs:

```text
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: June 6th)
3. [E][ ] project meeting (from: Aug 6th 2-4pm to: Aug 6th 3-4pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
