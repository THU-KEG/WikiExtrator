package preprocessing.wikipedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class AbstractExtractor extends Extractor {
	public AbstractExtractor() {
		super();
	}

	public AbstractExtractor(String xmlName, String outputFile) {
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

					String definition = null;
					String wikitext = page.getWikiText();
					if (!StringUtils.isEmpty(wikitext))
						definition = getFirstParagraph(title, wikitext);

					line.append((definition == null) ? "" : page
							.getPlainText(definition));
					write(line.toString().replaceAll("\n", "").replaceAll("\r",
							"").trim());
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
				if (s.toLowerCase().contains(title.toLowerCase()))
					return texts[i];
			}
		}
		return null;
	}
}
