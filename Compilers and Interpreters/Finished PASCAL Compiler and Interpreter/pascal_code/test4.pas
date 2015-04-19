VAR ignore;

PROCEDURE foo(x);
BEGIN
    x := x + 1;
    WRITELN(x);
END;

ignore := foo(10);
.