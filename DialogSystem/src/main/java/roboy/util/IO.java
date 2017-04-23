package roboy.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for IO related tasks.
 */
public class IO {
	
	public static String readFile(String file){
		try{
			return new String(Files.readAllBytes(Paths.get(file)));
		} catch(IOException e){
			return "";
		}
	}
	
	public static String readFile(File file){
		try{
			return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		} catch(IOException e){
			return "";
		}
	}
	
	public static List<String> readLines(String file){
		try{
			return Files.readAllLines(Paths.get(file));
		} catch(IOException e){
			return new ArrayList<>();
		}
	}
	
	public static List<String> readLines(File file){
		try{
			return Files.readAllLines(Paths.get(file.getAbsolutePath()));
		} catch(IOException e){
			return new ArrayList<>();
		}
	}
}
