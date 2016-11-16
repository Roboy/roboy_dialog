package de.roboy.io;

import java.util.Scanner;

public class CommandLineInput implements InputDevice{
	
	private Scanner sc = new Scanner(System.in);

	@Override
	public String listen() {
		String input = sc.nextLine();
		return input;
	}

	@Override
	protected void finalize(){
		sc.close();
	}
}
