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
 * Tests code using the various testers supplied by Mr. Page
 * and my custom tester.
 * @author Nathan Dalal
 */
public class CompilerTester
{
    public static final String PASCAL_DIR = "../pascal_code/";
    public static final String MIPS_DIR = "../mips_code/";

    public static final String PASCAL_FILE_EXT = ".pas";
    public static final String MIPS_FILE_EXT = ".asm";

    /**
     * Parses pascal input from all test files.
     * Then compiles these test files into MIPS code.
     * @param args thrown from the command line
     * @throws Exception from file I/O or parsing
     */
    public static void main(String[] args) throws Exception
    {
        File[] files = new File(PASCAL_DIR).listFiles();
        List<String> tests = new ArrayList<String>();
        for (File file : files)
        {
            String name = file.getName();
            if (file.isFile() && name.endsWith(PASCAL_FILE_EXT))
            { 
                tests.add(name.substring(0, 
                    name.length() - PASCAL_FILE_EXT.length()));
            }
        }

        for(int i = 0; i < tests.size(); i++)
        {
            String pascalFilepath = PASCAL_DIR + tests.get(i) + PASCAL_FILE_EXT;
            String mipsFilepath = MIPS_DIR + tests.get(i) + MIPS_FILE_EXT;

            System.out.println("\nINPUT PASCAL CODE (" + tests.get(i) + "):\n");
            printFile(pascalFilepath);

            Parser.compile(pascalFilepath, mipsFilepath);
            System.out.println("\n\nCode " + pascalFilepath + 
                                " successfully compiled, stored in " +
                                mipsFilepath + " file.\n");

            System.out.println("\nOUTPUT MIPS CODE (" + tests.get(i) + "):\n");
            printFile(mipsFilepath);
        }
        System.out.println("\n\nAll code successfully compiled.\n");
        Readln.in.close();
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