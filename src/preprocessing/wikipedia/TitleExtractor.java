package preprocessing.wikipedia;

import edu.jhu.nlp.wikipedia.*;

public class TitleExtractor extends Extractor {

	private WikiXMLParser parser;

	public TitleExtractor() {
		super();
	}

	public TitleExtractor(String xmlName, String outputFile) {
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
					write(line.toString().replaceAll("\n", ""));
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setParser(String xmlName) {
		this.parser = WikiXMLParserFactory.getSAXParser(xmlName);
	}
}
