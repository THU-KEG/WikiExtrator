package preprocessing.wikipedia;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.language.Language;
import edu.jhu.nlp.wikipedia.InfoBox;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ZHInfoboxExtractor extends Extractor {
	public ZHInfoboxExtractor() {
		super();
	}

	public ZHInfoboxExtractor(String xmlName, String outputFile) {
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

					line.append(ChineseUtil.translate(title));
					line.append("\t\t");

					Vector<InfoBox> infoboxes = page
							.getInfoBoxes(Language.CHINESE);
					for (InfoBox i : infoboxes) {
						String result = analyzeInfobox(i);
						if (result == null)
							continue;
						line.append(ChineseUtil.translate(result) + "\t");
					}
					write(line.toString().replaceAll("\n", "").replaceAll("\r",
							"").trim());
				}
			});
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String analyzeInfobox(InfoBox infobox) {
		StringBuilder line = new StringBuilder();

		String infoboxDumpRaw = infobox.dumpRaw();
		int firstline = StringUtils.indexOf(infoboxDumpRaw, '\n');
		String infoboxName = infoboxDumpRaw.substring(2, firstline);
		if (!infoboxName.contains("|")) {
			if (infoboxName.trim().length() == 0)
				return null;
			line.append(infoboxName.replaceAll("\\s+", " ").trim() + ":::::");
		} else {
			String[] tmp = infoboxName.split("|");
			if (tmp[0].trim().length() == 0)
				return null;
			line.append(tmp[0].replaceAll("\\s+", " ").trim() + ":::::");
			if (tmp.length > 1) {
				// String.split("=") is wrong
				String attr = processAttr(StringUtils.substringBefore(tmp[1]
						.trim(), "="));
				String val = processEntry(StringUtils.substringAfter(tmp[1]
						.trim(), "="));
				if (attr != null)
					line.append(attr + "::::=" + val + "::::;");
			}
		}

		Pattern pattern = Pattern.compile("^\\|(.*?=.*)|(.*?=.*)\\|$",
				Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(infoboxDumpRaw);
		while (matcher.find()) {
			String tmp = matcher.group().trim();
			// String.split("=") is wrong
			String attr = processAttr(StringUtils.substringBefore(tmp, "="));
			String val = processEntry(StringUtils.substringAfter(tmp, "="));
			if (attr != null)
				line.append(attr + "::::=" + val + "::::;");
		}
		// remove the last character ::::;
		if (line.charAt(line.length() - 1) == ';')
			line.delete(line.length() - 5, line.length());
		return line.toString();
	}

	private String processAttr(String attr) {
		attr = attr.replaceAll("[\\|\\{\\}\\[\\]']", "");
		attr = processEntry(attr);
		if (attr.length() > 30 || attr.length() == 0)
			return null;
		return attr;
	}

	private String processEntry(String entry) {
		return entry.replaceAll("\\s+", " ").trim();
		// return entry.replaceAll("\\[\\[", "").replaceAll("\\]\\]", "")
		// .replaceAll("\\{\\{", "").replaceAll("\\}\\}", "").replaceAll(
		// "<.*?>", "").replaceAll("\\|", " ").trim();
	}
}
