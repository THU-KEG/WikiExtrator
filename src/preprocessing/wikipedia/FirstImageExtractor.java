package preprocessing.wikipedia;

import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import edu.jhu.nlp.language.Language;
import edu.jhu.nlp.wikipedia.InfoBox;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

/**
 * 
 * @author Miyayx 获取wiki 的 FirstImage： 1. 从infobox里获得image信息 2.
 *         根据wiki提供的api获得json，从json中提取准确的image url
 * 
 *         注：本代码只负责第一步
 * 
 *         Example： infobox里的图片信息格式是：
 *         http://en.wikipedia.org/w/index.php?action=raw&title=Douglas_Jardine
 *         image = Douglas Jardine Cigarette Card.jpg image_size = 175px
 *         图片对应json数据:
 *         http://en.wikipedia.org/w/api.php?action=query&titles=File
 *         :Douglas_Jardine_Cigarette_Card.jpg&prop=imageinfo&iiprop=url
 * 
 */
public class FirstImageExtractor extends Extractor {
	private String language = Language.ENGLISH;

	public FirstImageExtractor() {
		super();
	}

	public FirstImageExtractor(String xmlName, String outputFile, String language) {
		super();
		setParser(xmlName);
		setWriter(outputFile);
		this.language = language;
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

					Vector<InfoBox> infoboxes = page.getInfoBoxes(language);

					String url = null;
					for (InfoBox i : infoboxes) {
						String raw = i.dumpRaw();
						if (!raw.contains("| image ="))
							continue;
						else {
							System.out.println("title-->" + title);
							url = StringUtils.substringBetween(raw, "image =", "\n").trim()
									.replace("[", "").replace("]", "");
							System.out.println("url-->" + url);
							if(url.length() < 3)//http://en.wikipedia.org/w/index.php?action=raw&title=Vassiliy%20Jirov
								continue;
							if(url.contains("image array")){
								//如果是一个image array就取第一个image
								//http://en.wikipedia.org/w/index.php?action=raw&title=African_American
								url = StringUtils.substringBetween(url, "image1", "|");
								if(url == null || url.length() < 3)
									//http://en.wikipedia.org/w/index.php?action=raw&title=Curtis%20Hairston
									continue;
								}
										url.trim().replace("=", "").replace("[", "").replace("]", "");
								System.out.println("newurl-->" + url);

							if (url.contains("File:")) {
								url = StringUtils.substringAfter(url, "File:").split("\\|")[0];
								System.out.println("newurl-->" + url);
							}
							if (url.contains("Image:")) {
								url = StringUtils.substringAfter(url, "Image:").split("\\|")[0];
								System.out.println("newurl-->" + url);
							}
							if(url.contains("caption ="))
								// http://en.wikipedia.org/w/index.php?action=raw&title=Balkan%20Wars
								
								continue;
							//http://en.wikipedia.org/w/index.php?action=raw&title=Hipparchus
                            url = url.split("\\|")[0];
							line.append(url.trim());

							break;
						}
					}
					if (url == null || url.trim().length() == 0)
						return;
					write(line.toString().replaceAll("\n", "").replaceAll("\r", "").trim());
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
