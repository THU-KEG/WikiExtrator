package preprocessing.wikipedia;

import org.apache.commons.lang.StringUtils;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.language.Language;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

/**
 * 
 * @author Tony Extractor for extracting cross-lingual links from Wikipedia
 */
public class CategoryLanLinkExtractor extends Extractor {

	private String sourceLanguage;
	private String targetLanguage;

	public CategoryLanLinkExtractor() {
		super();
	}

	public CategoryLanLinkExtractor(String xmlName, String outputFile,
			String sourceLanguage, String targetLanguage) {
		super();
		setParser(xmlName);
		setWriter(outputFile);
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
	}

	@Override
	public void extract() {
		try {
			parser.setPageCallback(new PageCallbackHandler() {

				@Override
				public void process(WikiPage page) {
					if(page.isRedirect())
						return;
					
					String title = page.getTitle();
					if (!title.startsWith("Category:"))
						return;

					String translation = page
							.getTranslatedTitle(targetLanguage);
					if (StringUtils.isEmpty(translation))
						return;
					if (!translation.startsWith("Category:"))
						return;

					// strip the head Category:
					title = title.substring(9);
					translation = translation.substring(9);

					if (sourceLanguage.equals(Language.CHINESE))
						title = ChineseUtil.translate(title);
					if (targetLanguage.equals(Language.CHINESE))
						translation = ChineseUtil.translate(translation);

					write(title.trim() + "\t\t" + translation.trim());
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
