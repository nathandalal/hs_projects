Play with this bit field and the sizeof() function.
See how big things are in bytes.


struct
   {

/* field 4 bits wide */
   unsigned short field1 :4;

/*
 * unnamed 3 bit field
 * unnamed fields allow for padding
 */
   unsigned short :3;

/*
 * one-bit field
 * can only be 0 or -1 in two's complement!
 */
   short field2   :1;

/* align next field on a storage unit boundry */
   unsigned short        :0;
   unsigned short field3 :6;

   } full_of_fields;

