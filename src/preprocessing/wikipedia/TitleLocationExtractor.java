package preprocessing.wikipedia;

import org.apache.commons.lang.StringUtils;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class TitleLocationExtractor extends Extractor {
	private WikiXMLParser parser;
	private String xmlName;

	public TitleLocationExtractor() {
		super();
	}

	public TitleLocationExtractor(String xmlName, String outputFile) {
		super();
		this.xmlName = xmlName;
		setParser(xmlName);
		setWriter(outputFile);
	}

	@Override
	public void extract() {
		try {
			parser.setPageCallback(new PageCallbackHandler() {
				@Override
				public void process(WikiPage page) {
					String title = page.getTitle().trim();
					// if (!validTitle(title))
					// return;
					// if (page.isRedirect())
					// return;

					String num = StringUtils.substringAfterLast(xmlName,
							"articles");
					num = StringUtils.substringBefore(num, ".xml");

					write(title + "\t\t" + num);
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
