package preprocessing.wikipedia;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import edu.jhu.nlp.language.Language;
import edu.jhu.nlp.wikipedia.InfoBox;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

/**
 * 
 * @author Miyayx 获取wiki 的 FirstImage： 
 *         1. 从infobox里获得image信息 
 *         2. 根据wiki提供的api获得json，从json中提取准确的image url
 * 
 *         注：本代码只负责第一步
 * 
 *         Example： infobox里的图片信息格式是：
 *         http://zh.wikipedia.org/w/index.php?action=raw&title=%E6%B8%85%E5%8D%8E%E5%A4%A7%E5%AD%A6
 *         |图像            =[[File:Tsinghua University Logo.svg|center|200px]]
 *         |图像说明        =
 *         图片对应json数据:
 *         http://zh.wikipedia.org/w/api.php?action=query&titles=File:Tsinghua_University_Logo.svg&prop=imageinfo&iiprop=url&format=json
 *         
 */
public class ZHFirstImageExtractor extends Extractor {

	public ZHFirstImageExtractor() {
		super();
	}

	public ZHFirstImageExtractor(String xmlName, String outputFile) {
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

					Vector<InfoBox> infoboxes = page.getInfoBoxes(Language.CHINESE);

					String url = null;
					for (InfoBox i : infoboxes) {
						String raw = i.dumpRaw();
						Pattern p1 = Pattern.compile("\\|\\s*(图像|image).*");
						Matcher m1 = p1.matcher(raw);
						if(m1.find()){
//							System.out.println("dumpRaw:"+i.dumpRaw());
							url = m1.group(0);
							//System.out.println("m1:"+url);
							Pattern imagePattern = Pattern.compile("(File|image):.*?\\.((?i)jpg|(?i)svg|(?i)png|(?i)gif)");
							Matcher matcher = imagePattern.matcher(url);
							if(matcher.find()) {
								url = matcher.group(0);
							}
							else{
								url=null;
								continue;
							}
							line.append(url.trim());
							System.out.println(url.trim());
							break;
						}
						else {
							continue;
						}
					}
					if (StringUtils.isBlank(url))
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
