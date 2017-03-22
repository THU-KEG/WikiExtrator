package edu.jhu.nlp.wikipedia;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import edu.jhu.nlp.language.Language;

import preprocessing.util.ChineseUtil;

/**
 * For internal use only -- Used by the {@link WikiPage} class. Can also be used
 * as a stand alone class to parse wiki formatted text.
 * 
 * @author Delip Rao
 * 
 */
public class WikiTextParser {

	private String wikiText = null;
	private Vector<String> pageCats = null;
	private Vector<String> pageLinks = null;
	private Vector<InfoBox> infoBoxes = null;

	private boolean redirect = false;
	private String redirectString = null;
	private boolean stub = false;
	private boolean disambiguation = false;

	private static Pattern redirectPattern = Pattern
			.compile("#REDIRECT\\s*\\[\\[(.*?)\\]\\]");
	private static Pattern stubPattern = Pattern.compile("\\-stub\\}\\}");
	private static Pattern disambCatPattern = Pattern
			.compile("\\{\\{disambig\\}\\}");

	public WikiTextParser(String wtext) {
		// trim the \t character
		wikiText = wtext.replaceAll("\t", " ");
		Matcher matcher = redirectPattern.matcher(wikiText);
		if (matcher.find()) {
			redirect = true;
			if (matcher.groupCount() == 1)
				redirectString = matcher.group(1).trim();
		}
		matcher = stubPattern.matcher(wikiText);
		stub = matcher.find();
		matcher = disambCatPattern.matcher(wikiText);
		disambiguation = matcher.find();
	}

	public boolean isRedirect() {
		return redirect;
	}

	public boolean isStub() {
		return stub;
	}

	public String getRedirectText() {
		return redirectString;
	}

	public String getText() {
		return wikiText;
	}

	public Vector<String> getCategories() {
		if (pageCats == null)
			parseCategories();
		return pageCats;
	}

	public Vector<String> getLinks() {
		if (pageLinks == null)
			parseLinks();
		return pageLinks;
	}

	private void parseCategories() {
		pageCats = new Vector<String>();
		Pattern catPattern = Pattern.compile("\\[\\[Category:(.*?)\\]\\]",
				Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(wikiText);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");
			pageCats.add(temp[0]);
		}
	}

	private void parseLinks() {
		pageLinks = new Vector<String>();

		Pattern catPattern = Pattern.compile("\\[\\[(.*?)\\]\\]",
				Pattern.MULTILINE);
		Matcher matcher = catPattern.matcher(wikiText);
		while (matcher.find()) {
			String[] temp = matcher.group(1).split("\\|");
			if (temp == null || temp.length == 0)
				continue;
			String link = temp[0];
			if (link.contains(":") == false) {
				pageLinks.add(link);
			}
		}
	}

	public String getPlainText(String text) {
		text = text.replaceAll("\n", "");
		// text = text.replaceAll("\\{\\{.*?\\}\\}", " ");
		text = stripBrace(text);

		// strip <> tags
		text = Pattern.compile("<ref>.*?</ref>", Pattern.DOTALL).matcher(text)
				.replaceAll(" ");
		text = Pattern.compile("<.*?>", Pattern.DOTALL).matcher(text)
				.replaceAll(" ");

		text = text.replaceAll("&ndash;", "-").replaceAll(
				"&[a-zA-Z][a-zA-Z0-9]+;", " ");
		text = text.replaceAll("\\s+", " ").trim();

		// text = text.replaceAll("\\[\\[.*?:.*?\\]\\]", " ");
		// text = text.replaceAll("\\[\\[(.*?)\\]\\]", "$1");
		// text = text.replaceAll("\\s(.*?)\\|(\\w+\\s)", " $2");
		// text = text.replaceAll("\\[.*?\\]", " ");
		// text = text.replaceAll("\\'+", "");
		return text;
	}

	public Vector<InfoBox> getInfoBoxes(String language) {
		// parseInfoBox is expensive. Doing it only once like other parse*
		// methods
		if (infoBoxes == null)
			infoBoxes = parseInfoBox(language);
		return infoBoxes;
	}

	private Vector<InfoBox> parseInfoBox(String language) {
		wikiText = stripIndentation(wikiText);
		Vector<InfoBox> vector = new Vector<InfoBox>();

		// extract {{Infobox TemplateName
		String INFOBOX_CONST_STR = "{{";
		// int startPos = wikiText.indexOf(INFOBOX_CONST_STR);
		int endPos = 0;
		int startPos = -1;
		while ((startPos = StringUtils.indexOf(wikiText, INFOBOX_CONST_STR,
				endPos)) >= 0) {
			endPos = startPos + INFOBOX_CONST_STR.length();
			String substring = wikiText.substring(startPos + 2).trim()
					.toLowerCase();
			if (StringUtils.isEmpty(substring))
				continue;

			String infoboxPrefix = getInfoboxPrefix(language);

			if (!substring.startsWith(infoboxPrefix))
				if (language.equals(Language.CHINESE)) {
					char ch = substring.charAt(0);
					if (!ChineseUtil.isChinese(ch))
						continue;
				} else
					continue;

			// EsWiki: Plantilla:Ficha de persona

			int nextline = StringUtils.indexOf(wikiText, '\n', startPos + 1) + 1;
			String substring2 = wikiText.substring(nextline);
			if (StringUtils.isEmpty(substring2) || substring2.charAt(0) != '|')
				continue;

			int bracketCount = 2;
			for (; endPos < wikiText.length(); endPos++) {
				switch (wikiText.charAt(endPos)) {
				case '}':
					bracketCount--;
					break;
				case '{':
					bracketCount++;
					break;
				default:
				}
				if (bracketCount == 0)
					break;
			}
			// if (endPos + 1 >= wikiText.length())
			// return null;
			if (bracketCount != 0)
				continue;
			// This happens due to malformed Infoboxes in wiki text. See Issue
			// #10
			// Giving up parsing is the easier thing to do.
			String infoBoxText = wikiText.substring(startPos, endPos + 1);
			if (infoBoxText.charAt(infoBoxText.length() - 3) == '\n') {
				infoBoxText = trimInfoboxText(cleanWikiFormat(infoBoxText), 2);
				if (infoBoxText != null)
					vector.add(new InfoBox(trimInfoboxLine(infoBoxText)));
			}
		}
		return vector;
	}

	private String getInfoboxPrefix(String language) {
		if (language.equals(Language.SPANISH))
			return "ficha de";
		// else if (language.equals(Language.ENGLISH))
		// return "infobox";
		// else if (language.equals(Language.CHINESE))
		// return "infobox";
		// else if (language.equals(Language.GERMAN))
		// return "infobox";
		// else if (language.equals(Language.FRENCH))
		// return "infobox";
		// else if (language.equals(Language.DUTCH))
		// return "infobox";

		// else if (language.equals(Language.ITALIAN))
		// return "infobox";
		// else if (language.equals(Language.POLISH))
		// return "infobox";
		// else if (language.equals(Language.RUSSIAN))
		// return "infobox";
		// else if (language.equals(Language.JAPANESE))
		// return "infobox";
		// else if (language.equals(Language.PORTUGUESE))
		// return "infobox";
		return "infobox";
	}

	public boolean isLegalLine(String line) {

		if (!line.startsWith("|") || line.length() < 3)
			return false;

		line = cleanWikiFormat(line).replaceAll("\\|", "").replaceAll(
				"\\[\\[.*?\\]\\]", "").replaceAll("\\{\\{.*?\\}\\}", "").trim();
		return line.contains("=");
	}

	private String trimInfoboxLine(String infoboxText) {
		StringBuilder sb = new StringBuilder();
		String[] lines = infoboxText.split("\n");
		sb.append(lines[0] + "\n" + lines[1] + "\n");
		for (int i = 2; i < lines.length - 1; i++) {
			if (!isLegalLine(lines[i])) {
				sb.deleteCharAt(sb.length() - 1);
				sb.append(" ");
			}
			sb.append(lines[i] + "\n");
		}
		sb.append(lines[lines.length - 1]);
		return sb.toString();
	}

	private String trimInfoboxText(String infoboxText, int start) {
		String CITE_CONST_STR = "{{";
		int startPos = StringUtils.indexOf(infoboxText, CITE_CONST_STR, start);
		if (startPos < 0) {
			return infoboxText;
		}
		int bracketCount = 2;
		int endPos = startPos + CITE_CONST_STR.length();
		for (; endPos < infoboxText.length() - 2; endPos++) {
			switch (infoboxText.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}

		if (bracketCount != 0)
			return null;

		infoboxText = infoboxText.substring(0, startPos)
				+ infoboxText.substring(startPos, endPos + 1).replaceAll("\n",
						" ") + infoboxText.substring(endPos + 1);
		return trimInfoboxText(infoboxText, endPos);
	}

	private String stripIndentation(String input) {
		StringBuilder sb = new StringBuilder();
		String[] a = input.split("\n");
		for (String e : a) {
			e = e.trim();
			if (!StringUtils.isEmpty(e))
				sb.append(e + "\n");
		}
		return sb.toString().trim();
	}

	private String cleanWikiFormat(String input) {
		if (input == null)
			return null;
		// strip cite and noise
		input = stripNoiseData(stripCite(input));

		input = input.replaceAll("&ndash;", "-").replaceAll(
				"&[a-zA-Z][a-zA-Z0-9]+;", " ");

		// strip <> tags
		input = Pattern.compile("<ref.*?>.*?</ref>", Pattern.DOTALL).matcher(
				input).replaceAll(" ");
		input = Pattern.compile("<.*?>", Pattern.DOTALL).matcher(input)
				.replaceAll(" ");

		// input = input.replaceAll("\\s+", " ");
		return input.trim();
	}

	private String stripBrace(String text) {
		String CITE_CONST_STR = "{{";
		int startPos = text.indexOf(CITE_CONST_STR);
		if (startPos < 0)
			return text;
		int bracketCount = 2;
		int endPos = startPos + CITE_CONST_STR.length();
		for (; endPos < text.length(); endPos++) {
			switch (text.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}
		if (endPos == text.length() && startPos != 0)
			text = text.substring(0, startPos) + " ";
		else if (endPos != text.length() && startPos != 0)
			text = text.substring(0, startPos) + " "
					+ text.substring(endPos + 1);
		else if (endPos != text.length() && startPos == 0)
			text = " " + text.substring(endPos + 1);
		else
			text = " ";
		return stripBrace(text);
	}

	private String stripCite(String text) {
		String CITE_CONST_STR = "{{cite";
		int startPos = text.indexOf(CITE_CONST_STR);
		if (startPos < 0) {
			startPos = text.indexOf("{{Cite");
			if (startPos < 0)
				return text;
		}
		int bracketCount = 2;
		int endPos = startPos + CITE_CONST_STR.length();
		for (; endPos < text.length(); endPos++) {
			switch (text.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}
		if (endPos == text.length() && startPos != 0)
			text = text.substring(0, startPos) + " ";
		else if (endPos != text.length() && startPos != 0)
			text = text.substring(0, startPos) + " "
					+ text.substring(endPos + 1);
		else if (endPos != text.length() && startPos == 0)
			text = " " + text.substring(endPos + 1);
		else
			text = " ";
		return stripCite(text);
	}

	private String stripFile(String text) {
		String NOISE_DATA_STR = "[[File:";
		int startPos = text.indexOf(NOISE_DATA_STR);
		if (startPos < 0) {
			return text;
		}
		int bracketCount = 2;
		int endPos = startPos + NOISE_DATA_STR.length();
		for (; endPos < text.length(); endPos++) {
			switch (text.charAt(endPos)) {
			case ']':
				bracketCount--;
				break;
			case '[':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}
		if (endPos == text.length() && startPos != 0)
			text = text.substring(0, startPos) + " ";
		else if (endPos != text.length() && startPos != 0)
			text = text.substring(0, startPos) + " "
					+ text.substring(endPos + 1);
		else if (endPos != text.length() && startPos == 0)
			text = " " + text.substring(endPos + 1);
		else
			text = " ";
		return stripFile(text);
	}

	private String stripImage(String text) {
		String NOISE_DATA_STR = "[[Image:";
		int startPos = text.indexOf(NOISE_DATA_STR);
		if (startPos < 0) {
			return text;
		}
		int bracketCount = 2;
		int endPos = startPos + NOISE_DATA_STR.length();
		for (; endPos < text.length(); endPos++) {
			switch (text.charAt(endPos)) {
			case ']':
				bracketCount--;
				break;
			case '[':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}
		if (endPos == text.length() && startPos != 0)
			text = text.substring(0, startPos) + " ";
		else if (endPos != text.length() && startPos != 0)
			text = text.substring(0, startPos) + " "
					+ text.substring(endPos + 1);
		else if (endPos != text.length() && startPos == 0)
			text = " " + text.substring(endPos + 1);
		else
			text = " ";
		return stripFile(text);
	}

	private String stripNoiseData(String text) {
		String NOISE_DATA_STR = "{{notetag";
		int startPos = text.indexOf(NOISE_DATA_STR);
		if (startPos < 0) {
			NOISE_DATA_STR = "{{refTag";
			startPos = text.indexOf(NOISE_DATA_STR);
			if (startPos < 0)
				return text;
		}
		int bracketCount = 2;
		int endPos = startPos + NOISE_DATA_STR.length();
		for (; endPos < text.length(); endPos++) {
			switch (text.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}
		if (endPos == text.length() && startPos != 0)
			text = text.substring(0, startPos) + " ";
		else if (endPos != text.length() && startPos != 0)
			text = text.substring(0, startPos) + " "
					+ text.substring(endPos + 1);
		else if (endPos != text.length() && startPos == 0)
			text = " " + text.substring(endPos + 1);
		else
			text = " ";
		return stripNoiseData(text);
	}

	public boolean isDisambiguationPage() {
		return disambiguation;
	}

	public String getTranslatedTitle(String languageCode) {
		Pattern pattern = Pattern.compile("^\\[\\[" + languageCode
				+ ":(.*?)\\]\\]$", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(wikiText);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
}
