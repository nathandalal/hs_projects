VAR ignore;

PROCEDURE foo();
BEGIN
    ignore := bar();
    WRITELN(-3);
END;

PROCEDURE bar();
WRITELN(3);

ignore := foo();
.