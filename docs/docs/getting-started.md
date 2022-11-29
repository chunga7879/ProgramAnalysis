# Getting Started

### Introduction
Our analysis is a Static Program Analysis that tracks the potential domain of local variables and parameters to check whether any runtime errors can occur.

### Running the Analysis
To run the code, run `src/main/java/ui/Main.java` with the following arguments:

`[Java file path] [Method name] [Output file path (optional)] -d`

- `Java file path`: Path to the Java file you are trying to analyze
- `Method name`: Name of the method in the Java file
- `Output file path` (optional): Path to output the analysis diagram
- `-d` (optional): Enables **Debug Mode** which will output the tracked domain for each line of execution to the command line