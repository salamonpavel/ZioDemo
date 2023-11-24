### Notes for the discussion

### Core ZIO Data Types

    ZIO — ZIO is a value that models an effectful program, which might fail or succeed.
        UIO — UIO[A] is a type alias for ZIO[Any, Nothing, A].
        URIO — URIO[R, A] is a type alias for ZIO[R, Nothing, A].
        Task — Task[A] is a type alias for ZIO[Any, Throwable, A].
        RIO — RIO[R, A] is a type alias for ZIO[R, Throwable, A].
        IO — IO[E, A] is a type alias for ZIO[Any, E, A].
    ZIOApp — ZIOApp and the ZIOAppDefault are entry points for ZIO applications.
    Runtime — Runtime[R] is capable of executing tasks within an environment R.
    Exit — Exit[E, A] describes the result of executing an IO value.
    Cause — Cause[E] is a description of a full story of a fiber failure.

### 5 elements of Service pattern
1. Service definition with scala trait
2. Classes for service implementation
3. Service dependencies passed via constructors (interfaces, not implementation)
4. ZLayer constuctors
5. Accessor methods to create more ergonomic API