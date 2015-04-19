import java.io.*;
import java.util.*;
/**
 * Breaks down bitmap files and normalizes the file to allow the perceptron
 * to use the bitmap files as inputs.
 * 
 * This class will take as inputs a filename and the requested dimensions of the bitmap.
 * It will load in the image using an adapted version of Dr. Eric Nelson's DibDumpAdapted.java file.
 * It will then use a 2-step process to convert the existing bmp image.
 * 
 * 1. Center of Mass
 *    The class will find the center of mass of the object in the bitmap file.
 *    Uses physics equations of center of mass to calculate center of mass.
 *    Used to find the middle of the image in mind, and eventually move it to the middle.
 * 2. Crop Image
 *    The image will be cropped with the center of mass in the middle based on crop dimensions.
 *    If the crop along with COM results in out-of-bounds, the processing will fill the outside with zeros.
 * 
 * In this way, we break down bitmap images and make different dimensional images
 * compatible to be inputed into our perceptron.
 * 
 * @author Nathan Dalal
 * @version 10/13/14
 */
public class BitmapUtility
{
   public static final int FILL_VALUE = 0;

   public final ArrayList<String> FILE_LIST;

   private final int CROP_DIM_X;
   private final int CROP_DIM_Y;

   private DibDumpAdapted[] dibdumpers;
   
   /**
    * Finds names of all bitmap files in directory.
    *
    * @precondition all files must be .bmp and directory must be defined
    *
    * @param dir the directory to look for files
    * @return ArrayList of String filenames
    */
   private ArrayList<String> listAllBitmapFileNames(File dir)
   {
      ArrayList<String> filenames = new ArrayList<String>();

      for (File f : dir.listFiles()) 
         if (f.getName().substring(f.getName().length()-3).equals("bmp"))
            filenames.add(f.getName());

      return filenames;
   }

   /**
    * Finds paths of all bitmap files in directory.
    *
    * @precondition all files must be .bmp and directory must be defined
    *
    * @param ArrayList of string filenames
    * @return ArrayList of String file paths
    */
   public ArrayList<String> listAllBitmapFileLocations(File dir, ArrayList<String> filenames)
   {
      ArrayList<String> filepaths = new ArrayList<String>();

      for (int i = 0; i < filenames.size(); i++)
         filepaths.add(dir + "/" + filenames.get(i));

      return filepaths;
   }

   /**
    * Initializes final dimensions of image and image filename.
    * Reads in bits of file using segments of Eric Nelson's DibDump.java file.
    * Fits data to specifications by finding image COM and cropping around it.
    * This allows our perceptron to find "a image" and not "the image".
    * 
    * @param dimx what dimx the bitmap should be normalized to when done
    * @param dimy what dimy the bitmap should be normalized to when done
    * @param directory the bitmap file directory denoting where the .bmp files are
    */
   public BitmapUtility (String directory, int crop_x, int crop_y, boolean isAscii)
   {
      FILE_LIST = listAllBitmapFileNames(new File(directory));
      ArrayList<String> filepaths = listAllBitmapFileLocations(new File(directory), FILE_LIST);

      //set crops for bitmap (inverted to get right dimensions)
      CROP_DIM_X = crop_y; 
      CROP_DIM_Y = crop_x;

      dibdumpers = new DibDumpAdapted[filepaths.size()];
      System.out.println();
      for (int i = 0; i < dibdumpers.length; i++)
      {
         System.out.println("Processing " + filepaths.get(i));

         dibdumpers[i] = new DibDumpAdapted(filepaths.get(i));
         int[][] grayscaleFlippedImageArray;

         if (isAscii) //monochrome for ascii
         {
            grayscaleFlippedImageArray = dibdumpers[i].getMonochromeImageArray(dibdumpers[i].flipArrayBits(dibdumpers[i].getGrayscaleImageArray()));
         }
         else //normal grayscale for all else
         {
            grayscaleFlippedImageArray = dibdumpers[i].flipArrayBits(dibdumpers[i].getGrayscaleImageArray());
         }
         
         int[][] processedImageArray = processImageArray(grayscaleFlippedImageArray);
         dibdumpers[i].setImageArray(processedImageArray);
      }
   }
   
   /**
    * Finds x-com of pixels.
    * Uses reference point at imageArray[0][0]
    * 
    * @return int position of xcom
    */
   private int getXcom(int[][] imageArray)
   {
      int topSum = 0;
      int bottomSum = 0;

      for (int i = 0; i < imageArray.length; i++)
      {
         for (int j = 0; j < imageArray[0].length; j++)
         {
            topSum += imageArray[i][j]*i;
            bottomSum += imageArray[i][j];
         }
      }

      return topSum/bottomSum;
   }

   /**
    * Finds y-com of pixels.
    * Uses reference point at imageArray[0][0]
    * 
    * @return int position of ycom
    */
   private int getYcom(int[][] imageArray)
   {
      int topSum = 0;
      int bottomSum = 0;

      for (int i = 0; i < imageArray.length; i++)
      {
         for (int j = 0; j < imageArray[0].length; j++)
         {
            topSum += imageArray[i][j]*j;
            bottomSum += imageArray[i][j];
         }
      }

      return topSum/bottomSum;
   }

   /**
    * Processes image and saves the new imageArray in the instance variable.
    * Crops around the center of mass based on the dimensions in CROP_DIM_X and CROP_DIM_Y.
    * Fills the rest of the image with the fill value if the crop results in out-of-bounds.
    */
   public int[][] processImageArray(int[][] imageArray)
   {
      int xstart = getXcom(imageArray) - (CROP_DIM_X/2);
      int ystart = getYcom(imageArray) - (CROP_DIM_Y/2);

      int[][] newImageArray = new int[CROP_DIM_X][CROP_DIM_Y];

      for (int i = 0; i < CROP_DIM_X; i++)
      {
         for (int j = 0; j < CROP_DIM_Y; j++)
         {
            try 
            {
               newImageArray[i][j] = imageArray[xstart+i][ystart+j];
            }
            catch (IndexOutOfBoundsException e) 
            {
               newImageArray[i][j] = FILL_VALUE;
            }
         }//crop y
      }//crop x

      imageArray = newImageArray;

      return imageArray;
   }

   /**
    * Gets the dibdumper in this instance of BitmapUtility.
    *
    * @return dibdumper
    */
   public DibDumpAdapted[] getDibDumperArray()
   {
      return dibdumpers;
   }

   /**
    * Writes all bitmas stored in utility to disk.
    *
    * @param outDir the directory the images are written to after processing
    */
   public void writeAllBitmapsToDisk(File outDir)
   {
      ArrayList<String> filepaths = listAllBitmapFileLocations(outDir, FILE_LIST);
      for (int i = 0; i < dibdumpers.length; i++)
      {
         System.out.println("Writing " + filepaths.get(i));
         dibdumpers[i].writeBitmapToDisk(filepaths.get(i));
      }

      return;
   }

   /**
    * Not intended for non-testing purposes.
    *
    * Takes first 2 arguments for X and Y crop dimensions.
    *
    * After that, takes 1 argument to read in from 1 directory.
    * Can also take 2 arguments - #2 would be to output the processed files to a new directory
    *
    * @param args from command line
    */
   public static void main (String[] args)
   {
      BitmapUtility bmputil = new BitmapUtility(args[2], Integer.parseInt(args[0]), Integer.parseInt(args[1]), false);
      if (args.length == 4)
      {
         File outDir = new File(args[3]);
         outDir.mkdirs();
         bmputil.writeAllBitmapsToDisk(outDir);
      }

      return;
   }
}
