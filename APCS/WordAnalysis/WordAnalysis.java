import java.util.*;
import java.io.*;
/**
 * WordAnalysis breaks down a read-in text file and returns data about the text file requested by
 * the user. It can return number of times a word appears, as well as other functions.
 * 
 * @author Nathan Dalal
 * @version 2/5/13
 */
@SuppressWarnings("resource")
public class WordAnalysis
{
   private ArrayList <Word> words;

   /**
    * Default constructor needed for compilation.
    */
   public WordAnalysis()
   {}
   
    /**
    * Constructor for objects of class WordAnalysis.
    * 
    * @param filename the file to be read.
    * 
    * @throws IOException
    */
   public WordAnalysis(String filename)throws IOException
   {
       words = new ArrayList<Word>();
       readFile(filename);   
   }
   
   /**
     * This will read the file of MobyDick.txt one line at a time.
     * 
     * @param   String filename     the file name
     * @throws  IOException         if file with the word information cannot be found
     * @author Susan King
     */
    public void readFile(String filename) throws IOException
    {
        Scanner inFile = new Scanner(new File(filename));
        while(inFile.hasNext())
        {
            String str = inFile.next().toLowerCase().trim();
            String tempName = cleanUp(str);
            if (tempName != null && tempName.length() > 0)
            {
                findOrInsertWord(tempName);
            }
        }
        inFile.close();
    }

   /**
    * Finds the index of a word in the ArrayList words
    * However, it doesn't really matter what the index is for the purpose of readFile. 
    * readFile just checks if it is in the ArrayList at all.
    * 
    * @param   String the word we are looking at
    * 
    * @return  int the index of the word in the ArrayList words
    * 
    * @author Susan King
    */
   public int findWord(String str)
   {
       for (int i = 0; i < words.size(); i++)
       {
           if (words.get(i).getWord().equals(str))
           {
               return i;
           }
       }
       return -1;
   }

   /**
    * Adds a word to the ArrayList words
    * 
    * @Precondition: the word is not already in the ArrayList
    * 
    * @param   String str that we are adding as a Word to the ArrayList words
    * 
    * @author Susan King
    */
   public void addWord(String str)
   {
       Word addition = new Word(str);
       words.add(addition);
   }

   /**
    * Cleans up a string of unecessary characters. Makes sure that they are characters in the alphabet
    * 
    * @param   s the String that we are modifying before we add to the ArrayList words
    * 
    * @return  String the newer, modified word
    * 
    * @author Susan King
    */
   public String cleanUp(String s)
   {
       String letters = "";
       for (int i = 0; i < s.length(); i++)
       {
           char c = s.charAt(i);
           if (Character.isLetter(c))
           {
               letters += c;
           }
       }
       return letters;
   }

   /**
    * Finds the txt in the words list. Once found, it adds to the frequency.
    * If txt is not in the main txt, adds the Word into instance variable words.
    * 
    * @param txt the text to be checked with the ArrayList
    * 
    * @return int where the Word should go
    */
    public int findOrInsertWord(String txt)
    {
        int high = words.size() - 1;
        int low = 0;
        int mid = (high + low) / 2;
        while ( high >= low)
        {
            mid = (high + low) / 2;
            int c = words.get(mid).getWord().compareTo(txt);
            if (c == 0)
            {
                words.get(mid).addFrequency();
                return mid;
            }
            else if (c < 0)
            {
                low = mid + 1;
            }
            else
            {
                high = mid - 1;
            }
        }
        return addWord(txt, high);
    }

   /**
    * Inserts the word alphabetically into the ArrayList words
    * 
    * @param   String the words being inserted
    * @param   index the index of the ArrayList
    * 
    * @return  int the index that the word is being inserted
    */
   public int addWord(String text, int index)
   {
       while (index >= 0 && index < words.size() && words.get(index).getWord().compareTo(text) < 0)
       {
           index++;
       }
       Word w = new Word(text);
       if (0 < index && index < words.size())
       {
           words.add(index, w);
           return index;
       }
       else if (index < 0 && words.size() == 0)
       {
           words.add(w);
           return words.size() - 1;
       }
       else if (index < 0)
       {
           words.add(0, w);
           return 0;
       }
       words.add(w);
       return words.size() - 1;
   }
   
   /**
    * Finds the number of words in the book.
    * 
    * @return number of words in the book.
    */
   public int lengthOfBook()
   {
       int counter = 0;
       for (int i = 0; i < words.size(); i++)
           counter += words.get(i).getFrequency();
       return counter;
   }
   
   /**
    * Binary search for a Word in words.
    * 
    * @param key the String to be searched for
    * @param start the starting index
    * @param end the finishing index that is included
    * 
    * @return int where the key is in the instance variable words
    */
   public int binarySearch (String key, int start, int end)
   {
       int mid = (start + end) / 2;
       if (start > end)
           return -1;
       if (words.get(mid).getWord().compareTo(key) == 0)
           return mid;
       if (words.get(mid).getWord().compareTo(key) < 0)
           return binarySearch (key, mid + 1, end);
       return binarySearch (key, start, mid - 1);
   }
   
   /**
    * Sorts the list of words lexicographically using a merge sort.
    * 
    * @param start the starting index which is included
    * @param end  the finishing index which is included
    */
   public void alphaSort (int start, int end)
   {
       if (start == end)
           return;
       else
       {
           int mid = (start + end) / 2;
           alphaSort (start, mid);
           alphaSort (mid + 1, end);
           alphaMerge (start, mid + 1, end);
       }
   }
   
   /**
    * Implemented in aplhaSort, used to sort stacks in the recursion.
    * 
    * @param start the starting index
    * @param mid the middle index
    * @param end the ending index
    */
   public void alphaMerge (int start, int mid, int end)
   {
       int aPtr = start;
       int bPtr = mid;
       ArrayList <Word> temp = new ArrayList <Word>();
       for (int k = 0; k < end - start + 1; k++)
       {
           if (aPtr >= mid)
           {
               temp.add (words.get (bPtr));
               bPtr++;
           }
           else if (bPtr > end)
           {
               temp.add (words.get (aPtr));
               aPtr++;
           }
           else if (words.get(aPtr).compareTo(words.get(bPtr)) >= 0)
           {
               temp.add (words.get (bPtr));
               bPtr++;
           }
           else
           {
               temp.add (words.get (aPtr));
               aPtr++;
           }
       }    
       for (int i = 0; i < temp.size(); i++)
               words.set (start + i, temp.get(i));
   }
   
   /**
    * Sorts the list of words by frequency descending using a merge sort.
    * 
    * @param start the starting index which is included
    * @param end  the finishing index which is included
    */
   public void frequentSort (int start, int end)
   {
       if (start == end)
           return;
       else
       {
           int mid = (start + end) / 2;
           frequentSort (start, mid);
           frequentSort (mid + 1, end);
           frequentMerge (start, mid + 1, end);
       }
   }
   
   /**
    * Implemented in frequentSort, used to sort stacks in the recursion descending.
    * 
    * @param start the starting index
    * @param mid the middile index
    * @param end the ending index
    */
   public void frequentMerge (int start, int mid, int end)
   {
       int aPtr = start;
       int bPtr = mid;
       ArrayList <Word> temp = new ArrayList <Word>(end - start + 1);
       for (int k = 0; k < end - start + 1; k++)
       {
           if (aPtr >= mid)
           {
               temp.add (words.get (bPtr));
               bPtr++;
           }
           else if (bPtr > end)
           {
               temp.add (words.get (aPtr));
               aPtr++;
           }
           else if (words.get(aPtr).compareFrequencyTo(words.get(bPtr)) <= 0)
           {
               temp.add (words.get (bPtr));
               bPtr++;
           }
           else
           {
               temp.add (words.get (aPtr));
               aPtr++;
           }
       }    
       for (int i = 0; i < temp.size(); i++)
               words.set (start + i, temp.get(i));
   }
   
   /**
    * Finds the number of unique words in the book.
    * 
    * @return number of unique words in the book.
    */
   public int uniqueWords()
   {
       return words.size();
   }
   
   /**
    * Finds the x most frequent words, an x passed in by the user.
    * 
    * @param int x amount of frequent words needed to be passed.
    * @return ArrayList of x most frequent words
    */
   public ArrayList <Word> frequentWords(int x)
   {
       ArrayList <Word> temp = new ArrayList <Word>();
       frequentSort(0, words.size() - 1);
       if (x > words.size())
           throw new IllegalArgumentException ("Number passed contains more words than in text.");
       else
       {
           for (int i = 0; i < x; i++)
               temp.add (words.get(i));
           return temp;
       }
   }
   
   /**
    * Uses System.out.println to print a list of words.
    * 
    * @param w the ArrayList to be printed
    */
   public void printList (ArrayList <Word> w)
   {
       if (w == null)
           System.out.println ("Error. Please try again.");
       else
       {
           printHeader();
           for (int i = 1; i < w.size() + 1; i++)
           {
               System.out.print (String.format("%-7d ", i));
               w.get(i - 1).print();
           }
       }
   }
   
   /**
    * Uses System.out.println to print a list of the instance variable words.
    * Overloads the method using a default printing the instance variable.
    */
   public void printList()
   {
       printList(words);
   }
   
   /**
    * Retrieves the instance variable words.
    * 
    * @return ArrayList <Word> words
    */
   public ArrayList <Word> getMainList()
   {
       return words;
   }
   
   /**
    * Gets total frequency of the x most frequent words.
    * 
    * @param x the limit of words to be analyzed
    */
   public int getFrequencies (int x)
   {
       frequentSort (0, words.size() - 1);
       int sum = 0;
       for (int i = 0; i < x; i++)
       {
           sum += words.get(i).getFrequency();
       }
       return sum;
   }
   
   /**
    * Prints the percentage and ratio of the top x most frequent words.
    * 
    * @param x the limit of words to be analyzed
    */
   public void printRatio (int x)
   {
       double percentage = (double)(getFrequencies (x)) / (lengthOfBook());
       System.out.println ("\n\n\nThe percentage of the top " + x + " most frequent words is " +
    		   				Math.floor(percentage * 10000)/100 + "%");
       System.out.println ("The fractional expression is shown here: " + getFrequencies (x) +
    		   				" / " + lengthOfBook() );
   }
   
   /**
    * Prints the user interface for MobyDickTester.
    */
   public void printMenu()
   {
       System.out.println("Please enter a number to carry out an action:\n ");
       System.out.println("\t 1 - Prints all Word data in lexicographical order \n" +
                          "\t 2 - Prints all Word data in order of frequency\n" +
                          "\t 3 - Prints total number of words and unique words \n" +
                          "\t 4 - Prints the x most frequent words, with percentage and ratio \n" +
                          "\t 5 - Searches for a given word and prints its frequency \n" +
                          "\t 6 - Return to menu of available texts\n" +
                          "\t 7 - Quit \n");
   }
   
   /**
    * Explains what is in which column in the output.
    */
   public void printHeader()
   {
       System.out.println ("Number  Word                       Frequency");
   }
       
   /**
    * Makes option 1 (lexicographical sort) look better in code.
    */
   public void alphaPrint()
   {
       alphaSort (0, words.size() - 1);
       printList();
       System.out.println("\n");
   }
   
   /**
    * Makes option 2 (frequency sort) look better in code.
    */
   public void frequentPrint()
   {
       frequentSort (0, words.size() - 1);
       printList();
       System.out.println("\n");
   }
   
   /**
    * Makes option 3 (printing number of total words and total unique words) look better in code.
    */
   public void printTotalWordData()
   {
       System.out.println ("Total number of words in this text " + lengthOfBook());
       System.out.println ("Total number of unique words in this text: " + uniqueWords() + "\n\n");
   }
   
   /**
    * Makes option 4 (analyzing the x most frequent words) look better in code.
    */
   public void runFrequentWordBreakdown()
   {
	   Scanner in = new Scanner(System.in);
       System.out.print ("Please select the number of most frequent words to analyze.\n\t");
       int x = in.nextInt();
       clrscr();
       System.out.println ("The top " + x + " most frequent words are:\n\n");
       printList (frequentWords (x));
       printRatio (x);
       System.out.println("\n");
   }
   
   /**
    * Makes option 5 (searching for a word) look better in code.
    */
   public void searchWord()
   {
       Scanner in = new Scanner(System.in);
       alphaSort (0, words.size() - 1);
       System.out.print ("\nPlease select the word you would like to analyze.\n\t");
       String str = in.next().toLowerCase();
       clrscr();
       int index = binarySearch (str, 0, words.size() - 1);
       if (index != -1) 
           System.out.println ("The word you requested, \"" + str + "\", is displayed below: \n\n" + 
        		   				words.get(index) + "\n\n");
       else 
           System.out.println ("The word you requested, \"" + str + "\", is not found.\n\n");
   }
   
   /**
    * Clears the terminal window in BlueJ.
    */
   public void clrscr()
   {
       System.out.print('\f');
   }
   
   /**
    * The code for the user interface of HurricaneOrganizerArrayList.
    * 
    * @throws IOException
    */
   public void interactWithUser() throws IOException
   {
       Scanner in = new Scanner(System.in);
       printMenu();
       int choice = in.nextInt();
       in.nextLine();
       clrscr();
       numberInput (choice);
   }
   
   /**
    * Implemented in interactWithUser(). Manages the loop with the inputed numbers.
    * 
    * @param choice the choice the user inputed
    *                 
    * @throws IOException
    */
   public void numberInput (int choice) throws IOException
   {
       if(choice == 1)
           alphaPrint();
       else if (choice == 2)
           frequentPrint();
       else if (choice == 3)
           printTotalWordData();
       else if(choice == 4)
           runFrequentWordBreakdown();
       else if(choice == 5)
           searchWord();
       else if(choice == 6)
           runProgram (getFilename(), this, System.currentTimeMillis());
       else if (choice == 7)
           System.exit(0);
       else System.out.println ("Index out of bounds.");
   }
   
   /**
    * Prints the initial menu and gets a user-inputed filename.
    * 
    * @return String filename
    */
   public String getFilename()
   {
       Scanner in = new Scanner(System.in);
       
       System.out.print ("The text files we have in our directory are:\n\t");
       System.out.print ("MobyDick         {written by Herman Melville}\n\t");
       System.out.print ("Frankenstein     {written by Mary Shelley}\n\t");
       System.out.print ("AnimalFarm       {written by George Orwell}\n\t");
       System.out.print ("Constitution     {written by the United States of America}\n\t");
       System.out.print ("GreenEggsAndHam  {written by Doctor Seuss}\n\t");
       System.out.print ("BhagavadGita     {written by Lord Ganesha}\n\n");
       
       System.out.print ("Please write the name of a text file to analyze, or \"Quit\" to quit.\n\n");
       System.out.print ("\t*Disclaimer: ");
       System.out.print ("Please type the name exactly as it is written in our directory.\n\n");
       String filename = in.next();
       
       clrscr();
       
       return filename;
   }
   
   /**
    * Runs the part with the filename already inputed.
    * 
    * @param filename name of file inputed by user
    * @param person WordAnalysis running person
    * @param time a param needed to calculate the amount of time taken to read file
    * 
    * @throws IOException
    */
   public void runProgram (String filename, WordAnalysis person, long time) throws IOException
   {
       if (filename.equalsIgnoreCase("quit")) System.exit(0);
       else
       {
           person = new WordAnalysis (filename + ".txt");
           time = System.currentTimeMillis() - time;
           System.out.println ("The text file you are analyzing is called \"" +
                               filename + "\".\n\nInteresting Fact: It took this program " +
                               "approximately " + time +
                               " milliseconds to read in \"" + filename + "\".\n");
           while (true)
               person.interactWithUser();
       }
   }
   
   /**
    * Runs WordAnalysis methods with text files available.
    * 
    * @throws IOException
    */
   public static void main (String [] args) throws IOException
   {
        System.out.print('\f');
        WordAnalysis nate = new WordAnalysis();
        String filename = nate.getFilename();
        long time = System.currentTimeMillis();
        nate.runProgram (filename, nate, time);
   }
}
