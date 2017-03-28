package roboy.memory;

import java.io.IOException;

import roboy.util.Relation;

public class DBpediaMemory implements Memory<Relation>{

	@Override
	public boolean save(Relation object) throws InterruptedException, IOException {
		return false;
	}

	@Override
	public Relation retrieve(Relation object) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
