package preprocessing.wikipedia;

import java.util.Vector;

import edu.jhu.nlp.language.Language;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class CategoryExtractor extends Extractor {
	
	private String language = Language.ENGLISH;
	
	public CategoryExtractor() {
		super();
	}

	public CategoryExtractor(String xmlName, String outputFile, String language) {
		super();
		this.language = language;
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

					// categories
					Vector<String> categories = page.getCategories(language);
					String categoryString = "";
					if (categories != null) {
						for (int i = 0; i < categories.size(); i++) {
							categoryString = categoryString
									+ categories.get(i).trim() + ";";
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
