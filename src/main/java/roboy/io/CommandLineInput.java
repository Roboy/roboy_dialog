package roboy.io;

import roboy.linguistics.Linguistics;
import roboy.util.Maps;

import java.util.Scanner;

/**
 * Uses the command line as input device.
 */
public class CommandLineInput implements InputDevice{
	
	private Scanner sc = new Scanner(System.in);

	@Override
	public Input listen() {
		System.out.print("[You]:   ");
		String input = sc.nextLine();
		//if ( input.contains("roboy") ){
		//	return new Input(input, Maps.stringObjectMap(Linguistics.ROBOYDETECTED, true));
		//}
		if (input.contains("roboy")) {
            return new Input(input);
        }
		return new Input(input);
	}

	@Override
	protected void finalize(){
		sc.close();
	}
}
