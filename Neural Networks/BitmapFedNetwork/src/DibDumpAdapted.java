/**
 * 
 * @author Nathan Dalal
 * @author EricN
 * February 2, 2009
 *
 * An apadption of Dr. Nelson's original DibDump.java code that modularizes the code and allows for use of the following functionality:
 *    DibDumpAdapted dibdumper = new DibDumpAdapted(String filename); - reads in header, color table, and image array of passed .bmp file
 *                   dibdumper.getImageArray() - gets picture elements of bitmap in original color format
 *                   dibdumper.getGrayscaleImageArray() - gets grayscale picture elements of bitmap
 *                   dibdumper.getMonochromeImageArray(int[][] arr) - converts all valued bits to 1 and leaves 0 at 0 for monochrome effect
 *                   dibdumper.flipArrayBits(int[][] arr) - flips all pels in bitmap image array
 *                   dibdumper.writeBitmapToDisk(int[][] newimageArray, String filename) - given a new image array of height and width,
 *                                                                                         this method will copy this array to a .bmp
 *                                                                                         file with the specified filename
 *
 * Original Introductory Comments:
 *
 * A "short" code segment to open bitmaps and
 * extract the bits as an array of integers. If the array is small (less than 30 x 30)
 * it will print the hex values to the console.
 * The code subsequently saves the array as a 24-bit true color bitmap. The default input file name is test1.bmp
 * the default output name is test2.bmp. You can override these defaults by passing
 * different names as arguments. This file is not meant to be used "as is". You should create your own class
 * and extract what you need from here to populate it.
 *
 * Rev: 2/18/09 - case 1: for 2 colors was missing
 *                case 2: had 2 not 4 colors.
 *                The mask for 16 colors was 1 and should have been 0x0F.
 *                case 16: for 2^16 colors was not decoding the 5 bit colors properly and did not read the padded bytes. It should work properly now. Not tested.
 *                Updated the comment on biSizeImage and all the image color depths
 *                Decoding for color table images was incorrect. All image types are padded so that the number of bytes read per
 *                   scan line is a multiple of 4. Added the code to read in the "dead bytes" along with updating the comments. Additionally
 *                   the most significant bit, half-nibble or nibble is on the left side of the least significant parts. The ordering was
 *                   reversed which scrambled the images.
 *                256 Color images now works correctly.
 *                16 Color images now works correctly.
 *                4 Color images should work, but is not tested.
 *                2 Color images now works correctly.
 * 
 * Rev: 2/19/09 - The color table was not correctly read when biClrUsed was non-zero. Added one line (and comments) just prior to reading the color table
 *                   to account for this field being non-zero.
 * Rev: 2/20/09 - Added RgbQuad class
 *                Added pelToRGB(), rgbToPel() and colorToGrayscale() to DibDump class. These use the new RgbQuad class.
 *                Added peltoRGBQ(), rgbqToPel() (these handle the reserved byte in 32-bit images)
 *                Did NOT implement pelToRGB and rgbToPel in DibDump overall.
 * Rev: 2/21/09   The array index values for passing arguments in main() were 1 and 2, should have been 0 and 1 (at least according to Conrad). Not tested.
 *
 * Classes in the file:
 *  RgbQuad
 *  DibDump
 *  
 * Methods in this file:
 *  int     swapInt(int v)
 *  int     swapShort(int v)
 *  RgbQuad pelToRGBQ(int pel)
 *  int     rgbqToPel(int red, int green, int blue, int reserved)
 *  RgbQuad pelToRGB(int pel)
 *  int     rgbToPel(int red, int green, int blue)
 *  int     colorToGrayscale(int pel)
 *  void    main(String[] args)
 *  
 * There is a lot of cutting and pasting from various
 * documents dealing with bitmaps and I have not taken the
 * time to clean up the formatting in the comments. The C syntax is
 * included for reference. The types are declared in windows.h. The C
 * structures and data arrays are predefined static so that they don't
 * ever fall out of scope.
 *
 * I have not "javafied" this file. Much of it needs to be broken out into
 * various specialty methods. These modifications are left as an exercise
 * for the reader.
 *
 * Notes on reading bitmaps:
 *
 * The BMP format assumes an Intel integer type (little endian), however, the Java virtual machine
 * uses the Motorola integer type (big endian), so we have to do a bunch of byte swaps to get things
 * to read and write correctly. Also note that many of the values in a bitmap header are unsigned
 * integers of some kind and Java does not know about unsigned values, except for reading in
 * unsigned byte and unsigned short, but the unsigned int still poses a problem.
 * We don't do any math with the unsigned int values, so we won't see a problem.
 *
 * Bitmaps on disk have the following basic structure
 *  BITMAPFILEHEADER (may be missing if file is not saved properly by the creating application)
 *  BITMAPINFO -
 *        BITMAPINFOHEADER
 *        RGBQUAD - Color Table Array (not present for true color images)
 *  Bitmap Bits in one of many coded formats
 *
 *  The BMP image is stored from bottom to top, meaning that the first scan line in the file is the last scan line in the image.
 *
 *  For ALL images types, each scan line is padded to an even 4-byte boundary.
 *  
 *  For images where there are multiple pels per byte, the left side is the high order element and the right is the
 *  low order element.
 *
 *  in Windows...
 *  DWORD is an unsigned 4 byte integer
 *  WORD is an unsigned 2 byte integer
 *  LONG is a 4 byte signed integer
 *
 *  in Java we have the following sizes:
 *
 * byte
 *   1 signed byte (two's complement). Covers values from -128 to 127.
 *
 * short
 *   2 bytes, signed (two's complement), -32,768 to 32,767
 *
 * int
 *   4 bytes, signed (two's complement). -2,147,483,648 to 2,147,483,647.
 *   Like all numeric types ints may be cast into other numeric types (byte, short, long, float, double).
 *   When lossy casts are done (e.g. int to byte) the conversion is done modulo the length of the smaller type.
 */
import java.io.*;

/*
 * A member-variable-only class for holding the RGBQUAD structure elements.
 */
final class RgbQuad
   {
   int red;
   int green;
   int blue;
   int reserved;
   }

public class DibDumpAdapted
{
   String filename;

   // The color table
   int[] colorPallet = new int[256];  // reserve space for the largest possible color table

   // BITMAPFILEHEADER
   int bmpFileHeader_bfType;          // WORD
   int bmpFileHeader_bfSize;          // DWORD
   int bmpFileHeader_bfReserved1;     // WORD
   int bmpFileHeader_bfReserved2;     // WORD
   int bmpFileHeader_bfOffBits;       // DWORD
   // BITMAPINFOHEADER
   int bmpInfoHeader_biSize;          // DWORD
   int bmpInfoHeader_biWidth;         // LONG
   int bmpInfoHeader_biHeight;        // LONG
   int bmpInfoHeader_biPlanes;        // WORD
   int bmpInfoHeader_biBitCount;      // WORD
   int bmpInfoHeader_biCompression;   // DWORD
   int bmpInfoHeader_biSizeImage;     // DWORD
   int bmpInfoHeader_biXPelsPerMeter; // LONG
   int bmpInfoHeader_biYPelsPerMeter; // LONG
   int bmpInfoHeader_biClrUsed;       // DWORD
   int bmpInfoHeader_biClrImportant;  // DWORD
   // The true color pels
   int[][] imageArray;

   // if bmpInfoHeader_biHeight is negative then the image is a top down DIB. This flag is used to
   // identify it as such. Note that when the image is saved, it will be written out in the usual
   // inverted format with a positive bmpInfoHeader_biHeight value.
   boolean topDownDIB = false;

   /**
    * Initializes this instance of DibDumpAdapted and intializes the filename.
    *
    * @param filename the directory of the bmp image to read
    */
   public DibDumpAdapted(String filename)
   {
      this.filename = filename;
      initializeImage();
   }

   /**
    * Accessor for filename of this instance of DibDumpAdapted
    *
    * @return filename
    */
   public String getFilename()
   {
      return filename;
   }

   /**
    * Methods to go between little and big endian integer formats.
    *
    * @param integer to convert
    * @return converted integer
    */
   private int swapInt(int v)
   {
      return  (v >>> 24) | (v << 24) | ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
   }

   private int swapShort(int v)
   {
      return  ((v << 8) & 0xFF00) | ((v >> 8) & 0x00FF);
   }

   /**
    * Method pelToRGBQ accepts an integer (32 bit) picture element and returns the red, green and blue colors.
    * Unlike pelToRGB, this method also extracts the most significant byte and populates the reserved element of RgbQuad.
    *
    * @param picture element pel
    * @return an RgbQuad object. See rgbqToPel(int red, int green, int blue, int reserved) to go the the other way. 
    */
   private RgbQuad pelToRGBQ(int pel)
   {
      RgbQuad rgbq = new RgbQuad();

      rgbq.blue     =  pel        & 0x00FF;
      rgbq.green    = (pel >> 8)  & 0x00FF;
      rgbq.red      = (pel >> 16) & 0x00FF;
      rgbq.reserved = (pel >> 24) & 0x00FF;
            
      return rgbq;
   }

   /**
    * The rgbqToPel method takes red, green and blue color values plus an additional byte and returns a single 32-bit integer color.
    * See pelToRGBQ(int pel) to go the other way.
    *
    * @param red part of RGB element
    * @param green part of RGB element
    * @param blue part of RGB element
    * @param reserved additional byte
    *
    * @return pel version of RGB - 32 bit integer
    */
   private int rgbqToPel(int red, int green, int blue, int reserved)
   {
      return (reserved << 24) | (red << 16) | (green << 8) | blue;
   }

   /**
    * Method pelToRGB accepts an integer (32 bit) picture element and returns the red, green and blue colors
    * as an RgbQuad object. See rgbToPel(int red, int green, int blue) to go the the other way. 
    * 
    * @param picture element to convert to RGB
    * @return RgbQuad element converted from passed pel
    */
   private RgbQuad pelToRGB(int pel)
   {
      RgbQuad rgb = new RgbQuad();

      rgb.reserved = 0;

      rgb.blue  =  pel        & 0x00FF;
      rgb.green = (pel >> 8)  & 0x00FF;
      rgb.red   = (pel >> 16) & 0x00FF;
        
      return rgb;
   }

   /**
    * The rgbToPel method takes red, green and blue color values and returns a single 32-bit integer color.
    * See pelToRGB(int pel) to go the other way.
    *
    * Does not take an additional reserved byte!
    * Calls rgbqToPel method
    *
    * @param red part of RGB element
    * @param green part of RGB element
    * @param blue part of RGB element
    *
    * @return picture element of 3 rgb
    */
   private int rgbToPel(int red, int green, int blue)
   {
      return rgbqToPel(red, green, blue, 0);
   }

   /**
    * Y = 0.3RED+0.59GREEN+0.11Blue
    * The colorToGrayscale method takes a color picture element (pel) and returns the gray scale pel.
    * 
    * @param pel a picture element to convert to grayscale
    *
    * @return a grayscale version of the pel
    */
   private int colorToGrayscale(int pel)
   {
      RgbQuad rgb = pelToRGB(pel);
    
      int lum = (int)Math.round(0.3 * (double)rgb.red + 0.589 * (double)rgb.green + 0.11 * (double)rgb.blue);

      return rgbToPel(lum, lum, lum);
   }

   /**
    * Inverts the pel and masks the top 6 bits to 0.
    * Assumes a 24 bit true color image for valid use cases.
    *
    * @precondition pel should have been grayscaled to make RG of RGB irrelevant
    *
    * @param pel the pel to invert
    * @return the inverted pel
    */
   private int invertPel(int pel)
   {
      return (~pel) & 0x000000FF;
   }


   /**
    * Initializes the header values, color table, and the imageArray.
    */
   public void initializeImage()
   {
      try // lots of things can go wrong when doing file i/o
      {
         // Open the file that is the first command line parameter
         FileInputStream fstream = new FileInputStream(filename);

         // Convert our input stream to a DataInputStream
         DataInputStream in = new DataInputStream(fstream);

         /*
          * Pipeline:
          *    Initializes headers
          *    Initializes color table
          *    Reads in the bitmap image array.
          */
         initializeHeaderValues(in);
         initializeColorTable(in);
         initializeImageArray(in);

         in.close();
         fstream.close();
      } // try
      catch (Exception e)
      {
         System.err.println("File input error" + e);
      }

   }

   /**
    * Initializes the header values of the bitmap into the instance variables of
    * this instance of DibDumpAdapted.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeHeaderValues(DataInputStream in) throws Exception
   {
      /*
       *  Read in BITMAPFILEHEADER
       *
       *              typedef struct tagBITMAPFILEHEADER {
                          WORD    bfType;
                          DWORD   bfSize;
                          WORD    bfReserved1;
                          WORD    bfReserved2;
                          DWORD   bfOffBits;
                  } BITMAPFILEHEADER, FAR *LPBITMAPFILEHEADER, *PBITMAPFILEHEADER;

      bfType
          Specifies the file type. It must be set to the signature word BM (0x4D42) to indicate bitmap.
      bfSize
          Specifies the size, in bytes, of the bitmap file.
      bfReserved1
          Reserved; set to zero
      bfReserved2
          Reserved; set to zero
      bfOffBits
          Specifies the offset, in bytes, from the BITMAPFILEHEADER structure to the bitmap bits
      */

      // Read and Convert to big endian
      bmpFileHeader_bfType      = swapShort(in.readUnsignedShort());    // WORD
      bmpFileHeader_bfSize      = swapInt(in.readInt());                // DWORD
      bmpFileHeader_bfReserved1 = swapShort(in.readUnsignedShort());    // WORD
      bmpFileHeader_bfReserved2 = swapShort(in.readUnsignedShort());    // WORD
      bmpFileHeader_bfOffBits   = swapInt(in.readInt());                // DWORD

      /*
      Read in BITMAPINFOHEADER

                     typedef struct tagBITMAPINFOHEADER{
                             DWORD      biSize;
                             LONG       biWidth;
                             LONG       biHeight;
                             WORD       biPlanes;
                             WORD       biBitCount;
                             DWORD      biCompression;
                             DWORD      biSizeImage;
                             LONG       biXPelsPerMeter;
                             LONG       biYPelsPerMeter;
                             DWORD      biClrUsed;
                             DWORD      biClrImportant;
                     } BITMAPINFOHEADER, FAR *LPBITMAPINFOHEADER, *PBITMAPINFOHEADER;


      biSize
          Specifies the size of the structure, in bytes.
          This size does not include the color table or the masks mentioned in the biClrUsed member.
          See the Remarks section for more information.
      biWidth
          Specifies the width of the bitmap, in pixels.
      biHeight
          Specifies the height of the bitmap, in pixels.
          If biHeight is positive, the bitmap is a bottom-up DIB and its origin is the lower left corner.
          If biHeight is negative, the bitmap is a top-down DIB and its origin is the upper left corner.
          If biHeight is negative, indicating a top-down DIB, biCompression must be either BI_RGB or BI_BITFIELDS. Top-down DIBs cannot be compressed.
      biPlanes
          Specifies the number of planes for the target device.
          This value must be set to 1.
      biBitCount
          Specifies the number of bits per pixel.
          The biBitCount member of the BITMAPINFOHEADER structure determines the number of bits that define each pixel and the maximum number of colors in the bitmap.
          This member must be one of the following values.
          Value     Description
          1       The bitmap is monochrome, and the bmiColors member contains two entries.
                  Each bit in the bitmap array represents a pixel. The most significant bit is to the left in the image. 
                  If the bit is clear, the pixel is displayed with the color of the first entry in the bmiColors table.
                  If the bit is set, the pixel has the color of the second entry in the table.
          2       The bitmap has four possible color values.  The most significant half-nibble is to the left in the image.
          4       The bitmap has a maximum of 16 colors, and the bmiColors member contains up to 16 entries.
                  Each pixel in the bitmap is represented by a 4-bit index into the color table. The most significant nibble is to the left in the image.
                  For example, if the first byte in the bitmap is 0x1F, the byte represents two pixels. The first pixel contains the color in the second table entry, and the second pixel contains the color in the sixteenth table entry.
          8       The bitmap has a maximum of 256 colors, and the bmiColors member contains up to 256 entries. In this case, each byte in the array represents a single pixel.
          16      The bitmap has a maximum of 2^16 colors.
                  If the biCompression member of the BITMAPINFOHEADER is BI_RGB, the bmiColors member is NULL.
                  Each WORD in the bitmap array represents a single pixel. The relative intensities of red, green, and blue are represented with 5 bits for each color component.
                  The value for blue is in the least significant 5 bits, followed by 5 bits each for green and red.
                  The most significant bit is not used. The bmiColors color table is used for optimizing colors used on palette-based devices, and must contain the number of entries specified by the biClrUsed member of the BITMAPINFOHEADER.
          24      The bitmap has a maximum of 2^24 colors, and the bmiColors member is NULL.
                  Each 3-byte triplet in the bitmap array represents the relative intensities of blue, green, and red, respectively, for a pixel.
                  The bmiColors color table is used for optimizing colors used on palette-based devices, and must contain the number of entries specified by the biClrUsed member of the BITMAPINFOHEADER.
          32      The bitmap has a maximum of 2^32 colors. If the biCompression member of the BITMAPINFOHEADER is BI_RGB, the bmiColors member is NULL. Each DWORD in the bitmap array represents the relative intensities of blue, green, and red, respectively, for a pixel. The high byte in each DWORD is not used. The bmiColors color table is
                  used for optimizing colors used on palette-based devices, and must contain the number of entries specified by the biClrUsed member of the BITMAPINFOHEADER.
                  If the biCompression member of the BITMAPINFOHEADER is BI_BITFIELDS, the bmiColors member contains three DWORD color masks that specify the red, green, and blue components, respectively, of each pixel.
                  Each DWORD in the bitmap array represents a single pixel.
      biCompression
          Specifies the type of compression for a compressed bottom-up bitmap (top-down DIBs cannot be compressed). This member can be one of the following values.
          Value               Description
          BI_RGB              An uncompressed format.
          BI_BITFIELDS        Specifies that the bitmap is not compressed and that the color table consists of three DWORD color masks that specify the red, green, and blue components of each pixel.
                              This is valid when used with 16- and 32-bpp bitmaps.
                              This value is valid in Windows Embedded CE versions 2.0 and later.
          BI_ALPHABITFIELDS   Specifies that the bitmap is not compressed and that the color table consists of four DWORD color masks that specify the red, green, blue, and alpha components of each pixel.
                              This is valid when used with 16- and 32-bpp bitmaps.
                              This value is valid in Windows CE .NET 4.0 and later.
                              You can OR any of the values in the above table with BI_SRCPREROTATE to specify that the source DIB section has the same rotation angle as the destination.
      biSizeImage
          Specifies the size, in bytes, of the image. This value will be the number of bytes in each scan line which must be padded to
          insure the line is a multiple of 4 bytes (it must align on a DWORD boundary) times the number of rows.
          This value may be set to zero for BI_RGB bitmaps (so you cannot be sure it will be set).
      biXPelsPerMeter
          Specifies the horizontal resolution, in pixels per meter, of the target device for the bitmap.
          An application can use this value to select a bitmap from a resource group that best matches the characteristics of the current device.
      biYPelsPerMeter
          Specifies the vertical resolution, in pixels per meter, of the target device for the bitmap
      biClrUsed
          Specifies the number of color indexes in the color table that are actually used by the bitmap.
          If this value is zero, the bitmap uses the maximum number of colors corresponding to the value of the biBitCount member for the compression mode specified by biCompression.
          If biClrUsed is nonzero and the biBitCount member is less than 16, the biClrUsed member specifies the actual number of colors the graphics engine or device driver accesses.
          If biBitCount is 16 or greater, the biClrUsed member specifies the size of the color table used to optimize performance of the system color palettes.
          If biBitCount equals 16 or 32, the optimal color palette starts immediately following the three DWORD masks.
          If the bitmap is a packed bitmap (a bitmap in which the bitmap array immediately follows the BITMAPINFO header and is referenced by a single pointer), the biClrUsed member must be either zero or the actual size of the color table.
      biClrImportant
          Specifies the number of color indexes required for displaying the bitmap.
          If this value is zero, all colors are required.
      Remarks

      The BITMAPINFO structure combines the BITMAPINFOHEADER structure and a color table to provide a complete definition of the dimensions and colors of a DIB.
      An application should use the information stored in the biSize member to locate the color table in a BITMAPINFO structure, as follows.

      pColor = ((LPSTR)pBitmapInfo + (WORD)(pBitmapInfo->bmiHeader.biSize));
       */

      // Read and convert to big endian
      bmpInfoHeader_biSize          = swapInt(in.readInt());              // DWORD
      bmpInfoHeader_biWidth         = swapInt(in.readInt());              // LONG
      bmpInfoHeader_biHeight        = swapInt(in.readInt());              // LONG
      bmpInfoHeader_biPlanes        = swapShort(in.readUnsignedShort());  // WORD
      bmpInfoHeader_biBitCount      = swapShort(in.readUnsignedShort());  // WORD
      bmpInfoHeader_biCompression   = swapInt(in.readInt());              // DWORD
      bmpInfoHeader_biSizeImage     = swapInt(in.readInt());              // DWORD
      bmpInfoHeader_biXPelsPerMeter = swapInt(in.readInt());              // LONG
      bmpInfoHeader_biYPelsPerMeter = swapInt(in.readInt());              // LONG
      bmpInfoHeader_biClrUsed       = swapInt(in.readInt());              // DWORD
      bmpInfoHeader_biClrImportant  = swapInt(in.readInt());              // DWORD

      /*
      System.out.printf("biSize=%d\nbiWidth=%d\nbiHeight=%d\nbiPlanes=%d\nbiBitCount=%d\nbiCompression=%d\nbiSizeImage=%d\nbiXPelsPerMeter=%d\nbiYPelsPerMeter=%d\nbiClrUsed=%d\nbiClrImportant=%d\n",
                 bmpInfoHeader_biSize,
                 bmpInfoHeader_biWidth,
                 bmpInfoHeader_biHeight,
                 bmpInfoHeader_biPlanes,
                 bmpInfoHeader_biBitCount,
                 bmpInfoHeader_biCompression,
                 bmpInfoHeader_biSizeImage,
                 bmpInfoHeader_biXPelsPerMeter,
                 bmpInfoHeader_biYPelsPerMeter,
                 bmpInfoHeader_biClrUsed,
                 bmpInfoHeader_biClrImportant);

      System.out.printf("\n");
       */

      // Since we use the height to create arrays, it cannot have a negative a value. If the height field is
      // less than zero, then make it positive and set the topDownDIB flag to TRUE so we know that the image is
      // stored on disc upsidedown (which means it is actually rightside up).
      if (bmpInfoHeader_biHeight < 0)
      {
         topDownDIB = true;
         bmpInfoHeader_biHeight = -bmpInfoHeader_biHeight;
      }
   }

   /**
    * Determines the number of colors in the default color table.
    * Initializes the color pallet instance variable.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeColorTable(DataInputStream in) throws Exception
   {
      /*
      Now for the color table. For true color images, there isn't one.

      typedef struct tagRGBQUAD {
              BYTE    rgbBlue;
              BYTE    rgbGreen;
              BYTE    rgbRed;
              BYTE    rgbReserved;
              } RGBQUAD;

      typedef RGBQUAD FAR* LPRGBQUAD;
       */

      int numberOfColors;

      switch (bmpInfoHeader_biBitCount) // Determine the number of colors in the default color table
      {
         case 1:
            numberOfColors = 2;
            break;
         case 2:
            numberOfColors = 4;
            break;
         case 4:
            numberOfColors = 16;
            break;
         case 8:
            numberOfColors = 256;
            break;
         default:
            numberOfColors = 0; // no color table
      }//switch

      /*
       * biClrUsed -  Specifies the number of color indexes in the color table that are actually used by the bitmap.
       *     If this value is zero, the bitmap uses the maximum number of colors corresponding to the value of the biBitCount member for the compression mode specified by biCompression.
       *     If biClrUsed is nonzero and the biBitCount member is less than 16, the biClrUsed member specifies the actual number of colors the graphics engine or device driver accesses.
       *     If biBitCount is 16 or greater, the biClrUsed member specifies the size of the color table used to optimize performance of the system color palettes.
       *     If biBitCount equals 16 or 32, the optimal color palette starts immediately following the three DWORD masks.
       *     If the bitmap is a packed bitmap (a bitmap in which the bitmap array immediately follows the BITMAPINFO header and is referenced by a single pointer), the biClrUsed member must be either zero or the actual size of the color table.
       */
      if (bmpInfoHeader_biClrUsed > 0) numberOfColors = bmpInfoHeader_biClrUsed;
            
      for (int i = 0; i < numberOfColors; ++i) // Read in the color table (or not if numberOfColors is zero)
      {
         int rgbQuad_rgbBlue      = in.readUnsignedByte(); // lowest byte in the color
         int rgbQuad_rgbGreen     = in.readUnsignedByte();
         int rgbQuad_rgbRed       = in.readUnsignedByte(); // highest byte in the color
         int rgbQuad_rgbReserved  = in.readUnsignedByte();

         // Build the color from the RGB values. Since we declared the rgbQuad values to be int, we can shift and then OR the values
         // to build up the color. Since we are reading one byte at a time, there are no "endian" issues.

         colorPallet[i] = (rgbQuad_rgbRed << 16) | (rgbQuad_rgbGreen << 8) | rgbQuad_rgbBlue;
         // System.out.printf("DEBUG: Color Table = %d, %06X\n", i, colorPallet[i]);
      } // for (i = 0; i < numberOfColors; ++i)
   }

   /**
    * Initializes the image array based on the bit count of the image.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */   
   private void initializeImageArray(DataInputStream in) throws Exception
   {
      /*
       * Now for the fun part. We need to read in the rest of the bit map, but how we interpret the values depends on the color depth.
       *
       * numberOfColors = 2:   Each bit is a pel, so there are 8 pels per byte. The Color Table has only two values for "black" and "white"
       * numberOfColors = 4:   Each pair of bits is a pel, so there are 4 pels per byte. The Color Table has only four values
       * numberOfColors = 16;  Each nibble (4 bits) is a pel, so there are 2 pels per byte. The Color Table has 16 entries.
       * numberOfColors = 256; Each byte is a pel and the value maps into the 256 byte Color Table.
       *
       * Any other value is read in as "true" color.
       *
       * The BMP image is stored from bottom to top, meaning that the first scan line is the last scan line in the image.
       *
       * The rest is the bitmap. Use the height and width information to read it in. And as I mentioned before....
       * In the 24-bit format, each pixel in the image is represented by a series of three bytes of RGB stored as BRG.
       * For ALL image types each scan line is padded to an even 4-byte boundary.
       *
       */

      imageArray = new int[bmpInfoHeader_biHeight][bmpInfoHeader_biWidth]; // Create the array for the pels

      /*
       * I use the same loop structure for each case for clarity so you can see the similarities and differences.
       * The outer loop is over the rows (in reverse), the inner loop over the columns. 
       */
      switch (bmpInfoHeader_biBitCount)
      {
         case 1:
            initializeImageArray_1Bit(in);
            break;
         case 2: 
            initializeImageArray_2Bit(in);
            break;
         case 4: 
            initializeImageArray_4Bit(in);
            break;
         case 8: 
            initializeImageArray_8Bit(in);
            break;
         case 16:
            initializeImageArray_16Bit(in);
            break;
         case 24: 
            initializeImageArray_24Bit(in);
            break;
         case 32:
            initializeImageArray_32Bit(in);
            break;
         default: // Oops
            System.out.printf("Unreadable bit count. This error should not occur - 1!\n");

      } // switch (bmpInfoHeader_biBitCount)
   }

   /**
    * An option to read in the image array of the bitmap based on a 1 bit count
    * (black and white) in the initialized file header.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeImageArray_1Bit(DataInputStream in) throws Exception
   {
      // each bit is a color, so there are 8 pels per byte.  Works
      /*
       * Each byte read in is 8 columns, so we need to break them out. We also have to deal with the case
       * where the image width is not an integer multiple of 8, in which case we will
       * have bits from part of the remaining byte. Each color is 1 bit which is masked with 0x01.
       * The screen ordering of the pels is High-Bit to Low-Bit, so the most significant element is first in the array of pels.
       */
      int iBytesPerRow = bmpInfoHeader_biWidth / 8;
      int iTrailingBits = bmpInfoHeader_biWidth % 8;

      int iDeadBytes = iBytesPerRow;
      if (iTrailingBits > 0) ++iDeadBytes;
      iDeadBytes = (4 - iDeadBytes % 4) % 4;

      for (int row = 0; row < bmpInfoHeader_biHeight; ++row) // read over the rows
      {
         int i;
         if (topDownDIB) i = row; else i = bmpInfoHeader_biHeight - 1 - row;
         
         for (int j = 0; j < iBytesPerRow; ++j)
         {
            int iByteVal = in.readUnsignedByte();

            for (int k = 0; k < 8; ++k)     // Get 8 pels from the one byte
            {
               int iColumn = j * 8 + k;
               int pel = colorPallet[(iByteVal >> (7 - k)) & 0x01];
               imageArray[i][iColumn] = pel;
            }
         }
         if (iTrailingBits > 0) // pick up the trailing bits for images that are not mod 8 columns wide
         {
            int iByteVal = in.readUnsignedByte();

            for (int k = 0; k < iTrailingBits; ++k)
            {
               int iColumn = iBytesPerRow * 8 + k;
               int pel = colorPallet[(iByteVal >> (7 - k)) & 0x01];
               imageArray[i][iColumn] = pel;
            }
         }

         for (int j = 0; j < iDeadBytes; ++j) in.readUnsignedByte(); // Now read in the "dead bytes" to pad to a 4 byte boundary
      }
   }

   /**
    * An option to read in the image array of the bitmap based on a 2 bit count
    * in the initialized file header.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeImageArray_2Bit(DataInputStream in) throws Exception
   {
      // 4 colors, Each byte is 4 pels (2 bits each),  Should work, not tested.
      /*
       * Each byte read in is 4 columns, so we need to break them out. We also have to deal with the case
       * where the image width is not an integer multiple of 4, in which case we will
       * have from 2 to 6 bits of the remaining byte. Each color is 2 bits which is masked with 0x03.
       * The screen ordering of the pels is High-Half-Nibble to Low-Half-Nibble, so the most significant element is first in the array of pels.
       */
      int iBytesPerRow = bmpInfoHeader_biWidth / 4;
      int iTrailingBits = bmpInfoHeader_biWidth % 4; // 0, 1, 2 or 3

      int iDeadBytes = iBytesPerRow;
      if (iTrailingBits > 0) ++iDeadBytes;
      iDeadBytes = (4 - iDeadBytes % 4) % 4;

      for (int row = 0; row < bmpInfoHeader_biHeight; ++row) // read over the rows
      {
         int i;
         if (topDownDIB) i = row; else i = bmpInfoHeader_biHeight - 1 - row;

         for (int j = 0; j < iBytesPerRow; ++j)
         {
            int iByteVal = in.readUnsignedByte();

            for (int k = 0; k < 4; ++k) // Get 4 pels from one byte
            {
               int iColumn = j * 4 + k;
               int pel = colorPallet[(iByteVal >> ((3 - k) * 2)) & 0x03]; // shift 2 bits at a time and reverse order
               imageArray[i][iColumn] = pel;
            }
         }
         if (iTrailingBits > 0) // pick up the trailing nibble for images that are not mod 2 columns wide
         {
            int iByteVal = in.readUnsignedByte();

            for (int k = 0; k < iTrailingBits; ++k)
            {
               int iColumn = iBytesPerRow * 4 + k;  
               int pel = colorPallet[(iByteVal >> ((3 - k) * 2)) & 0x03];
               imageArray[i][iColumn] = pel;
            }
         }
         for (int j = 0; j < iDeadBytes; ++j) in.readUnsignedByte(); // Now read in the "dead bytes" to pad to a 4 byte boundary
      }
   }

   /**
    * An option to read in the image array of the bitmap based on a 4 bit count
    * in the initialized file header.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeImageArray_4Bit(DataInputStream in) throws Exception
   {
      // 16 colors, Each byte is two pels. Works
      /*
       * Each byte read in is 2 columns, so we need to break them out. We also have to deal with the case
       * where the image width is not an integer multiple of 2, in which case we will
       * have one nibble from part of the remaining byte. We then read in the dead bytes so that each
       * scan line is a multiple of 4 bytes. Each color is a nibble (4 bits) which is masked with 0x0F.
       * The screen ordering of the pels is High-Nibble Low-Nibble, so the most significant element is first in the array of pels.
       */
      int iPelsPerRow   = bmpInfoHeader_biWidth;
      int iBytesPerRow  = iPelsPerRow / 2;
      int iTrailingBits = iPelsPerRow % 2;  // Will either be 0 or 1

      int iDeadBytes = iBytesPerRow;
      if (iTrailingBits > 0) ++iDeadBytes;
      iDeadBytes = (4 - iDeadBytes % 4) % 4;

      for (int row = 0; row < bmpInfoHeader_biHeight; ++row) // read over the rows
      {
         int i;
         if (topDownDIB) i = row; else i = bmpInfoHeader_biHeight - 1 - row;

         for (int j = 0; j < iBytesPerRow; ++j)
         {
            int iByteVal = in.readUnsignedByte();

            for (int k = 0; k < 2; ++k) // Two pels per byte
            {
               int iColumn = j * 2 + k;          // 1 - k  is needed to have High, Low nibble ordering for the image.
               int pel = colorPallet[(iByteVal >> ((1 - k) * 4)) & 0x0F]; // shift 4 bits at a time
               imageArray[i][iColumn] = pel;
            }
         }

         if (iTrailingBits > 0) // pick up the trailing nibble for images that are not mod 2 columns wide
         {
            int iByteVal = in.readUnsignedByte();

            int iColumn = iBytesPerRow * 2;
            int pel = colorPallet[(iByteVal >> 4) & 0x0F]; // The High nibble is the last remaining pel
            imageArray[i][iColumn] = pel;
         }
         for (int j = 0; j < iDeadBytes; ++j) in.readUnsignedByte(); // Now read in the "dead bytes" to pad to a 4 byte boundary
      } // for (i = bmpInfoHeader_biHeight - 1; i >= 0; --i)
   }

   /**
    * An option to read in the image array of the bitmap based on an 8 bit count
    * in the initialized file header.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeImageArray_8Bit(DataInputStream in) throws Exception
   {
      // 1 byte, 1 pel, Works
      /*
       * Each byte read in is 1 column. We then read in the dead bytes so that each scan line is a multiple of 4 bytes.
       */
      int iPelsPerRow = bmpInfoHeader_biWidth;
      int iDeadBytes = (4 - iPelsPerRow % 4) % 4;
      for (int row = 0; row < bmpInfoHeader_biHeight; ++row) // read over the rows
      {
         int i;
         if (topDownDIB) i = row; else i = bmpInfoHeader_biHeight - 1 - row;

         for (int j = 0; j < iPelsPerRow; ++j)         // j is now just the column counter
         {
            int iByteVal = in.readUnsignedByte();
            int pel = colorPallet[iByteVal];
            imageArray[i][j] = pel;
         }

         for (int j = 0; j < iDeadBytes; ++j) in.readUnsignedByte(); // Now read in the "dead bytes" to pad to a 4 byte boundary
      }
   }

   /**
    * An option to read in the image array of the bitmap based on a 16 bit count
    * in the initialized file header.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeImageArray_16Bit(DataInputStream in) throws Exception
   {
      // Should work, not tested.
      /*
       * Each two bytes read in is 1 column. Each color is 5 bits in the 2 byte word value, so we shift 5 bits and then mask them
       * off with 0x1F which is %11111 in binary. We then read in the dead bytes so that each scan line is a multiple of 4 bytes.
       */
      int iPelsPerRow = bmpInfoHeader_biWidth;
      int iDeadBytes = (4 - iPelsPerRow % 4) % 4;
      for (int row = 0; row < bmpInfoHeader_biHeight; ++row) // read over the rows
      {
         int i;
         if (topDownDIB) i = row; else i = bmpInfoHeader_biHeight - 1 - row;

         for (int j = 0; j < iPelsPerRow; ++j)         // j is now just the column counter
         {
            int pel = swapShort(in.readUnsignedShort()); // Need to deal with little endian values
            int rgbQuad_rgbBlue      =  pel        & 0x1F;
            int rgbQuad_rgbGreen     = (pel >> 5)  & 0x1F;   
            int rgbQuad_rgbRed       = (pel >> 10) & 0x1F;
            pel = (rgbQuad_rgbRed << 16) | (rgbQuad_rgbGreen << 8) | rgbQuad_rgbBlue;
            imageArray[i][j] = pel;
         }

         for (int j = 0; j < iDeadBytes; ++j) in.readUnsignedByte(); // Now read in the "dead bytes" to pad to a 4 byte boundary
      } // for (i = bmpInfoHeader_biHeight - 1; i >= 0; --i)
   }

   /**
    * An option to read in the image array of the bitmap based on a 24 bit count
    * (true color) in the initialized file header.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeImageArray_24Bit(DataInputStream in) throws Exception
   {
      // True color - Works
      /*
       * Each three bytes read in is 1 column. Each scan line is padded to by a multiple of 4 bytes.
       */
      int iPelsPerRow = bmpInfoHeader_biWidth;
      int iDeadBytes = (4 - (iPelsPerRow * 3) % 4) % 4;

      for (int row = 0; row < bmpInfoHeader_biHeight; ++row) // read over the rows
      {
         int i;
         if (topDownDIB) i = row; else i = bmpInfoHeader_biHeight - 1 - row;

         for (int j = 0; j < iPelsPerRow; ++j)         // j is now just the column counter
         {
            int rgbQuad_rgbBlue      = in.readUnsignedByte();
            int rgbQuad_rgbGreen     = in.readUnsignedByte();
            int rgbQuad_rgbRed       = in.readUnsignedByte();
            int pel = (rgbQuad_rgbRed << 16) | (rgbQuad_rgbGreen << 8) | rgbQuad_rgbBlue;
            imageArray[i][j] = pel;
         }
         for (int j = 0; j < iDeadBytes; ++j) in.readUnsignedByte(); // Now read in the "dead bytes" to pad to a 4 byte boundary
      }
   }

   /**
    * An option to read in the image array of the bitmap based on a 32 bit count
    * in the initialized file header.
    *
    * @param in the DataInputStream previously created to read the bitmap.
    */
   private void initializeImageArray_32Bit(DataInputStream in) throws Exception
   {
      // Works
      /*
       * Each four bytes read in is 1 column. The number of bytes per line will always be a multiple of 4, so there are no dead bytes.
       */
      int iPelsPerRow = bmpInfoHeader_biWidth;
      for (int row = 0; row < bmpInfoHeader_biHeight; ++row) // read over the rows
      {
         int i;
         if (topDownDIB) i = row; else i = bmpInfoHeader_biHeight - 1 - row;

         for (int j = 0; j < iPelsPerRow; ++j)         // j is now just the column counter
         {
            int rgbQuad_rgbBlue      = in.readUnsignedByte();
            int rgbQuad_rgbGreen     = in.readUnsignedByte();
            int rgbQuad_rgbRed       = in.readUnsignedByte();
            int rgbQuad_rgbReserved  = in.readUnsignedByte();
            int pel =  (rgbQuad_rgbReserved << 24) |(rgbQuad_rgbRed << 16) | (rgbQuad_rgbGreen << 8) | rgbQuad_rgbBlue;
            imageArray[i][j] = pel;
         }
      }
   }

   /**
    * Gets the image array imageArray with dimensions [bmpHeight][bmpWidth].
    *
    * @return the imageArray instance variable
    */
   public int[][] getImageArray()
   {
      return imageArray;
   }

   /**
    * Sets the image array to a passed 2d integer array.
    *
    * @param 2d int array to set the imageArray
    */
   public void setImageArray(int[][] newImageArray)
   {
      imageArray = newImageArray;
   }

   /**
    * Gets grayscale version of the image array by passing the pels through the colorToGrayscale method.
    *
    * @return grayscale version of image array
    *
    * ORIGINAL PASSED ARRAY IS CHANGED
    */
   public int[][] getGrayscaleImageArray()
   {
      for (int i = 0; i < imageArray.length; i++)
         for (int j = 0; j < imageArray[0].length; j++)
            imageArray[i][j] = colorToGrayscale(imageArray[i][j]);

      return imageArray;
   }

   /**
    * Gets monochrome version of the image array by setting all nonzero pels to 1.
    *
    * @return monochrome version of arr
    *
    * ORIGINAL PASSED ARRAY IS CHANGED
    */
   public int[][] getMonochromeImageArray(int[][] arr)
   {
      for (int i = 0; i < imageArray.length; i++)
         for (int j = 0; j < imageArray[0].length; j++)
         {
            if (arr[i][j] != 0)
               arr[i][j] = 1;
         }

      return arr;
   }

   /**
    * Static method that flips values of any 2d array.
    * By default bitmap image array is 0 when black and goes up as it approaches white.
    * This needs to be flipped for the purposes of the neural network.
    * Masks off the reserved bits as well as red and blue bits and makes sure they stay at value 0.
    * Intended for use with grayscale image array.
    *
    * Precondition: Values of array between 0 and 1
    * Postcondition: every value in array will be reset to (1-original value)
    *
    * ORIGINAL PASSED ARRAY IS CHANGED
    */
   public int[][] flipArrayBits(int[][] arr)
   {
      for (int i = 0; i < arr.length; i++)
         for (int j = 0; j < arr[0].length; j++)
            arr[i][j] = invertPel(arr[i][j]);

      return arr;
   }

   /**
    * Writes out the imageArray instance variable to a bitmap disk file.
    * This method exists to verify changing the imageArray.
    *
    * @precondition the bitmap headers are already initialized in the instance of DibDumpAdapted
    *
    * @param filename the output filename of the bitmap
    */
   public void writeBitmapToDisk(String filename)
   {
      try
      {
         bmpInfoHeader_biHeight = imageArray.length;
         bmpInfoHeader_biWidth = imageArray[0].length;
         
         int iDeadBytes = (4 - (bmpInfoHeader_biWidth * 3) % 4) % 4;

         bmpInfoHeader_biSizeImage =  (bmpInfoHeader_biWidth * 3 + iDeadBytes) * bmpInfoHeader_biHeight;
         bmpFileHeader_bfOffBits = 54;        // 54 byte offset for 24 bit images (just open one with this app to get this value)
         bmpFileHeader_bfSize = bmpInfoHeader_biSizeImage + bmpFileHeader_bfOffBits;
         bmpInfoHeader_biBitCount = 24;       // 24 bit color image
         bmpInfoHeader_biCompression = 0;     // BI_RGB (which is a value of zero)
         bmpInfoHeader_biClrUsed = 0;         // Zero for true color
         bmpInfoHeader_biClrImportant = 0;    // Zero for true color

         FileOutputStream fstream = new FileOutputStream(filename);
         DataOutputStream out = new DataOutputStream(fstream);

         // BITMAPFILEHEADER
         out.writeShort(swapShort(bmpFileHeader_bfType));      // WORD
         out.writeInt(swapInt(bmpFileHeader_bfSize));          // DWORD
         out.writeShort(swapShort(bmpFileHeader_bfReserved1)); // WORD
         out.writeShort(swapShort(bmpFileHeader_bfReserved2)); // WORD
         out.writeInt(swapInt(bmpFileHeader_bfOffBits));       // DWORD

         // BITMAPINFOHEADER
         out.writeInt(swapInt(bmpInfoHeader_biSize));          // DWORD
         out.writeInt(swapInt(bmpInfoHeader_biWidth));         // LONG
         out.writeInt(swapInt(bmpInfoHeader_biHeight));        // LONG
         out.writeShort(swapShort(bmpInfoHeader_biPlanes));    // WORD
         out.writeShort(swapShort(bmpInfoHeader_biBitCount));  // WORD
         out.writeInt(swapInt(bmpInfoHeader_biCompression));   // DWORD
         out.writeInt(swapInt(bmpInfoHeader_biSizeImage));     // DWORD
         out.writeInt(swapInt(bmpInfoHeader_biXPelsPerMeter)); // LONG
         out.writeInt(swapInt(bmpInfoHeader_biYPelsPerMeter)); // LONG
         out.writeInt(swapInt(bmpInfoHeader_biClrUsed));       // DWORD
         out.writeInt(swapInt(bmpInfoHeader_biClrImportant));  // DWORD

         // there is no color table for this true color image, so write out the pels

         for (int i = bmpInfoHeader_biHeight - 1; i >= 0; --i)    // write over the rows (in the usual inverted format)
         {
            for (int j = 0; j < bmpInfoHeader_biWidth; ++j) // and the columns
            {
               int pel = imageArray[i][j];
               int rgbQuad_rgbBlue  = pel & 0x00FF;
               int rgbQuad_rgbGreen = (pel >> 8)  & 0x00FF;
               int rgbQuad_rgbRed   = (pel >> 16) & 0x00FF;
               out.writeByte(rgbQuad_rgbBlue); // lowest byte in the color
               out.writeByte(rgbQuad_rgbGreen);
               out.writeByte(rgbQuad_rgbRed);  // highest byte in the color
            }
            for (int j = 0; j < iDeadBytes; ++j)
            {
               out.writeByte(0); // Now write out the "dead bytes" to pad to a 4 byte boundary
            }
         } // for (i = bmpInfoHeader_biHeight - 1; i >= 0; --i)

         out.close();
         fstream.close();
      }
      catch (Exception e)
      {
         System.err.println("File output error" + e);
      }
   }

   /**
    * Returns a 1 dimensional version of the 2d imageArray array.
    * Used to feed inputs into our perceptron.
    * 
    * @return 1d array version of 2d imageArray
    */
   public double[] getFlattenedImageArray()
   {
      double[] flattenedPattern = new double [imageArray.length * imageArray[0].length];
      for (int x = 0; x < imageArray.length; x++)
         for (int y = 0; y < imageArray[0].length; y++)
            flattenedPattern[(x * imageArray.length) + y] = imageArray[x][y];
      return flattenedPattern;
   }

   /**
    * The main method will serve as a test to read in a bitmap and 
    * immediately write a grayscale image of the bitmap to a disk.
    *
    * @param args must be 2 strings - 1st string is input filename and 2nd string is output filename
    */
   public static void main(String[] args)
   {
      DibDumpAdapted dibdumper = new DibDumpAdapted(args[0]);
      dibdumper.writeBitmapToDisk(args[1]);
   }

}
