package preprocessing.wikipedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class CategoryParentExtractor extends Extractor {

	public CategoryParentExtractor() {
		super();
	}

	public CategoryParentExtractor(String xmlName, String outputFile) {
		super();
		setParser(xmlName);
		setWriter(outputFile);
	}

	@Override
	public void extract() {
		// TODO Auto-generated method stub
		try {
			parser.setPageCallback(new PageCallbackHandler() {

				@Override
				public void process(WikiPage page) {
					if (page.isRedirect())
						return;

					String title = page.getTitle().replaceAll("\n", "").trim();
					if (!(title.startsWith("Category:")||title.startsWith("分类:")))
						return;

					StringBuilder line = new StringBuilder();
					line.append(title.substring(9));
					line.append("\t\t");

					String wikitext = page.getPlainText(page.getWikiText());

					Pattern pattern = Pattern
							.compile("\\[\\[(Category:|分类:)(.*?)\\]\\]");
					Matcher matcher = pattern.matcher(wikitext);
					while (matcher.find()) {
						String[] tmp = matcher.group(2).split("\\|");
						line.append(tmp[0].trim() + ";");
					}

					if (line.charAt(line.length() - 1) == ';')
						line.deleteCharAt(line.length() - 1);

					write(ChineseUtil.translate(line.toString()));
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
