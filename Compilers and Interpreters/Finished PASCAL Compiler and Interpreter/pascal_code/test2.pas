VAR a, b;

PROCEDURE foo(b, c);
BEGIN
    WRITELN(a);
    WRITELN(b);
    WRITELN(c);
END;

BEGIN
    a := 9;
    b := 10;
    a := foo(11, a + 3);
END;
.