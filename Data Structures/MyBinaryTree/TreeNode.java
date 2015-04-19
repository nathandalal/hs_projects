/**
 * TreeNode is a class that manages the data and pointers of the class MyBinaryTree.
 * It contains a link to the left container, a link to the right container, and the value.
 * 
 * Services Provided:
 *      a 3 parameter constructor that sets the containers and the value
 *      a 1 parameter constructor that sets the value
 *      3 methods that get the left container, the right container, and the value
 *      3 methods that set the left container, the right container, and the value
 */
public class TreeNode
{
    private Object value;
    private TreeNode left;
    private TreeNode right;

    /**
     * A 1 parameter constructor for TreeNode that sets the value to a user-given value.
     * Sets the left and right containers to null.
     */
    public TreeNode(Object initValue)
    { 
        this(initValue, null, null);
    }

    /**
     * A 3 parameter constructor for objects of class TreeNode
     * Sets the left container, the right container, and the value.
     * 
     * @param l to be set to the left container
     * @param r to be set to the right container
     * @param v to be set to the value instance variable
     */
    public TreeNode(Object initValue, TreeNode initLeft, TreeNode initRight)
    { 
        value = initValue; 
        left = initLeft; 
        right = initRight; 
    }

    /**
     * Gets value for objects of class TreeNode
     * 
     * @return value
     */
    public Object getValue() 
    {
        return value;
    }
    
    /**
     * Gets the left container for objects of class TreeNode
     * 
     * @return left
     */
    public TreeNode getLeft()
    {
        return left; 
    }
    
    /**
     * Gets the right container for objects of class TreeNode
     * 
     * @return right
     */
    public TreeNode getRight()
    {
        return right;
    }

    /**
	 * Sets value for objects of class TreeNode
	 * 
	 * @param theNewValue to be set to value
	 */
    public void setValue(Object theNewValue) 
    {
        value = theNewValue; 
    }
    
    /**
     * Sets the left container for objects of class TreeNode
     * 
     * @param theNewLeft to be set to left
     */
    public void setLeft(TreeNode theNewLeft) 
    {
        left = theNewLeft;
    }
    
    /**
     * Sets the right container for objects of class TreeNode
     * 
     * @param theNewRight to be set to right
     */
    public void setRight(TreeNode theNewRight) 
    {
        right = theNewRight; 
    }
}