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
