package beans;

public class Pair {
	private String attribute;
	private String value;

	public Pair(String attribute, String value) {
		super();
		this.attribute = attribute;
		this.value = value;
	}

	public Pair(String pairText) {
		// System.out.println(pairText);
		String[] a = pairText.split("::::=");
		this.attribute = a[0];
		this.value = (a.length == 2) ? a[1] : "";
	}

	@Override
	public String toString() {
		return "Pair [attribute=" + attribute + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
