# Annotations

Select the type of annotation on the right hand side to get started.


## Combining Annotations

Annotations can be combined:

```java
void foo(
    // 10 <= `a` <= 20
    @Min(value=10) @Max(value=20) int a,
    // b must not be null and b must have length < 0
    @NotNull @Negative Integer b
) { }
```