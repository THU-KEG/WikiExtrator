package preprocessing.util;

public class InfoUnit {

	private String key;
	private String[] values;

	public InfoUnit(String line) {
		String[] temp = line.split("\t\t");
		if (temp.length < 2)
			return;
		key = temp[0];
		values = temp[1].split(";");
	}

	public String getKey() {
		return key;
	}

	public String[] getValues() {
		return values;
	}

}
