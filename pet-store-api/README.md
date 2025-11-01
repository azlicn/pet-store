# Pawfect Store - Backend Documentation

A backend documentation for Pawfect Store


## Design Pattern Used

The project uses design patters in below implementation

### 1. Order Number Generator

&nbsp;&nbsp;&nbsp;&nbsp;**Three Generator Implementations:**
<ul>
  <li>UUIDOrderNumberGenerator - Best for production (truly unique, distributed-safe)</li>
  <li>SSequentialOrderNumberGenerator - Thread-safe with atomic counter</li>
  <li>imeBasedOrderNumberGenerator** - Simple timebased generator</li>
</ul>

&nbsp;&nbsp;&nbsp;&nbsp;**Design Patterns Used:**
<ul>
  <li>Strategy Pattern - Interface allows swapping implementations</li>
  <li>Dependency Injection - All dependencies injected via constructor</li>
  <li>Configuration Pattern - Can switch generators via application.properties</li>
</ul>

&nbsp;&nbsp;&nbsp;&nbsp;**Key Features:**
<ul>
  <li>Thread-safe - All implementations handle concurrency
</li>
  <li>Testable - Clock injection allows deterministic tests</li>
  <li>Configurable - Switch generators without code changes</li>
  <li>SOLID principles - Single responsibility, open/closed, dependency inversion</li>
</ul>

&nbsp;&nbsp;&nbsp;&nbsp;**Quick Usage:**
```java
@Autowired
private OrderNumberGenerator orderNumberGenerator;

order.setOrderNUmber(orderNumberGenerator.generate());
```

&nbsp;&nbsp;&nbsp;&nbsp;**Configuration (`application.properties`):**
```properties
# Order Number Generator Type (uuid, sequential, timeBased)
app.order.generator.type=uuid
```
The UUID implementation is recommended for production as it guarantees uniqueness even across multiple servers/instances.

&nbsp;&nbsp;&nbsp;&nbsp;**Class Diagram:**

```mermaid
---
title: Order Number Generator
---
classDiagram
    %% Strategy Pattern Interface
    class OrderNumberGenerator {
        <<interface>>
        +generate() String
    }

    %% Concrete Implementations
    class UUIDOrderNumberGenerator {
        +generate() String
    }

    class SequentialOrderNumberGenerator {
        -AtomicLong counter
        +generate() String
    }

    class TimeBasedOrderNumberGenerator {
        -Clock clock
        -SecureRandom random
        +TimeBasedOrderNumberGenerator(Clock)
        +generate() String
    }

    %% Configuration Classes
    class OrderConfiguration {
        +clock() Clock
        +orderNumberGenerator(OrderNumberGenerator) OrderNumberGenerator
    }

    class OrderGeneratorConfig {
        -String generatorType
        +clock() Clock
        +configuredOrderNumberGenerator(...) OrderNumberGenerator
    }

    %% Service Layer
    class OrderService {
        -OrderNumberGenerator orderNumberGenerator
        +OrderService(OrderNumberGenerator)
        +order.setOrderNumber() void
    }

    %% Spring Components
    class Clock {
        <<Java Time API>>
        +millis() long
        +systemDefaultZone() Clock
    }

    class SecureRandom {
        <<Java Security>>
        +nextInt(int) int
    }

    class AtomicLong {
        <<Java Concurrent>>
        +incrementAndGet() long
    }

    %% Relationships - Strategy Pattern
    OrderNumberGenerator <|.. UUIDOrderNumberGenerator : implements
    OrderNumberGenerator <|.. SequentialOrderNumberGenerator : implements
    OrderNumberGenerator <|.. TimeBasedOrderNumberGenerator : implements

    %% Dependencies
    OrderService --> OrderNumberGenerator : uses
    TimeBasedOrderNumberGenerator --> Clock : depends on
    TimeBasedOrderNumberGenerator --> SecureRandom : depends on
    SequentialOrderNumberGenerator --> AtomicLong : contains

    %% Configuration Dependencies
    OrderConfiguration ..> OrderNumberGenerator : configures
    OrderConfiguration ..> Clock : creates
    OrderGeneratorConfig ..> OrderNumberGenerator : configures
    OrderGeneratorConfig ..> Clock : creates
    OrderGeneratorConfig ..> UUIDOrderNumberGenerator : selects
    OrderGeneratorConfig ..> SequentialOrderNumberGenerator : selects
    OrderGeneratorConfig ..> TimeBasedOrderNumberGenerator : selects

    %% Spring Annotations
    note for UUIDOrderNumberGenerator "@Component\n@Qualifier('uuidOrderNumberGenerator')"
    note for SequentialOrderNumberGenerator "@Component\n@Qualifier('sequentialOrderNumberGenerator')"
    note for TimeBasedOrderNumberGenerator "@Component\n@Qualifier('timeBasedOrderNumberGenerator')"
    note for OrderService "@Service\nConstructor Injection"
    note for OrderConfiguration "@Configuration\n@Primary bean definition"
    note for OrderGeneratorConfig "@Configuration\nProperty-based selection"


```





