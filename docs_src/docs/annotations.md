# Annotations
## `@Null`
- Indicates that the parameter must be `null`.
```java
void foo(@Null Object o) { }
```

## `@NotNull`
- Indicates that the parameter must be not `null`.
```java
void foo(@NotNull Object o) { }
```

## `@Min(value=<int>)`
- Indicates that an `Integer` or `int` has value >= `value`.
```java
void foo(@Min(value=10) Integer x) { }
void foo(@Min(value=42) int x) { }
```

## `@Max(value=<int>)`
- Indicates that an `Integer` or `int` has value <= `value`.
```java
void foo(@Max(value=10) Integer x) { }
void foo(@Max(value=10) int x) { }
```

## `@Negative`
- Indicates that an `Integer` or `int` has value < 0.

```java
// Can use with `Integer`
void foo(@Negative Integer a) { }
// Or with the `int` primitive.
void foo(@Negative int a) { }
```

## `@Positive`
- Indicates that an `Integer` or `int` has value > 0.

```java
// Can use with `Integer`
void foo(@Positive Integer a) { }
// Or with the `int` primitive.
void foo(@Positive int a) { }
```


## `@NegativeOrZero`
- Indicates that an `Integer` or `int` has value <= 0.

```java
// Can use with `Integer`
void foo(@NegativeOrZero Integer a) { }
// Or with the `int` primitive.
void foo(@NegativeOrZero int a) { }
```

## `@PositiveOrZero`
- Indicates that an `Integer` or `int` has value >= 0.

```java
// Can use with `Integer`
void foo(@PositiveOrZero Integer a) { }
// Or with the `int` primitive.
void foo(@PositiveOrZero int a) { }
```

## `@Size(min=<int>, max=<int>)`
- Indiciates the bounds for the array size.

```java
void a(@Size(min=50) int[] fifty) { }
void b(@Size(max=100) int[] hundred) { }
void c(@Size(min=1, max=100) int[] both) { }
```


## `@NotEmpty`

- Indicates that the array has length > 0.

```java
void a(@NotEmpty int[] fifty) { }
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