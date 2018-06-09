package roboy.memory;

import java.io.IOException;

/**
 * The Memory interface contains of methods to save and retrieve information.
 * 
 * @param <T> the type of information stored
 */
public interface Memory<T> {
	/**
	 * Storing the element in the memory.
	 * 
	 * @param object the element to be stored
	 * @return true, if storing was successful
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean save(T object) throws InterruptedException, IOException;
	
//	/**
//	 * Retrieve an element from memory.
//	 *
//	 * @param object a version of the object that lacks information (e.g. it only has the ID)
//	 * @return a list of objects that match the query containing all the required information
//	 * @throws InterruptedException
//	 * @throws IOException
//	 */
//	public List<T> retrieve(T object) throws InterruptedException, IOException;


}