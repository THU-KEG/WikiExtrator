package basic;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;

public class FileManipulator {
	public final static int Only_One_Column_Flag = -1;
	public final static int First_Column_Num = 0;
	public final static int Second_Column_Num = 1;

	// column=-1 means load a file with only one column
	public static Set<String> loadOneColum(String fileName, String separator,
			int column) throws IOException {
		Set<String> columnSet = new HashSet<String>();

		// System.out.println("Start loading " + fileName);

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = null;
		if (column == Only_One_Column_Flag)
			while (null != (line = in.readLine()))
				columnSet.add(line.trim());
		else
			while (null != (line = in.readLine())) {
				String[] array = line.trim().split(separator);
				if (array.length > column) {
					String value = array[column].trim();
					if (!StringUtils.isEmpty(value))
						columnSet.add(value);
				}
			}
		in.close();

		// System.out.println("Finish loading " + fileName);

		return columnSet;
	}

	public static Map<String, String> loadOneToOne(String fileName,
			String separator, int keyColumnNum) throws IOException {
		Map<String, String> map = new HashMap<String, String>();

		// System.out.println("Start loading " + fileName);

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = null;
		while (null != (line = in.readLine())) {
			String[] array = line.trim().split(separator);
			if (array.length != 2)
				continue;
			map.put(array[keyColumnNum].trim(), array[1 - keyColumnNum].trim());
		}
		in.close();

		// System.out.println("Finish loading " + fileName);

		return map;
	}

	public static Map<String, Set<String>> loadOneToMany(String fileName,
			String separator1, String separator2) throws IOException {
		Map<String, Set<String>> oneToMany = new HashMap<String, Set<String>>();

		// System.out.println("Start loading " + fileName);

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = null;
		while (null != (line = in.readLine())) {
			String[] keyValues = line.trim().split(separator1);
			if (keyValues.length != 2)
				continue;

			String key = keyValues[0].trim();
			if (StringUtils.isEmpty(key))
				continue;

			String[] many = keyValues[1].split(separator2);
			Set<String> set = new HashSet<String>();
			for (String each : many) {
				each = each.trim();
				if (!StringUtils.isEmpty(each))
					set.add(each);
			}

			if (set.size() == 0)
				continue;
			oneToMany.put(key, set);
		}
		in.close();

		// System.out.println("Finish loading " + fileName);

		return oneToMany;
	}

	public static void outputOneToOne(Map<String, String> map, String fileName,
			String separator, int keyColumnNum) throws IOException {
		String key = null;
		String value = null;

		// System.out.println("Start outputing " + fileName);

		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		for (Map.Entry<String, String> e : map.entrySet()) {
			if (keyColumnNum == FileManipulator.First_Column_Num) {
				key = e.getKey();
				value = e.getValue();
			} else if (keyColumnNum == FileManipulator.Second_Column_Num) {
				key = e.getValue();
				value = e.getKey();
			} else {
				System.out
						.println("Error happens in FileManipulator.outputOneToOne()");
			}
			if (StringUtils.isEmpty(key))
				continue;

			out.write(key + separator + value + "\n");
			out.flush();
		}
		out.close();

		// System.out.println("Finish outputing " + fileName);
	}

	public static void outputOneToMany(Map<String, Set<String>> oneToMany,
			String fileName, String separator1, String separator2)
			throws IOException {
		// System.out.println("Start outputing " + fileName);

		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		for (Map.Entry<String, Set<String>> e : oneToMany.entrySet()) {
			String key = e.getKey();
			if (StringUtils.isEmpty(key))
				continue;

			Set<String> set = e.getValue();
			String tmp = key + separator1;
			for (String s : set)
				tmp += s + separator2;
			if (tmp.endsWith(separator2))
				tmp = tmp.substring(0, tmp.length() - separator2.length());

			out.write(tmp + "\n");
			out.flush();
		}
		out.close();

		// System.out.println("Finish outputing " + fileName);
	}
}
