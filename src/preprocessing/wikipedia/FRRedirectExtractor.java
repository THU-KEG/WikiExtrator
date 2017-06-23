package preprocessing.wikipedia;

import org.apache.commons.lang.StringUtils;


import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class FRRedirectExtractor extends Extractor {

	public FRRedirectExtractor() {
		super();
	}

	public FRRedirectExtractor(String xmlName, String outputFile) {
		super();
		setParser(xmlName);
		setWriter(outputFile);
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

//					System.out.println("r:"+title + "\t\t" + redirect);
					write(title + "\t\t" + redirect);
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private boolean isInfobox(String s) {
		s = s.toLowerCase();
		if (!s.startsWith("modèle:"))
			return false;
		s = s.substring(7);
		return s.startsWith("infobox");
	}
}
