package preprocessing.wikipedia;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class TextExtractor extends Extractor {
	public TextExtractor() {
		super();
	}

	public TextExtractor(String xmlName, String outputFile) {
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
					StringBuilder line = new StringBuilder();
					String title = page.getTitle().replaceAll("\n", "").trim();
					if (!validTitle(title))
						return;
					if (page.isRedirect())
						return;

					line.append(title);
					line.append("\t\t");

					String wikitext = page.getPlainText(page.getWikiText());

					line.append((wikitext == null) ? "" : wikitext);
					write(line.toString().replaceAll("\n", "").replaceAll("\r",
							"").trim());
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
