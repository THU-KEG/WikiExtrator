package preprocessing.wikipedia;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;


import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class FRRedirectExtractor extends Extractor {

	HashMap<String, HashSet<String>> redirects;
	
	public FRRedirectExtractor() {
		super();
	}

	public FRRedirectExtractor(String xmlName, String outputFile) {
		super();
		setParser(xmlName);
		setWriter(outputFile);
		redirects = new HashMap<String, HashSet<String>>();
	}

	@Override
	public void extract() {
		try {
			parser.setPageCallback(new PageCallbackHandler() {
				@Override
				public void process(WikiPage page) {
					String title = page.getTitle();

					// is Template
					// if (!isInfobox(title))
					// return;

					if (!validTitle(title))
						return;


					String redirect = page.getRedirectPage();
					if (StringUtils.isEmpty(redirect))
						return;

					if (title.equals(redirect))
						return;

					if(redirects.containsKey(redirect)) {
						redirects.get(redirect).add(title);
						
					}
					else{
						redirects.put(redirect, new HashSet<String>(Arrays.asList(title)));
					}
					//write(title + "\t\t" + redirect);
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void writeToFile() {
		for(Entry<String, HashSet<String>> redirect_entry : redirects.entrySet()) {
			String line = redirect_entry.getKey() + "\t\t";
			for(String redirect_value : redirect_entry.getValue()) {
				line += redirect_value.trim().replaceAll("\n", "") + ", ";
			}
			write(line);
		}
	}
}
