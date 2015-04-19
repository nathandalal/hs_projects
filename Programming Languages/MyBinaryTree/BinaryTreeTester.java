import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
/**
 * Tests the TreeUtil class.
 * 
 * @author Nathan Dalal
 * @version 11/13/13
 */
public class BinaryTreeTester
{
    /**
     * Tests the basic binary tree methods of TreeUtil.
     */
    private void basicTest()
    {
        System.out.print ("\f");
        
        int randomDepth = (int)(Math.random()*5) + 3;
        TreeNode t = TreeUtil.createRandom (randomDepth);
        
        TreeDisplay display = new TreeDisplay();
        
        TreeUtil util = new TreeUtil();
        
        display.setTester (this);
        display.displayTree (t);
        
        if (util.maxDepth (t) == randomDepth) System.out.println ("maxDepth works");
        else System.out.println ("maxDepth no work");
        TreeNode copyTree = t;
        if (util.sameShape (copyTree, t)) System.out.println ("SameShape works");
        else System.out.println ("SameShape no work");
        copyTree = util.copy(t);
        if (util.sameShape (copyTree, t)) System.out.println ("Copy works");
        else System.out.println ("Copy no work");
        System.out.println ("Leftmost node from root is " + util.leftmost (t));
        System.out.println ("Rightmost node from root is " + util.rightmost (t));
        System.out.println ("Total number of nodes is " + util.countNodes (t));
        System.out.println ("Total number of leaves is " + util.countLeaves (t));
        
        System.out.println ("\n\nDoing Pre Order");
        util.preOrder (t, display);
        
        List <String> list = new ArrayList <String> ();
        util.fillList (t, list);
        System.out.println ("\n\nfillList works if below numbers match Pre Order");
        for (int i = 0; i < list.size(); i++)
        {
            System.out.print (list.get(i) + " ");
        }
        
        System.out.println ("\n\nDoing In Order");
        util.inOrder (t, display);
        
        System.out.println ("\n\nDoing Post Order");
        util.postOrder (t, display);
        
        System.out.println ("\n\nCheck with the tree to see that all of your methods work.");
    }
    
    /**
     * Tests the morse code methods of the TreeUtil class.
     */
    private void morseTest()
    {
        System.out.print ("\f");
        
        TreeDisplay display = new TreeDisplay();
        display.setTester (this);
        TreeUtil util = new TreeUtil();
        
        TreeNode decodingTree = TreeUtil.createDecodingTree (display);
        
        System.out.println ("\nLooks like we know our ABC's!\n");
        
        System.out.println ("Let's decode ... --- ...");
        String decoded1 = util.decodeMorse (decodingTree, "... --- ...", display);
        System.out.println ("\nIt should have printed: " + decoded1 + "\n");
        
        System.out.println ("Let's decode .- -- . .-. .. -.- ..- ....");
        String decoded2 = util.decodeMorse (decodingTree, ".- -- . .-. .. -.- ..- ....", display);
        System.out.println ("\nIt should have printed: " + decoded2 + "\n");

        System.out.println ("Let's decode -.-. .... .-. .. ... - -- .- ...");
        String decoded3 = util.decodeMorse (decodingTree, "-.-. .... .-. .. ... - -- .- ...", display);
        System.out.println ("\nIt should have printed: " + decoded3 + "\n");

        System.out.println ("\nCheck with the tree to see that all of your methods work.\n");
        
        userMorse(util, decodingTree, display);
    }
    
    /**
     * Allows the user to test their own code with user-inputted morse code.
     * 
     * @param util the TreeUtil that needs to be passed
     * @param decodingTree the decoding tree in the display window
     * @param display the TreeDisplay object that needs to be passed
     */
    private void userMorse(TreeUtil util, TreeNode decodingTree, TreeDisplay display)
    {
        Scanner in = new Scanner (System.in);
        
        System.out.println ("Enter \"y\" to decode your own morse!" +
                            "\nEnter \"n\" to end the morse code test.");
        String response = in.nextLine();
        
        while (response.equals("y"))
        {
            System.out.println ("\fType valid morse code. The tester will break if code is not valid."
                                + "\nAlso do not add spaces after the final morse code segment." +
                                "\n\nLet's try your own Morse Code!\nPlace your code below:");
            String code = in.nextLine();
            System.out.println ("Let's decode " + code);
            String userDecoded = util.decodeMorse (decodingTree, code, display);
            System.out.println ("\nIt should have printed: " + userDecoded + "\n");
            
            System.out.println ("Enter \"y\" to enter a new morse code.\n" +
                                "Enter \"n\" to end the user-inputted morse code sequence.");
            response = in.nextLine();
        }
        
        System.out.println ("\n\nHope you had fun!\n");
    }
    
    /**
     * Compiles all aspects of the tester in a menu-based system.
     */
    private void test()
    {
        String initial = "";
        int response = 0;
        Scanner in = new Scanner (System.in);
        boolean notValidInput = true;
        boolean alreadyHappened = false;
        
        while (notValidInput)
        {
            try
            {
                if (!alreadyHappened) 
                {
                    System.out.println ("\fWelcome to the Binary Tree Tester.\n" + 
                                        "This tests the implementation of the TreeUtil class.\n\n\n" +
                                        "Enter 1 to test basic tree methods, 2 for morse code " + 
                                        "methods, 3 to quit.");
                }
                else
                {
                    System.out.println ("\fInvalid input, please try again.\n\n" + 
                                            "Enter 1 to test basic tree methods, " + 
                                            "2 for morse code methods, 3 to quit.");
                }
                
                initial = in.nextLine();
                response = Integer.parseInt (initial);
                notValidInput = false;
            }
            catch (IllegalArgumentException i)
            {
                notValidInput = true;
                alreadyHappened = true;
            }
        }
        
        while (true)
        {
            if (response == 1) {basicTest();}
            if (response == 2) {morseTest();}
            if (response == 3) {System.out.print("\fYou win!"); return;}
            if (response != 1 && response != 2 && response != 3)
                System.out.print ("\fInvalid input, please try again.");
            //System.out.print("\n\nEnter 1 to test basic tree methods, " +
              //                     "2 for morse code methods, 3 to quit.\n");
            //response = Integer.parseInt (in.nextLine());
            notValidInput = true;
            alreadyHappened = false;
            while (notValidInput)
            {
                try
                {
                    if (!alreadyHappened)
                        System.out.println ("\n\nEnter 1 to test basic tree methods, " + 
                                            "2 for morse code methods, 3 to quit.");
                    else
                        System.out.println ("\fInvalid input, please try again.\n\n" + 
                                            "Enter 1 to test basic tree methods, " + 
                                            "2 for morse code methods, 3 to quit.");
                    initial = in.nextLine();
                    response = Integer.parseInt (initial);
                    notValidInput = false;
                }
                catch (IllegalArgumentException i)
                {
                    notValidInput = true;
                    alreadyHappened = true;
                }
            }
        }
    }
    
    /**
    * called by the display object to send back the node value
    * when a node is visited
    * 
    * @param value the value passed that is printed out
    */
    public void sendValue(Object value)
    {
        System.out.print(value + " ");
    }
    
    public static void main (String [] args)
    {
        BinaryTreeTester richard = new BinaryTreeTester();
        richard.test();
    }
}
