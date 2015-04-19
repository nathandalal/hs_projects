import myArrayList.MyArrayList;
import myArrayList.MyList;
import myArrayList.MyListIterator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
/*
 * DO NOT MODIFY THIS CLASS.
 * There are no user serviceable parts inside.
 * Altering this class in any way will void your warranty.
 */

public class MyArrayListTesterNew
{
	//determines whether it prints each operation
	private static final boolean DEBUG = true;

	public static void main(String[] args)
	{
		MyList<Integer> your = new MyArrayList<Integer>();
		// special testing
		Class<?> theClass = your.getClass();
		System.out.println(theClass.getName());
		Field[] fields = theClass.getDeclaredFields();
		Field arrayField = null;
		int arrayCount = 0;
		for(Field f : fields) 
		{
		    if(f.getType().isArray()) 
		    {
		        arrayField = f;
		        arrayCount++;
		    }
		}
        if(arrayCount != 1)
            throw new RuntimeException("There should be 1 backing array, found " + arrayCount);
        // check the size of the backing array
        Object[] temp = getArray(your, arrayField);
		// fill the array list and check the new backing array size
        int originalSize = temp.length;
		for(int i = 0; i <= originalSize; i++) 
		    if(!your.add(1))
		        throw new RuntimeException("add returned false!!!!!!");

		temp = getArray(your, arrayField);
		if(temp.length != 2 * originalSize)
		    throw new RuntimeException("backing array size is incorrect \n "
		                        + "add is not O(1)");
		// check to see what happens with null values
		int insertIndex = temp.length/2;
		your.set(insertIndex, null);
		if(your.size() != originalSize + 1) 
		    throw new RuntimeException("size is incorrect after adding null " + your.size());
		int nRemove = your.size();
		// empty the MyArrayList object
		for(int i = 0; i < nRemove; i++) your.remove(0);
		
		List<Integer> real = new ArrayList<Integer>();
		int capacity = 1;
		for (int i = 0; i < 10000; i++)
		{
			debug("your:  " + your);
			debug("real:  " + real);

			if (!your.toString().equals(real.toString()))
				throw new RuntimeException("toString doesn't match");
			/*if (your.getCapacity() != capacity)
					throw new RuntimeException("Capacity is " + your.getCapacity() +
					" and should be " + capacity); */
			if (your.size() != real.size())
				throw new RuntimeException("Size is " + your.size() + " and should be " + real.size());

			for (int index = 0; index < real.size(); index++)
				if (your.get(index) != real.get(index))
					throw new RuntimeException("get(" + index + ") returned " + your.get(index) +
						" and should return " + real.get(index));

			int op = random(4);
			if (op == 0 && real.size() < 15)
			{
				Integer value = new Integer(random(100));
				debug("add(" + value + ")");
				real.add(value);
				if (!your.add(value))
					throw new RuntimeException("add(" + value +
						") returned false and should return true");
				if (real.size() > capacity)
					capacity *= 2;
			}
			else if (op == 1 && real.size() > 0)
			{
				int index = random(real.size());
				debug("remove(" + index + ")");
				Integer realValue = real.remove(index);
				Integer yourValue = (Integer)your.remove(index);
				if (realValue != yourValue)
					throw new RuntimeException("remove(" + index + ") returned " + yourValue +
						" and should return " + realValue);
			}
			else if (op == 2 && real.size() > 0)
			{
				Integer value = new Integer(random(100));
				int index = random(real.size());
				debug("set(" + index + ", " + value + ")");
				Integer realOld = real.set(index, value);
				Integer yourOld = (Integer)your.set(index, value);
				if (realOld != yourOld)
					throw new RuntimeException("set(" + index + ", " + value + ") returned " +
						yourOld + " and should return " + realOld);
			}
			else if(op == 3 && real.size() < 20)
			{
			    // test add at an index
			    int index = random(real.size());
			    Integer value = new Integer(random(101));
			    if(value.compareTo(75) > 0) value = null;
			    debug("add(" + index +", " + value + ")");
			    your.add(index, value);
			    real.add(index, value);
			}
		}
		/*
		 * Iterator tests
		 */
		 /*
        Iterator<Integer> itYour = your.iterator();
        if(itYour != null)
        {
            your = new MyArrayList<Integer>();
            real = new ArrayList<Integer>();
            
            for(int i = 0; i < 15; i++)
            {
                int rand = (int) (Math.random() * 100);
                your.add(rand);
                real.add(rand);
            }
            debug("Iterator test");
            debug("your:  " + your);
            debug("real:  " + real);
            Iterator<Integer> itReal = real.iterator();
            itYour = your.iterator();
            
            // test next and hasNext
            while(itReal.hasNext())
            {
                if(!itYour.hasNext()) throw new RuntimeException("Your iterator should have next");
                if(itReal.next() != itYour.next()) throw new RuntimeException("iterator next does not match");
            }
            if(itYour.hasNext()) throw new RuntimeException("Your iterator still has data!");
            
            // test enhanced for loop
            int index = 0;
            for(Integer anInt : your)
            {
                if(real.get(index) != anInt)  throw new RuntimeException("Iterator returned " + 
                                    anInt + " and should have returned " + real.get(index));
                index++;
            }
            if(index != real.size())
                throw new RuntimeException("Iterator iterated over only " + index + " elements");
            
            // test concurrent modification
            try
            {
                for(Integer anInt : your)
                {
                    your.add(1);
                }
            }
            catch (ConcurrentModificationException e)
            {
                debug("Concurrent Modification thrown - add");
            }
            
            try
            {
                for(Integer anInt : your)
                {
                    your.remove(1);
                }
            }
            catch (ConcurrentModificationException e)
            {
                debug("Concurrent Modification thrown - remove");
            }
            
            // test iterator remove  and remove state machine
            your = new MyArrayList<Integer>();
            real = new ArrayList<Integer>();
            
            for(int i = 0; i < 15; i++)
            {
                int rand = (int) (Math.random() * 100);
                your.add(rand);
                real.add(rand);
            }
            itReal = real.iterator();
            itYour = your.iterator();
            
            debug("Iterator test - remove  and remove state machine");
            debug("your:  " + your);
            debug("real:  " + real);
            
            itReal.next();
            itYour.next();
            itReal.remove();
            itYour.remove();

            debug("Iterator test remove ");
            debug("your:  " + your);
            debug("real:  " + real);
            
            if(!your.toString().equals(real.toString()))
                throw new RuntimeException("Iterator remove failed");

            try
            {
                itYour.remove();
            }
            catch (IllegalStateException e)
            {
                debug("Illegal state exception thrown");
                debug("your:  " + your);
                debug("real:  " + real);                
            }
            
            itReal = real.iterator();
            itYour = your.iterator();
            
            debug("Iterator test remove ");
            while(itReal.hasNext() && itYour.hasNext())
            {
                itReal.next();
                itYour.next();
                if(Math.random() > 0.5)
                {
                    itReal.remove();
                    itYour.remove();                    
                    
                    if(!your.toString().equals(real.toString()))
                    {
                        debug("your:  " + your);
                        debug("real:  " + real);               
                        throw new RuntimeException("remove failed");
                    }
                }
            }
            if(itReal.hasNext() != itYour.hasNext())
                throw new RuntimeException("remove - hasNext returns wrong value \n"
                                    + "real " + itReal.hasNext() + " your "
                                    + itYour.hasNext());
            // test a single node list
            your = new MyArrayList<Integer>();
            real = new ArrayList<Integer>();
            your.add(1);
            real.add(1);
            itYour = your.iterator();
            itReal = real.iterator();
            itYour.next();
            itReal.next();
            itReal.remove();
            itYour.remove();
            if(!your.toString().equals(real.toString()))
            {
                debug("your:  " + your);
                debug("real:  " + real);               
                throw new RuntimeException("remove failed");
            }
            
            debug("Iterator test completed succesfully");
        }
        
        MyListIterator<Integer> itYourList = your.listIterator();
        if(itYourList != null)
        {
            debug("Begin ListIterator test");
            your = new MyArrayList<Integer>();
            real = new ArrayList<Integer>();
            ListIterator<Integer> itRealList = real.listIterator();
            itYourList = your.listIterator();            
            
            for(int i = 0; i < 15; i++)
            {
                int rand = (int) (Math.random() * 100);
                // add using the iterator
                itRealList.add(rand);
                itYourList.add(rand);
            }
            debug("List Iterator test");
            debug("your:  " + your);
            debug("real:  " + real);
            if(!your.toString().equals(real.toString()))
                throw new RuntimeException("List Iterator add failed");
            // add to arraylist tester
            while(itRealList.hasNext() &&itYourList.hasNext())
            {
                if(itRealList.next() != itYourList.next())
                    throw new RuntimeException("after add, traversal failed");
            }
            itRealList.add(555);
            itYourList.add(555);
            debug("List Iterator test - add to end");
            debug("your:  " + your);
            debug("real:  " + real);
            if(!your.toString().equals(real.toString()))
                throw new RuntimeException("List Iterator add failed - end of list");  
            //
            // test set state machine
            itRealList = real.listIterator();
            itYourList = your.listIterator();  
            itYourList.next();
            itRealList.next();
            // should not throw an exception
            try
            {
                itYourList.set(123);
            }
            catch (IllegalStateException e)
            {
                throw new RuntimeException("Illegal state exception was thrown in error");
            }
            itRealList.set(123);
            
            try
            {
                itYourList.add(123);
                itRealList.add(123);
                itYourList.set(999);
                itRealList.add(999);
                itYourList.add(999);
                
            }
            catch (IllegalStateException e)
            {
                debug("Illegal state exception was thrown as expected");
            }
            debug("your:  " + your);
            debug("real:  " + real);
            if(!your.toString().equals(real.toString()))
                throw new RuntimeException("List Iterator set failed");
                         
            // test set
            itRealList = real.listIterator();
            itYourList = your.listIterator();  
            
            while(itRealList.hasNext() && itYourList.hasNext())
            {
                int realValue = itRealList.next();
                int yourValue = itYourList.next();
                if(realValue != yourValue)
                        throw new RuntimeException("next returns wrong value after add " +
                        "your " + yourValue + " real " + realValue);
                itRealList.set(666);
                itYourList.set(666);
                itRealList.add(999);
                itYourList.add(999);
            }
            debug("your:  " + your);
            debug("real:  " + real);
            if(!your.toString().equals(real.toString()))
                throw new RuntimeException("List Iterator set failed");
            if(itYourList.hasNext() != false)
                throw new RuntimeException("List Iterator: Your hasNext returned true");
        }
        */

		System.out.println("You win!");
	}

	private static void debug(String s)
	{
		if (DEBUG)
			System.out.println(s);
	}

	private static int random(int n)
	{
		return (int)(Math.random() * n);
	}
	
	private static Object[] getArray(MyList<Integer> your, Field arrayField)
	{
        Object[] temp = null;
        arrayField.setAccessible(true);
        try
        {
            temp = Object[].class.cast(arrayField.get(your));
            System.out.println("array size is " + temp.length);
        }
        catch (IllegalAccessException e)
        {
            System.out.println("illegal access");
        }
        return temp;
	}
}