      PROGRAM HELLO
      CHARACTER*16 NAME
      WRITE (*,*) 'HELLO! WHAT IS YOUR NAME? ' 
      READ (*,*) NAME
      WRITE (*,*) 'WELL HI, ' , NAME, '.'
      STOP
      END