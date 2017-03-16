package roboy.memory;

import java.io.IOException;
import roboy.util.Concept;
import roboy.util.Relation;

public interface Memory {
	public boolean save(Concept object) throws InterruptedException, IOException;
	public boolean retrieve(Concept object) throws InterruptedException, IOException;
	public boolean save(Relation triple) throws InterruptedException, IOException;
	public boolean retrieve(Relation triple) throws InterruptedException, IOException;
}