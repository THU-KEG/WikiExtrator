package preprocessing.wikipedia;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class TemplateExtractor extends Extractor {

	public TemplateExtractor() {
		super();
	}

	public TemplateExtractor(String xmlName, String outputFile) {
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

					String title = ChineseUtil.translate(page.getTitle());

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
		if (s.startsWith("模板:"))
			return true;
		if (!s.startsWith("template:"))
			return false;
		s = s.substring(9);
		return s.startsWith("infobox") || ChineseUtil.isChinese(s.charAt(0));
	}
}
