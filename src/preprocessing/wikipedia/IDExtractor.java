package preprocessing.wikipedia;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

//    IDExtractor
   

public class IDExtractor extends Extractor {

	private WikiXMLParser parser;

	public IDExtractor() {
		super();
	}

	public IDExtractor(String xmlName, String outputFile) {
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

					String wikitext = page.getPlainText(page.getID());
//					String wikitext="xxx";
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

	public void setParser(String xmlName) {
		this.parser = WikiXMLParserFactory.getSAXParser(xmlName);
	}
}