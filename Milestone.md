# Milestone 1

### Ideas for Program Analysis

* Detecting memory leak in java
* Tracking garbage collecting in java
* Showing a call graph (or sequence diagram) for a method to visualize how the method would go through and its other calls to methods
  * Could expand on JUNIT 
    * Perhaps with some form of annotation
  * Call graph method properties displayed:
    * Runtime (how long it takes to execute)
    * Call graph
    * Parameters called with the method; maybe the object the method is called on too
* Checking possible values for variable statistically (null, int, string, etc) and any errors

### Follow up-tasks
* Decide what program analysis we will do among the ideas
* What specific features do our analysis need to have?
* Decide what responsibilities/roles/tasks we need and who will get the responsibilities
* Determine tech stack (language + libraries)

### TA Feedback
* More think about what extend it can support
* Need to have Useful visualization => more features (how many visualization we can have)
* Doing something interesting in run time like histogram

# Milestone 2

### Program Analysis

#### Description: Runtime error analysis (sequence diagram) + possible input values (static)
#### Analysis
* [Static Analysis] Given a method, what are the potential runtime errors that may occur when the method is called. This involves tracking what values the arguments/local variables may take/how they’re updated, throughout the method (what values they can take).
  * Analyze statically what possible runtime/unchecked exceptions can be thrown from a method
  * Try to do this efficiently (minimizing false positives) by keeping track of possible values for variables that may cause an exception; these variables include parameter variables and local variables declared in the method being analyzed

#### Use case: Help debugging.
If developers know the list of possible runtime errors before running a method, it could help prevent potential errors or allow them to try and catch the errors.
#### Language: 
Any prominent real-world programming language (ex. Java, Python, etc.)

### TA Feedback
* For the idea, providing tree and sequence diagram, he said about the concern for depending on syntax too much with advice "it needs to think about what happened in execution not what happen inside."
  * So, we decide not to take this idea
* For the idea of runtime error analysis, he gives a feedback to link input values with exception. It is better to track everything influenced by input and how the input cause exception.
  * So, we decide to connect/combine the idea of runtime error analysis with possible input values.

### Responsibilities

| What | Who | When |
|------|-----|------|
| Sketch/Design examples/Scenario | Katherine, Shiven | Milestone 3 before first user study |
| Research - Analysis | Shiven, Emiru, Devon | Milestone 3 |
| Research - Visualization | Chunga, Katherine | Milestone 3 |
| First User Study | Chunga, Emiru | Milestone 3 |
| Analysis Implementation - 1 | Shiven | Milestone 3, 4, 5 |
| Analysis Implementation - 2 | Emiru | Milestone 3, 4, 5 |
| Analysis Implementation - 3 | Devon | Milestone 3, 4, 5 |
| Visualization - 1 | Chunga | Milestone 4, 5 |
| Visualization - 2 | Katherine | Milestone 4, 5 |
| Final User Study | Chunga, Emiru | Milestone 4(plan), 5 |
| Video - Script | Katherine | Milestone 5/Before project deadline |
| Video - voiceover/screen-recording | Shiven | Milestone 5/Before project deadline |
| Video - editing if needed | Katherine | Milestone 5/Before project deadline |
| Final testing | Everyone | Milestone 5/Before project deadline |

# Milestone 3

## Project mockup
See the design and examples [here](https://docs.google.com/document/d/1s9kJZhugibSClv30y_vehpwmT5QMzwrcgU_T8KU31UQ/edit).

Some pseudo code is also available [here](https://docs.google.com/document/d/1Bj9LG0nBri2cVTdeBYa_x0tmj76wMndX2Voi_znQNNk/edit).

## User Study
Results of the study are available [here](https://docs.google.com/document/d/1JJHXh6V-9dyOU6LWsA6AC1xYlG6Ga5CRW9KppHqRKJw/edit#).
#### Summary
- Analysis is helpful for simple methods but less so for complex methods
- Having errors show in the call graph that can’t happen is confusing
- Utilizing annotations is easily understandable


## Original Design Changes
No major changes have been made.

## Current Progress
- [x] Research - Analysis
  - Researched frameworks for analyzing Java
  - Considered spoon and JavaParser
  - Settled on [JavaParser](https://javaparser.org/)
- [x] Research - Visualization
  - Considering using PlantUML.
  - We can generate PlantUML code and pass it to the PlanetUML generator/api to get the actual visualization.
- [x] First User Study
  - User 1: Results of the study [here](https://docs.google.com/document/d/1JJHXh6V-9dyOU6LWsA6AC1xYlG6Ga5CRW9KppHqRKJw/edit#).
  - User 2: Results of the study [here](https://docs.google.com/document/d/1C3Lpz9sdsoHcYlyXDcWB-hhsP-SZOPds2dBTn9_1hnc/edit#).

# Milestone 4
## Status of Implementation
Completed/In-Review:
- Tracking integer (range) variables
- Tracking variable declarations/assignments
- Tracking simple integer operations ('+', '-')
- Tracking variables through if statements
  - Tracking variables restricted by if conditions ('>', '<=')
- Concrete design of visualization utilizing PlantUML library

## Final User Study Plan
Plan for final user study #1 [here](https://docs.google.com/document/d/1-g3DCVzaaz2V3TAX0o6ezeWUWyOC5LHBKOOkbZBqJYs/edit)
- Same questions except more requirement for the user to write code
- Remove last question

Plan for last user study #2 [here](https://docs.google.com/document/d/1bEJiU5YBf67wCE-Pl0bYzAzn25mnn8ByNqm5zVlFz8c/edit#)

## Planned Timeline
| What | Who | When |
|------|-----|------|
| Analysis: Primitives | Devon | 11/18~11/21 (Mon) |
| Analysis: Strings | Devon | 11/18~11/21 (Mon) |
| Analysis: Objects | Shiven | 11/18~11/21 (Mon) |
| Analysis: Arithmetic operations | Shiven | 11/18~11/21 (Mon) |
| Analysis: More conditional operations | Emiru | 11/18~11/21 (Mon) |
| Analysis: Loops | Emiru | 11/18~11/21 (Mon) |
| Analysis: Arrays | Emiru | 11/18~11/21 (Mon) |
| Visualization: Basic | Chunga, Katherine | 11/18~11/21 (Mon) |
| Analysis: Primitive Wrappers | Devon | 11/18~11/25 (Fri) |
| Analysis: Method calls | Shiven | 11/18~11/25 (Fri) |
| Analysis: Lists | Emiru | 11/18~11/25 (Fri) |
| Visualization: Complex (loops, etc...) | Chunga, Katherine | 11/18~11/25 (Fri) |
| Final User Study | Chunga, Emiru | 11/21~11/26 (Sat) |
| Video - Script | Katherine | 11/21~11/26 (Sat) |
| Video - Voiceover | Shiven | 11/26~11/27 (Sun) |
| Video - Editing | Katherine | 11/27~11/28 (Mon) |
| Final testing / Bug fixing | Everyone | 11/25~11/30 (Wed) |

## Completed Timeline
| What | Who | Status |
|------|-----|------|
| Sketch/Design examples/Scenario | Katherine, Shiven | ✔️ |
| Research - Analysis | Shiven, Emiru, Devon | ✔️ |
| Research - Visualization | Chunga, Katherine | ✔️ |
| First User Study | Chunga, Emiru | ✔️ |

# Milestone 5
## Final User Study
- Planned on 11/26~11/27 (Sun)
### Changes to Language Design
- Distinguish between Errors that will potentially or definitely occur to address feedback that it is confusing to see errors that may not occur. 

## Plans for Final Video
- A rough outline for the script and its contents has been drafted up. Found [here](https://docs.google.com/document/d/1_i1WkU7xVa-NEVM6DOT7XU9KYu2pJdwbfmatTz3TAoo/edit).

## Planned Timeline
| What | Who | When |
|------|-----|------|
| Analysis | Devon, Emiru, Shiven | 11/15~11/26 (Sat) |
| Visualization | Chunga, Katherine | 11/15~11/26 (Sat) |
| Final User Study | Chunga, Emiru | 11/26~11/27 (Sun) |
| Video - Script | Katherine | 11/24~11/27 (Sun) |
| Video - Voiceover | Shiven | 11/26~11/27 (Sun) |
| Video - Editing | Katherine | 11/27~11/28 (Mon) |
| Final testing / Bug fixing | Everyone | 11/26~11/30 (Wed) |

## Progresss Timeline
| What | Who | Status |
|------|-----|------|
| Sketch/Design examples/Scenario | Katherine, Shiven | ✔️ |
| Research - Analysis | Shiven, Emiru, Devon | ✔️ |
| Research - Visualization | Chunga, Katherine | ✔️ |
| First User Study | Chunga, Emiru | ✔️ |




