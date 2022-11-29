# Array Annotations

## `#!java @Size(min=<int>, max=<int>)`
Indiciates the bounds for the array size.

```java
void a(@Size(min=50) int[] fifty) { }
void b(@Size(max=100) int[] hundred) { }
void c(@Size(min=1, max=100) int[] both) { }
```


## `#!java @NotEmpty`

Indicates that the array has length > 0.

```java
void a(@NotEmpty int[] fifty) { }
```
