# Number Annotations

Number annotations can be used with `Integer`, `int`, `Character` and `char`.
## `#!java @Null`
Indicates that the parameter must be `null`.
```java
void foo(@Null Object o) { }
```

## `#!java @NotNull`
Indicates that the parameter must be not `null`.
```java
void foo(@NotNull Object o) { }
```

## `#!java @Min(value=<int>)`
Indicates that a parameter has value >= `value`.
```java
void foo(@Min(value=10) Integer x) { }
void foo(@Min(value=42) int x) { }
```

## `#!java @Max(value=<int>)`
Indicates that a parameter has value <= `value`.
```java
void foo(@Max(value=10) Integer x) { }
void foo(@Max(value=10) int x) { }
```

## `#!java @Negative`
Indicates that a parameter has value < 0.

```java
// Can use with `Integer`
void foo(@Negative Integer a) { }
// Or with the `int` primitive.
void foo(@Negative int a) { }
```

## `#!java @Positive`
Indicates that a parameter has value > 0.

```java
// Can use with `Integer`
void foo(@Positive Integer a) { }
// Or with the `int` primitive.
void foo(@Positive int a) { }
```


## `#!java @NegativeOrZero`
Indicates that a parameter has value <= 0.

```java
// Can use with `Integer`
void foo(@NegativeOrZero Integer a) { }
// Or with the `int` primitive.
void foo(@NegativeOrZero int a) { }
```

## `#!java @PositiveOrZero`
Indicates that a parameter has value >= 0.

```java
// Can use with `Integer`
void foo(@PositiveOrZero Integer a) { }
// Or with the `int` primitive.
void foo(@PositiveOrZero int a) { }
```

# Combining Annotations

Annotations can be combined:

```java
void foo(
    // 10 <= `a` <= 20
    @Min(value=10) @Max(value=20) int a,
    // b must not be null and b must have length < 0
    @NotNull @Negative Integer b
) { }
```
