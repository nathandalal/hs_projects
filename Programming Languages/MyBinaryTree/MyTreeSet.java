import java.util.*;
public class MyTreeSet<E>
{
    private TreeNode root;
    private int size;
    private TreeDisplay display;

    public MyTreeSet()
    {
        root = null;
        size = 0;
        display = new TreeDisplay();

        //wait 1 millisecond when visiting a node
        display.setDelay(5); // originally 1
    }

    public int size()
    {
        return size;
    }

    public boolean contains(Object obj)
    {
        //return BSTUtilities.contains(root, (Comparable)obj, display);
        if(root == null) return false;
        Comparable x = (Comparable)(obj);        
        ArrayDeque<TreeNode> temp = new ArrayDeque<TreeNode>();
        temp.add(root);
        while(temp.isEmpty() ==false)
        {
            TreeNode n = temp.poll();
            if(n != null)
            {
                if(x.compareTo(n.getValue()) == 0)
                    return true;
                else
                {
                    if(n.getRight() != null) temp.add(n.getRight());
                    if(n.getLeft() != null)  temp.add(n.getLeft());
                }
            }
        }
        return false;
    }

    // if obj is not present in this set, adds obj and
    // returns true; otherwise returns false
    public boolean add(E obj)
    {
        Comparable x = (Comparable)(obj);
        if(contains(obj))
            return false;
        root  = BSTUtilities.insert(root, x, display);
        size++;
        display.displayTree(root);
        return true;
    }

    // if obj is present in this set, removes obj and
    // returns true; otherwise returns false}
    public boolean remove(Object obj)
    {
        Comparable x = (Comparable)(obj);
        if(!contains(obj))
            return false;
        root = BSTUtilities.delete(root, x, display);
        size--;
        display.displayTree(root);
        return true;
    }
    public String toString()
    {
        return toString(root);
    }

    private String toString(TreeNode t)
    {
        if (t == null)
            return "";
        return  toString(t.getLeft()) +  t.getValue() + ", " + toString(t.getRight());
    }
}