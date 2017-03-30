package roboy.io;

import java.io.IOException;

import roboy.linguistics.Linguistics;
import roboy.util.Maps;

public class RoboyNameDetectionInput implements InputDevice{

	@Override
	public Input listen() throws InterruptedException, IOException {
		if(Math.random()<0.2){ // TODO: Put the actual detection instead of the random stuff in here
			return new Input(null,Maps.stringObjectMap(Linguistics.ROBOYDETECTED,true));
		} else {
			return new Input(null);
		}
	}

}
