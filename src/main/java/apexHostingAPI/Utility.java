package apexHostingAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utility {
	static ArrayList<String> loadFile(String fileName) {
		File file = new File("src/main/resources/" + fileName);
		
		ArrayList<String> data = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(file);
			
			while (scanner.hasNext()) {
				data.add(scanner.nextLine());
			}
			
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	static void print(String text) {
		System.out.println("[APEX] " + text);
	}
}
