package preprocessing.wikipedia;

import preprocessing.util.ChineseUtil;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ZHTextExtractor extends Extractor {
	public ZHTextExtractor() {
		super();
	}

	public ZHTextExtractor(String xmlName, String outputFile) {
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

					title = ChineseUtil.translate(title);

					line.append(title);
					line.append("\t\t");

					String wikitext = page.getPlainText(page.getWikiText());

					line.append((wikitext == null) ? "" : ChineseUtil
							.translate(wikitext));
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
