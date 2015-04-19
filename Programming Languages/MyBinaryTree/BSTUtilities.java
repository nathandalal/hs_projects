/**
 * a collection of static methods for operating on binary search trees
 * 
 * @author Nathan Dalal
 * @version 11/2/13
 */
public abstract class BSTUtilities
{    
    /**
     * precondition:  t is a binary search tree in ascending order
     * postcondition: returns true if t contains the value x;
     *                otherwise, returns false
     *                
     * @param t the TreeNode to check
     * @param x the x to compare with the values inside t
     * @param display that displays the tree
     * 
     * @return true if value of x is in t, false otherwise
    */
    public static boolean contains(TreeNode t, Comparable x, TreeDisplay display)
    {
        if (t == null) return false;
        if (x.compareTo (t.getValue()) == 0) return true;
        if (x.compareTo (t.getValue()) > 0) 
            return contains (t.getRight(), x, display);
        return contains (t.getLeft(), x, display);
    }
    
    /**
     * precondition:  t is a binary search tree in ascending order
     * postcondition: if t is empty, returns a new tree containing x;
     *                otherwise, returns t, with x having been inserted
     *                at the appropriate position to maintain the binary
     *                search tree property; x is ignored if it is a
     *                duplicate of an element already in t; only one new
     *                TreeNode is created in the course of the traversal
     * 
     * @param t the tree to insert x into
     * @param x the value to be inserted into t
     * @param display that displays the tree
     * 
     * @return TreeNode to be inserted (recursive)
     */
    public static TreeNode insert(TreeNode t, Comparable x, TreeDisplay display)
    {
        if (t == null)
            return new TreeNode (x);
        if (x.compareTo (t.getValue()) < 0)
            t.setLeft (insert (t.getLeft(), x, display));
        if (x.compareTo (t.getValue()) > 0)
            t.setRight (insert (t.getRight(), x, display));
        return t;
    }

    /**
     * precondition:  t is a binary search tree in ascending order
     * postcondition: returns a pointer to a binary search tree,
     *                in which the value at node t has been deleted
     *                (and no new TreeNodes have been created)
     *                
     * @param t the TreeNode to delete
     * @param display that displays the tree
     * 
     * @param edited TreeNode after deletion has occured
     */
    private static TreeNode deleteNode(TreeNode t, TreeDisplay display)
    {
        //leaf node, eliminate it
        if (t.getLeft() == null && t.getRight() == null) return null;
        
        //single child, slice it out
        if (t.getRight() == null) return t.getLeft();
        if (t.getLeft() == null) return t.getRight();
        
        //two children, swap with successor, splice it out
        TreeNode parent = t;
        TreeNode child = t.getRight();
        
        boolean childHasMoved = false;
        while (child.getLeft() != null)
        {
            parent = child;
            child = child.getLeft();
            childHasMoved = true;
        }
        
        Comparable temp = (Comparable)(t.getValue());
        t.setValue (child.getValue());
        child.setValue (temp);
        
        if (childHasMoved) parent.setLeft (child.getRight());
        else parent.setRight (child.getRight());
        return t;
    }
    
    /**
     * precondition:  t is a binary search tree in ascending order
     * postcondition: returns a pointer to a binary search tree,
     *                in which the value x has been deleted (if present)
     *                (and no new TreeNodes have been created)
     *                
     * @param t the TreeNode to search and delete from
     * @param x the value to be deleted
     * @param display that displays the tree
     * 
     * @return new TreeNode with x deleted
     */
    public static TreeNode delete(TreeNode t, Comparable x, TreeDisplay display)
    {
        if (t != null)
        {
            if (x.compareTo (t.getValue()) == 0)
                return deleteNode (t, display);
            if (x.compareTo (t.getValue()) < 0)
                t.setLeft (delete (t.getLeft(), x, display));
            if (x.compareTo (t.getValue()) > 0)
                t.setRight (delete (t.getRight(), x, display));
        }
        return t;
    }
}