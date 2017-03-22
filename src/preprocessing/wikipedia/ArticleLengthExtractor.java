package preprocessing.wikipedia;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ArticleLengthExtractor extends Extractor {

	public ArticleLengthExtractor() {
		super();
	}

	public ArticleLengthExtractor(String xmlName, String outputFile) {
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
					
					String content = page.getText();
					String[] contentList;
					int len;
					if (content != null) {
						contentList = content.split(" ");
						len = contentList.length;
					} else {
						len = 0;
					}
					line.append(len);

					write(line.toString().replaceAll("\n", ""));
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
