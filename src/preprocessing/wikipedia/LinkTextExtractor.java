package preprocessing.wikipedia;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class LinkTextExtractor extends Extractor {
	private HashMap<String, Set<String>> map;
	private HashSet<String> set;

	public LinkTextExtractor() {
		super();
	}

	public LinkTextExtractor(String xmlName,
			HashMap<String, Set<String>> map, HashSet<String> set) {
		super();
		setParser(xmlName);
		this.map = map;
		this.set = set;
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
						if (!set.contains(article))
							continue;

						String linkText = ChineseUtil.translate(m.group(2));
//						System.out.println(article + "  ===>  " + linkText);
						HashSet<String> set = (HashSet)map.get(article);
						if (set == null) {
							set = new HashSet<>();
							set.add(linkText);
							map.put(article, set);
						} else
							set.add(linkText);
					}
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
