package preprocessing.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Index {

	private HashMap<String, Integer> index;

	public Index(String input) throws IOException {
		index = new HashMap<String, Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = null;
		while (null != (line = reader.readLine())) {
			String[] vals = line.split("\t\t");
			if (vals.length != 2)
				continue;
			int id = Integer.parseInt(vals[0]);
			String title = vals[1];
			index.put(title, id);
		}

	}

	public int getID(String title) {
		Integer id = index.get(title);
		if (id != null)
			return id;
		else
			return -1;
	}

	public HashMap<String, Integer> getIndex() {
		return index;
	}

}
