package roboy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extension of ArrayList with possibility to get a random element.
 * @param <T> Class of objects in this list
 */
public class RandomList<T> extends ArrayList<T> {

    /**
     * Creates an empty ArrayList.
     */
    public RandomList() {
    }

    /**
     * Creates a list of objects that allows to select one element at random.
     *
     * To prevent issues with heap pollution, use this constructor to reduce
     * syntactic overhead only: new RandomList("a", "b", "c")
     *
     * @param objects objects to put in the list
     */
    @SafeVarargs
    public RandomList(T... objects) {
        addAll(Arrays.asList(objects));
    }

    /**
     * Creates list of objects that allows to select one element at random.
     * @param objectList list containing the objects to add to this list
     */
    public RandomList(List<T> objectList) {
        addAll(objectList);
    }


    /**
     * Returns a random element from this list.
     * @return random element from this list
     */
    public T getRandomElement() {
        if (size() == 0) {
            return null;
        }
        int id = (int) (Math.random() * size());
        return get(id);
    }

}
