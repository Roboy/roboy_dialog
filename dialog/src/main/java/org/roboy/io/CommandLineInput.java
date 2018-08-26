package org.roboy.io;

import org.roboy.linguistics.sentenceanalysis.Interpretation;

import java.util.Scanner;

/**
 * Uses the command line as input device.
 */
public class CommandLineInput implements InputDevice {
	
	private Scanner sc = new Scanner(System.in);

	@Override
	public Input listen() {
		System.out.print("[You]:   ");
		String input = sc.nextLine();

		if (input.contains("roboy")){
			Interpretation interpretation = new Interpretation();
			interpretation.setRoboy(true);
            return new Input(input, interpretation);
		}
		return new Input(input);
	}

	@Override
	protected void finalize(){
		sc.close();
	}
}
