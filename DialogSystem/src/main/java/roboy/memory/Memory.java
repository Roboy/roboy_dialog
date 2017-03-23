package roboy.memory;

import java.io.IOException;
import roboy.util.Concept;
import roboy.util.Relation;

public interface Memory<T> {
	public boolean save(T object) throws InterruptedException, IOException;
	public T retrieve(T object) throws InterruptedException, IOException;
}