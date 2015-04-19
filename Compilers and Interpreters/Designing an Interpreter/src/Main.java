import ast.*;
import parser.*;
import scanner.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

/**
 * Tests SIMPLE code using written test files in SIMPLE_DIR.
 * @author Nathan Dalal
 */
public class Main
{
    public static final String SIMPLE_DIR = "../SIMPLE_code/";
    public static final String SIMPLE_FILE_EXT = ".txt";

    /**
     * Parses SIMPLE input from all test files.
     * Then interprets the code to the console.
     * 
     * @param args thrown from the command line
     * @throws Exception from file I/O or parsing
     */
    public static void main(String[] args) throws Exception
    {
        File[] files = new File(SIMPLE_DIR).listFiles();
        List<String> tests = new ArrayList<String>();
        for (File file : files)
        {
            String name = file.getName();
            if (file.isFile() && name.endsWith(SIMPLE_FILE_EXT))
            { 
                tests.add(name.substring(0, 
                    name.length() - SIMPLE_FILE_EXT.length()));
            }
        }

        for(int i = 0; i < tests.size(); i++)
        {
            String simpleFilepath = SIMPLE_DIR + tests.get(i) + SIMPLE_FILE_EXT;

            System.out.println("\nINPUT SIMPLE CODE (" + tests.get(i) + "):\n");
            printFile(simpleFilepath);

            System.out.println("\nOUTPUT INTERPRETED ANSWER (" + 
                                tests.get(i) + "):\n");
            Parser.interpret(simpleFilepath);
        }
        System.out.println("\n\nAll code successfully interpreted.\n");
        ReadOption.in.close();
    }

    /**
     * Prints out contents of file to console.
     *
     * @param dir the directory of the file to print
     * @throws IOException from file I/O
     */
    public static void printFile(String dir) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(dir));
        String line = null;
        while ((line = br.readLine()) != null) 
        {
            System.out.println(line);
        }
    }
}