package preprocessing.wikipedia;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class FRTemplateExtractor extends Extractor {

	public FRTemplateExtractor() {
		super();
	}

	public FRTemplateExtractor(String xmlName, String outputFile) {
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
					// if (page.isRedirect())
					// return;

					String title = page.getTitle();

					if (!isInfobox(title))
						return;

					write(title);

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
