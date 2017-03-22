package preprocessing.wikipedia;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import preprocessing.util.ChineseUtil;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

/**
 * 
 * @author Miyayx
 * Entity Linking 的Mention候选集，此程序为从wiki的link里提取mention
 * 不计数，找到一对就记录下来，后面再处理，不然内存不够
 *
 */
public class LinkMentionExtractor extends Extractor {

	public LinkMentionExtractor() {
		super();
	}

	public LinkMentionExtractor(String xmlName,
			String outputFile) {
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
					// StringBuilder line = new StringBuilder();
					String title = page.getTitle().replaceAll("\n", "").trim();
					if (!validTitle(title))
						return;
					if (page.isRedirect())
						return;

					String wikitext = page.getPlainText(page.getWikiText());
					wikitext = wikitext.replaceAll("\\[\\[[^|]*?\\]\\]", "");
					Matcher m = Pattern.compile("\\[\\[(.*?)\\|(.*?)\\]\\]")
							.matcher(wikitext);
					while (m.find()) {
						String article = ChineseUtil.translate(m.group(1));
						article = ChineseUtil.translate(article);
						if(article.contains("File:"))
							continue;

						String linkText = ChineseUtil.translate(m.group(2));
						Matcher mm = Pattern.compile("\\'\\'(.*?)\\'\\'")
								.matcher(linkText);
						while (mm.find()) {
							linkText = mm.group(1).replace("'","");
						}
						//System.out.println(article+ "==>" + linkText);
						write(article+"\t\t"+linkText);
					}
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
