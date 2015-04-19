VAR count, ignore, times;

PROCEDURE printSquares(low, high);
VAR count, square;
BEGIN
    count := low;
    WHILE count <= high DO
    BEGIN
        square := count * count;
        WRITELN(square);
        count := count + 1;
        times := times + 1;
    END;
END;

BEGIN
    count := 196;
    times := 0;
    ignore := printSquares(10, 13);
    WRITELN(count);
    WRITELN(times);
END;
.