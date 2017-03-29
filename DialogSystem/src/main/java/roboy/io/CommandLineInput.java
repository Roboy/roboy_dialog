package roboy.io;

import java.util.Scanner;

public class CommandLineInput implements InputDevice{
	
	private Scanner sc = new Scanner(System.in);

	@Override
	public Input listen() {
		String input = sc.nextLine();
		return new Input(input);
	}

	@Override
	protected void finalize(){
		sc.close();
	}
}
