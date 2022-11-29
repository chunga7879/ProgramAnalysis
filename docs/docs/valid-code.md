# Analyzable Java Code

Listed below are Java structures that are analyzable by our analysis tool.

#### Values
- Integers
- Characters
- Booleans
- Strings
- Arrays
- Boxed Primitives (Booleans, Characters, and Integers)
- Any other objects
- No byte, long, double, etc...

#### Operations
- Strings
    - `+`
- Integers
    - `+`, `-`, `*`, `/`
        - `+=`, `-=`, `*=`, `/=`
    - `i++`, `i--`, `++i`, `--i`
    - `>`, `>=`, `<`, `<=`
    - No bitwise operators
- Any supported type
    - `==`, `!=`
- Booleans
    - `&&`, `||`, `()`, `!`
    - `condition ? a : b`
- Arrays
    - `new A[]`
    - `a[index]`
- Objects 
    - `new A()` 
    - `a.method()`
    - `a.field`
    - `a instanceOf b`
    - `(type) a`

#### Statements
- `if`/`else`/`else if`
- `for` and `while` loop
- `continue` and `break`
- `return`
- `throw`
- No switch, for-each, do-while, try/catch, assert, etc...

#### Annotations
Our analysis uses [javax.validation.constraints](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html) annotations.
- `@Negative`, `@Positive`, `@NegativeOrZero`, `@PositiveOrZero`
- `@Min`, `@Max`
- `@NotNull`, `@Null`
- `@NotEmpty`
- `@Size`

#### Invalid code
- Code must be compilable Java code
- Analysis does not handle Integer overflow (i.e. integer going high enough to wrap around to negative, going low enough to wrap around to positive)
    - Analysis will treat Integer.MAX_VALUE as a strict maximum and Integer.MIN_VALUE as a strict minimum