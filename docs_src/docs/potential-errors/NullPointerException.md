# `NullPointerException`

A possible `NullPointerException` could be thrown.

For example, `x` is possibly null:
```java
void test(int z) {
    Integer x = null;
    if (z < 10) {
        x = 10;
    } // (1)!
    int y = z += x;
}
```

1. `x` is potentially `null` here

Fix this error by assigning a non null value:
```java
void test(int z) {
    Integer x = 0;
    if (z < 10) {
        x = 10;
    }
    int y = z += x;
}
```