PROCEDURE max(x, y);
BEGIN
    max := x;
    IF y > x THEN max := y;
END;

BEGIN
WRITELN(max(5, 12));
WRITELN(max(13, 7));
END;
.