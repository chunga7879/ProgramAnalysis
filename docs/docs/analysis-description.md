# How It Works

Our analysis works by tracking the potential domain of each variable through the execution of a single method. The analysis notes when an error could occur in a potential domain.

### Tracked Domain
Our analysis tracks the potential domain of local variables and parameters as a set of domains.
The domain is filled with all possible values of the variable.
The analysis will over-approximate the potential domain if required.

#### Types
- Integers: range from min/max
- Characters: range from min/max
- Booleans: true, false, or either
- Objects: null, not null, or either
  - Boxed Primitives: value of the primitive
  - String: length from min/max
  - Arrays: length from min/max

#### Operations
Operations will attempt to approximate the potential domain when computes the operation. For example, adding integers with domains `[1, 10]` and `[-4, 3]` will result in an integer with domain `[-3, 13]`.

If errors occur in an operation, the domain is restricted to values that would not cause an error. For example, if object can be null after accessing a field inside the object, we will mark a NullPointerException but the domain afterwards will not have null.

For a list of operations, see [Analyzable Java Code/Operations](/valid-code).

#### Parameters
Parameters are initialized by their potential domain according to their annotations. Look under **Annotations** section for annotations that are supported.

#### Method calls/returns
Annotations are analyzed in the parameters to any called methods and errors are shown if the potential domain breaks the precondition defined by the annotation.

Returns of called methods and field access are initialized by domains according to their annotations.

### Statements
#### If-Else Statement
When our analysis encounters an If statement, it analyzes the Then block with the domains for when the condition is true, and the Else block with the domains for when the condition is false.
After the If-Else block, it will merge the potential domains of both the branches for the next statements.

#### Loops
When our analysis encounters a loop, it will repeatedly execute the body of the loop for a fixed number of runs.
If the analysis recognizes that the loop continues to execute more than the fixed number, it will begin to approximate the rest of the loop iterations.

During the approximation, the potential domains of the run is a merged domain of every loop run so far. In addition, certain operators will approximate up to the limit of the type's domain.
As such, restricting the number of runs of loops will result in a more accurate result.

After the Loop block, the analysis will continue with the merged domain of all the domains that exited the loop.

#### Unreachable Code
If our analysis recognizes that it has entered an unreachable code block, the domain will enter an "empty" state and it will stop tracking potential errors.
