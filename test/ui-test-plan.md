# UI Test Plan

## Application

- Main class: `ChudGPT`
- Source root: `src/main/java`
- Resource root: `src/main/resources`
- Java requirement: Java 25
- Comparison: exact stdout after normalizing line endings; the final newline is significant

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

## Test Case 2: Add, list, and complete a task

Aim: Verify that a task is added, shown by `list`, and marked complete with `mark 1`.

Inputs:

```text
read book
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
added: read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [ ] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 3: Reject an invalid task number

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
