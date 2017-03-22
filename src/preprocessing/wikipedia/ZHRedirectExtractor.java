package preprocessing.wikipedia;

import org.apache.commons.lang.StringUtils;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ZHRedirectExtractor extends Extractor {

	public ZHRedirectExtractor() {
		super();
	}

	public ZHRedirectExtractor(String xmlName, String outputFile) {
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
					String title = ChineseUtil.translate(page.getTitle());

					// is Template
					// if (!isInfobox(title))
					// return;

					if (!validTitle(title))
						return;

					title = ChineseUtil.translate(title);

					String redirect = page.getRedirectPage();
					if (StringUtils.isEmpty(redirect))
						return;

					redirect = ChineseUtil.translate(redirect);
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
		if (s.startsWith("模板:"))
			return true;
		if (!s.startsWith("template:"))
			return false;
		s = s.substring(9);
		return s.startsWith("infobox") || ChineseUtil.isChinese(s.charAt(0));
	}
}
