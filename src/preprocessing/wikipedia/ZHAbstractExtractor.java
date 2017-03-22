package preprocessing.wikipedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import preprocessing.util.ChineseUtil;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ZHAbstractExtractor extends Extractor {

	public ZHAbstractExtractor() {
		super();
	}

	public ZHAbstractExtractor(String xmlName, String outputFile) {
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

					String definition = null;
					String wikitext = page.getWikiText();
					if (!(wikitext == null || wikitext == ""))
						definition = getFirstParagraph(title, wikitext);

					line.append((definition == null) ? "" : ChineseUtil
							.translate(page.getPlainText(definition)));
					write(line.toString().replaceAll("\n", "").replaceAll("\r",
					""));
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String getFirstParagraph(String title, String wikiText) {
		Pattern pattern = Pattern.compile("'''(.*?)'''");
		String[] texts = wikiText.split("\n");
		for (int i = 0; i < texts.length; i++) {
			Matcher matcher = pattern.matcher(texts[i]);
			while (matcher.find()) {
				String s = matcher.group(1);
				if (s.contains(title))
					return texts[i];
			}
		}
		return null;
	}
}
