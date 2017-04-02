package roboy.memory;

import java.io.IOException;
import java.util.List;
import roboy.util.Concept;
import roboy.util.Relation;

public interface Memory<T> {
	public boolean save(T object) throws InterruptedException, IOException;
	public List<T> retrieve(T object) throws InterruptedException, IOException;
}