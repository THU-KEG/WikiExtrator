package preprocessing.util;

import java.io.*;
import java.util.*;

/**
 * combine multiple files into one
 * 
 * @author tony
 * 
 */
public class FileCombiner {
	private List<String> subfiles;
	private String output;

	public FileCombiner(List<String> _subfiles, String _output) {
		subfiles = _subfiles;
		output = _output;
	}

	public void combine() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		for (int i = 0; i < subfiles.size(); i++) {
			BufferedReader reader = new BufferedReader(new FileReader(subfiles
					.get(i)));
			String line = null;
			while (null != (line = reader.readLine())) {
				writer.write(line);
				writer.newLine();
			}
			reader.close();
		}
		writer.close();
	}

	public void combineAndDelete() throws IOException {
		System.out.println(">>Combine .tmp files...");
		combine();
		System.out.println(">>Delete .tmp files...");
		for (String filename : subfiles) {
			File f = new File(filename);
			f.delete();
		}
	}
}
