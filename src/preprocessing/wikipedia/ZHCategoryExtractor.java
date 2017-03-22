package preprocessing.wikipedia;

import java.util.Vector;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ZHCategoryExtractor extends Extractor {

	public ZHCategoryExtractor() {
		super();
	}

	public ZHCategoryExtractor(String xmlName, String outputFile) {
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

					Vector<String> categories = page.getCategories();
					String categoryString = "";
					if (categories != null) {
						for (int i = 0; i < categories.size(); i++) {
							categoryString += ChineseUtil.translate(categories
									.get(i).trim())
									+ ";";
						}
					}
					line.append(categoryString);
					write(line.toString().replaceAll("\n", ""));
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
