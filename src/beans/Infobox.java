package beans;

import java.util.*;

import org.apache.commons.lang.StringUtils;

public class Infobox {
	// Infobox template name
	private String type;
	private List<Pair> pairs;
	private String infoboxText;
	private boolean isUpdated = false;

	public Infobox(String type, List<Pair> pairs) {
		super();
		this.type = type;
		this.pairs = pairs;
	}

	public Infobox(String infoboxText) {
		this.infoboxText = infoboxText;
		this.type = StringUtils.substringBefore(infoboxText, ":::::");
		this.pairs = new ArrayList<Pair>();

		String infoboxPairs = StringUtils.substringAfter(infoboxText, ":::::");
		String[] pairArray = infoboxPairs.split("::::;");
		for (String pair : pairArray) {
			if (!pair.equals("::::="))
				pairs.add(new Pair(pair));
		}
	}

	@Override
	public String toString() {
		if (this.infoboxText != null && !isUpdated)
			return this.infoboxText;
		StringBuilder sb = new StringBuilder();
		sb.append(type + ":::::");
		for (Pair p : pairs) {
			sb.append(p.getAttribute() + "::::=" + p.getValue() + "::::;");
		}
		// remove the last character ::::;
		if (sb.charAt(sb.length() - 1) == ';')
			sb.delete(sb.length() - 5, sb.length());
		return sb.toString();
	}

	public int getInfoboxSize() {
		return pairs.size();
	}

	public HashMap<InfoboxSchema, HashSet<String>> pairsToSchemaMap() {
		HashMap<InfoboxSchema, HashSet<String>> schemaMap = new HashMap<InfoboxSchema, HashSet<String>>();
		for (Pair pair : pairs) {
			String value = null;
			InfoboxSchema schema = new InfoboxSchema(this.type, pair
					.getAttribute());
			HashSet<String> values = schemaMap.get(schema);
			if (values == null) {
				values = new HashSet<String>();
				values.add(pair.getValue());
				schemaMap.put(schema, values);
			} else {
				value = pair.getValue();
				if (value.length() > 0)
					values.add(value);
			}
		}
		return schemaMap;
	}
	
	/**
	 * 删除没用的property，比如image， image_size
	 */
	public void deleteExtraPairs(){
		
		String[] labels = {"image","image_size","caption","- align","image_caption","image_flag","alt"};
		Iterator<Pair> pair = pairs.iterator();
		while (pair.hasNext()) {
		   Pair p = pair.next(); // must be called before you can call p.remove()
		   String a = p.getAttribute().toLowerCase();
		   if (Arrays.binarySearch(labels, a) >= 0) //如果在labels里
			   pair.remove();
		}	
	}
	
	public void trimLabel(){
		for(Pair p:pairs){
			String a = p.getAttribute();
			a = StringUtils.stripStart(a, "-");
			// add other rules
			p.setAttribute(a);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((pairs == null) ? 0 : pairs.hashCode());
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
		Infobox other = (Infobox) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (pairs == null) {
			if (other.pairs != null)
				return false;
		} else if (!pairs.equals(other.pairs))
			return false;
		return true;
	}

	public String getName() {
		return type;
	}

	public void setName(String name) {
		this.isUpdated = true;
		this.type = name;
	}

	public List<Pair> getPairs() {
		return pairs;
	}

	public void setPairs(List<Pair> pairs) {
		this.pairs = pairs;
	}
}
