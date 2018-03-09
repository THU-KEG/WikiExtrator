package main;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;

import edu.jhu.nlp.language.Language;

import basic.FileManipulator;
import beans.Infobox;

import preprocessing.util.*;
import preprocessing.wikipedia.*;

public class Preprocess {
	private static String Wiki_Dump_Dir = "dump/enwiki/2017";
	private static String Wikifr_Dump_Dir = "dump/frwiki";
	private static String Temp_Dir = "etc/temp/";
	private static String Output_Dir = "xlore20170407/wikiExtractResult/";
	private final static String zhwikiFilePath = "dump/zhwiki/2016/zhwiki-20160203-pages-articles.xml";
	private final static String frwikiFilePath = "dump/frwiki/frwiki-20161120-pages-articles-multistream.xml";

	public static void init() {
		System.out.println(System.getProperty("user.dir"));

		// BufferedReader br;
		// try {
		// br = new BufferedReader(new FileReader(new File(Wiki_Dump_Dir)));
		// System.out.println(br.readLine());
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	public static void main(String[] args) throws IOException {
		init();
		// cleanSpecialCharacter(new File(Wiki_Dump_Dir));

		//extractEnwiki(Output_Dir);
		//extractZhwiki(Output_Dir);
		extractFrwiki(Output_Dir);

//		cleanSpecialCharacter(new File(Output_Dir));

//		infoboxTemplate("en");
		//infoboxTemplate("zh");
//
//		trimInfobox("en");
//		trimInfobox("zh");
//		trimInfobox("fr");

		//extractMultiInfoboxes(Output_Dir);
		// statMultiInfoboxes("etc/infobox-stat.dat");

		/* For French */
		// String yourWikiDumpPath = "your french wikipedia dump path";
		// String yourInfoboxPath = "the output path of your extracted infobox";
		//extractFrenchInfobox(Wikifr_Dump_Dir, yourInfoboxPath);
	}

	private static void extractFrenchInfobox(String iPath, String oPath)
			throws IOException {
		System.out.println("Start extracting " + "fr" + "wiki infobox...");
		InfoboxExtractor ie = new InfoboxExtractor(iPath, oPath, "fr");
		ie.extract();
		ie.closeWriter();
	}

	private static void cleanSpecialCharacter(File f) throws IOException {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files)
				// repeat only once
				if (!file.isDirectory())
					cleanSpecialCharacter(file);
		} else {
			System.out.println("Clean 'U+200E' in " + f.getName());
			BufferedWriter out = new BufferedWriter(new FileWriter(
					f.getAbsolutePath() + ".tmp"));
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line = null;
			while (null != (line = in.readLine()))
				out.write(line.replaceAll("\\u200E", "").trim() + "\n");
			in.close();
			out.close();
			// f.delete();
			new File(f.getAbsolutePath() + ".tmp").renameTo(f);
		}
	}

	/**
	 * 对infobox的清理，包括： 对template name的清理 删除不必要的infobox property，比如image，并记录删除信息
	 * deleteExtraPairs() in Infobox 对property label进行清理 trimLabel() in Infobox
	 * 
	 * @param language
	 *            en/zh
	 * @throws IOException
	 */
	private static void trimInfobox(String language) throws IOException {

		List<String> templates = FileUtils.readLines(new File(Output_Dir
				+ language + "wiki-template-name.dat"));
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for (String template : templates) {
			if(language.equals("fr")) 
				template = template.substring(7);
			else
				template = template.substring(9);
			String key = templateNameTrim(template);
			Set<String> value = map.get(key);
			if (value == null) {
				value = new HashSet<String>();
				value.add(template);
				map.put(key, value);
			} else
				value.add(template);
		}
		FileManipulator.outputOneToMany(map, Temp_Dir + language
				+ "wiki-template-name-cleaned.dat", "\t\t", ";");

		Set<String> templateSet = map.keySet();

		BufferedWriter out = new BufferedWriter(new FileWriter(Output_Dir
				+ language + "wiki-infobox.dat"));
		BufferedWriter prunedOut = new BufferedWriter(new FileWriter(Temp_Dir
				+ language + "wiki-pruned-infobox.dat"));
		BufferedReader in = new BufferedReader(new FileReader(Output_Dir
				+ language + "wiki-infobox-tmp.dat"));
		String line = null;
		while (null != (line = in.readLine())) {
			line = line.trim();

			String[] a = line.split("\t\t");
			if (a.length != 2) {
				out.write(line + "\n");
				continue;
			}

			StringBuilder sb = new StringBuilder(a[0] + "\t\t");
			String[] infoboxes = a[1].split("\t");
			for (String infobox : infoboxes) {
				Infobox i = new Infobox(infobox);
				i.deleteExtraPairs(); // 删除不需要到pair
				i.trimLabel();// 清理label里的特殊字符
				String type = templateNameTrim(i.getName());
				if (language.equals("zh") && type.startsWith("模板:"))
					type = type.substring(3);
				if (language.equals("fr") && type.startsWith("modèle:"))
					type = type.substring(7);
				if (templateSet.contains(type)) {
					i.setName(type);
					sb.append(i.toString() + "\t");
				} else {
					prunedOut.write(a[0] + "\t\t" + i.toString() + "\n");
				}
			}

			out.write(sb.toString().trim() + "\n");
		}
		in.close();
		out.close();
		prunedOut.close();

		// new File(Output_Dir + language + "wiki-infobox-tmp.dat").delete();
	}

	/**
	 * 将infobox使用的template过滤出来
	 * 
	 * @param language
	 *            en or zh
	 * @throws IOException
	 */
	private static void infoboxTemplate(String language) throws IOException {
		System.out.println("do infoboxTemplate:"+language);
		Set<String> infoboxSet = new HashSet<String>();
		BufferedReader in = new BufferedReader(new FileReader(Output_Dir
				+ language + "wiki-infobox-tmp.dat"));
		String line = null;
		while (null != (line = in.readLine())) {
			line = line.trim();

			String[] a = line.split("\t\t");
			if (a.length != 2)
				continue;

			String[] infoboxes = a[1].split("\t");
			for (String infobox : infoboxes) {
				Infobox i = new Infobox(infobox);
				String type = i.getName();
				if (language.equals("zh") && type.startsWith("模板:"))
					type = type.substring(3);
				infoboxSet.add(templateNameTrim(type));
				// infoboxSet.add(type);
			}
		}
		in.close();

		List<String> templates = FileUtils.readLines(new File(Output_Dir
				+ language + "wiki-template-name.dat"));
		Set<String> templateSet = new HashSet<String>();
		for (String template : templates)
			templateSet.add(templateNameTrim(template.substring(9)));
		// templateSet.add(template.substring(9));
		System.out.println(templateSet.size()+" "+infoboxSet.size());
		FileUtils.writeLines(new File(Temp_Dir + language
				+ "wiki-infobox-template.dat"), infoboxSet, "\n");
		infoboxSet.removeAll(templateSet);
		FileUtils.writeLines(new File(Temp_Dir + language
				+ "wiki-infobox-template-unmapped.dat"), infoboxSet, "\n");
		System.out.println("infoboxTemplate:" + language + "  ...done");
	}

	private static String templateNameTrim(String templateName) {
		return ChineseUtil.translate(
				templateName.replaceAll("_", " ").replaceAll("\\s+", " "))
				.toLowerCase();
	}
	private static void extractFrwiki(String outputDir) throws IOException {

		System.out.println(Wikifr_Dump_Dir);

//		 extractFrhwikiTitle(frwikiFilePath, outputDir + "frwiki-title.dat");//
//
//		 extractFrwikiInfobox(frwikiFilePath, outputDir + "frwiki-infobox_new.dat");//
//		 extractFrwikiCategory(frwikiFilePath, outputDir +"frwiki-category.dat");//
//		 extractFrwikiCategoryParent(frwikiFilePath, outputDir + "frwiki-category-parent.dat"); //

//		 extractFrwikiText(frwikiFilePath, outputDir + "frwiki-text.dat"); //

//		 extractFrwikiLanlinks(frwikiFilePath, outputDir + "frwiki-fr-en-langlink.dat");//
//
//		 extractFrwikiCategoryLanlinks(frwikiFilePath, outputDir
//		 + "frwiki-category-langlink.dat"); //BLANK FILE ???
//		 extractFrwikiRedirect(frwikiFilePath, outputDir +
//		 "frwiki-redirect.dat"); //
		 extractFrwikiTemplate(frwikiFilePath, outputDir
		 + "frwiki-template-name.dat");//

//		 extractFrwikiOutlink(frwikiFilePath, outputDir +
//				 "frwiki-outlink.dat");//
//		 generateInlink(outputDir + "frwiki-outlink.dat", outputDir
//				 + "frwiki-inlink.dat"); //

//		 extractFrwikiAbstract(frwikiFilePath, outputDir + "frwiki-abstract.dat");//
//		 extractFrwikiLength(frwikiFilePath, outputDir
//		 + "frwiki-length.dat");
//
//		 extractFrwikiLinkText(zhwikiFilePath, outputDir + "frwiki-linktext.dat"); 
//		 extractFrwikiID(frwikiFilePath, outputDir + "frwiki-id.dat");
	}

	private static void extractEnwiki(String outputDir) throws IOException {

		// System.out.println(enwikiFileNames);
		System.out.println(Wiki_Dump_Dir);
		// return;

		List<String> enwikiFileNames = getEnwikiFileNames(Wiki_Dump_Dir);

		for (int i = 0; i < enwikiFileNames.size(); i++)
			System.out.println(enwikiFileNames);

		extractEnwikiID(enwikiFileNames, outputDir + "enwiki-ID.dat");
//
//		extractEnwikiTitle(enwikiFileNames, outputDir + "enwiki-title.dat");
//		extractEnwikiTitleLocation(enwikiFileNames, outputDir
//				+ "enwiki-title-location.dat");
//
//		extractEnwikiInfobox(enwikiFileNames, outputDir
//				+ "enwiki-infobox-tmp.dat");
//		extractEnwikiCategory(enwikiFileNames, outputDir
//				+ "enwiki-category.dat");
//
//		extractEnwikiCategoryParent(enwikiFileNames, outputDir
//				+ "enwiki-category-parent.dat");
//
//		extractEnwikiText(enwikiFileNames, outputDir + "enwiki-text.dat");
//
//		extractEnwikiLanlinks(enwikiFileNames, outputDir
//				+ "enwiki-en-zh-langlink.dat");
//		extractEnwikiCatetoryLanlinks(enwikiFileNames, outputDir
//				+ "enwiki-category-langlink.dat");
//
//		extractEnwikiRedirect(enwikiFileNames, outputDir
//				+ "enwiki-redirect.dat");
//		extractEnwikiTemplate(enwikiFileNames, outputDir
//				+ "enwiki-template-name.dat");
//
//		extractEnwikiOutlink(enwikiFileNames, outputDir + "enwiki-outlink.dat");
//		generateInlink(outputDir + "enwiki-outlink.dat", outputDir
//				+ "enwiki-inlink.dat");
//
//		extractEnwikiAbstract(enwikiFileNames, outputDir
//				+ "enwiki-abstract.dat");
//		extractEnwikiFirstImage(enwikiFileNames, outputDir
//				+ "enwiki-firstimage.dat");
//		extractEnwikiLength(enwikiFileNames, outputDir
//				+ "enwiki/enwiki-length.dat");
//
//		extractEnwikiLinkText(enwikiFileNames, outputDir
//				+ "enwiki/enwiki-linktext.dat");
//
//		extractLinkMention(enwikiFileNames, outputDir
//				+ "enwiki-linkmention.dat");
	}

	private static void extractZhwiki(String outputDir) throws IOException {
		
		 extractZhwikiTitle(zhwikiFilePath, outputDir + "zhwiki-title.dat");
//
//		 extractZhwikiInfobox(zhwikiFilePath, outputDir
//		 + "zhwiki-infobox-tmp.dat");
//		 extractZhwikiCategory(zhwikiFilePath, outputDir +"zhwiki-category.dat");
//		 extractZhwikiCategoryParent(zhwikiFilePath, outputDir + "zhwiki-category-parent.dat");
//
//		 extractZhwikiText(zhwikiFilePath, outputDir + "zhwiki-text.dat");
//
//		 extractZhwikiLanlinks(zhwikiFilePath, outputDir + "zhwiki-zh-en-langlink.dat");
//
//		 extractZhwikiCategoryLanlinks(zhwikiFilePath, outputDir
//		 + "zhwiki-category-langlink.dat");
//
//		 extractZhwikiRedirect(zhwikiFilePath, outputDir +
//		 "zhwiki-redirect.dat");
//		 extractZhwikiTemplate(zhwikiFilePath, outputDir
//		 + "zhwiki-template-name.dat");
//
//		 extractZhwikiOutlink(zhwikiFilePath, outputDir +
//				 "zhwiki-outlink.dat");
//		 generateInlink(outputDir + "zhwiki-outlink.dat", outputDir
//				 + "zhwiki-inlink.dat");
//
//		 extractZhwikiAbstract(zhwikiFilePath, outputDir + "zhwiki-abstract.dat");
//		 extractZhwikiLength(zhwikiFilePath, outputDir
//		 + "zhwiki/zhwiki-length.dat");

//		 extractZhwikiLinkText(zhwikiFilePath, outputDir + "zhwiki/zhwiki-linktext.dat");

//		extractZhwikiFirstImage(zhwikiFilePath,outputDir+"zhwiki-firstimage.dat");

	}

	private static void extractMultiInfoboxes(String outputDir)
			throws IOException {
		File dir = new File("/home/keg/datastore/Wikipedia/20120802/");
		File[] files = dir.listFiles();
		for (File f : files) {
			String language = f.getName().substring(0, 2);
			if (language.equals(Language.ENGLISH)
					|| language.equals(Language.CHINESE))
				continue;
			System.out.println("Start extracting " + language
					+ "wiki infobox...");
			InfoboxExtractor te = new InfoboxExtractor(f.getAbsolutePath(),
					outputDir + language + "wiki-infobox.dat", language);
			te.extract();
			te.closeWriter();
		}
	}

	private static void statMultiInfoboxes(String path) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		File dir = new File("etc/");
		File[] files = dir.listFiles();
		for (File f : files) {
			String fileName = f.getName();
			if (!fileName.endsWith("-infobox.dat"))
				continue;
			int total = 0, count = 0;
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line = null;
			while (null != (line = in.readLine())) {
				String[] a = line.trim().split("\t\t");
				if (a.length == 2)
					count++;
				total++;
			}
			in.close();
			out.write(fileName.substring(0, 6) + ";" + total + ";" + count
					+ ";" + (double) count / total + "\n");
			out.flush();
		}
		out.close();
	}

	@SuppressWarnings("unused")
	private static void categoryPageExtract() {
		String zhwikiArticle = "/home/keg/datastore/DataSet/Wikipedia/zh/zhwiki-20111009-pages-articles.xml";// "/home/keg/datastore/DataSet/Wikipedia/articles/enwiki-latest-pages-articles";
		String zhoutputPath = "/home/keg/wzc/wikidata/temp/zh-category-links.txt";

		ZHCategoryPageExtractor zhte = new ZHCategoryPageExtractor(
				zhwikiArticle, zhoutputPath);
		zhte.extract();
	}

	public static void generateInlink(String outlinkPath, String inlinkPath)
			throws IOException {
		System.out.println("Start generating inlink...");
		InlinkGenerator.generate(outlinkPath, inlinkPath);
	}

	private static List<String> getEnwikiFileNames(String wikiDirPath) {
		File dir = new File(wikiDirPath);
		if (!dir.isDirectory()) {
			System.out.println("Enwiki needs directory of wiki, not a file");
			return null;
		}

		File[] files = dir.listFiles();
		if (files.length == 0) {
			System.out.println("No file");
			return null;
		}

		List<String> sourceFilenames = new ArrayList<String>();
		for (File f : files) {
			String fileName = f.getName();
			if (fileName.startsWith("enwiki") && !f.getName().endsWith(".tmp"))
				sourceFilenames.add(f.getAbsolutePath());
		}
		Collections.sort(sourceFilenames);

		return sourceFilenames;
	}

	public static void extractEnwikiTitle(List<String> sourceFilenames,
			String enTitlePath) throws IOException {
		System.out.println("Start extracting enwiki title...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			TitleExtractor te = new TitleExtractor(filename, filename
					+ "-title.tmp");
			te.extract();
			te.closeWriter();
			tmpFilenames.add(filename + "-title.tmp");
		}

		new FileCombiner(tmpFilenames, enTitlePath).combineAndDelete();
	}

	public static void extractEnwikiTitleLocation(List<String> sourceFilenames,
			String enTitleLocPath) throws IOException {
		System.out.println("Start extracting enwiki title location...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			TitleLocationExtractor te = new TitleLocationExtractor(filename,
					filename + "-title-location.tmp");
			te.extract();
			te.closeWriter();
			tmpFilenames.add(filename + "-title-location.tmp");
		}

		new FileCombiner(tmpFilenames, enTitleLocPath).combineAndDelete();
	}

	public static void extractEnwikiInfobox(List<String> sourceFilenames,
			String enInfoboxPath) throws IOException {
		// sourceFilenames = new ArrayList<String>();
		// sourceFilenames
		// .add("/home/keg/datastore/wzg/Wikipedia/Raw Data/enwiki/enwiki-20120403-pages-articles13.xml-p002425002p003124997");

		System.out.println("Start extracting enwiki infobox...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			InfoboxExtractor ie = new InfoboxExtractor(filename, filename
					+ "-infobox.tmp", Language.ENGLISH);
			ie.extract();
			ie.closeWriter();
			tmpFilenames.add(filename + "-infobox.tmp");
		}

		new FileCombiner(tmpFilenames, enInfoboxPath).combineAndDelete();
	}

	public static void extractLinkMention(List<String> sourceFilenames,
			String enOutlinkPath) throws IOException {
		System.out.println("Start extracting enwiki link mention...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			LinkMentionExtractor le = new LinkMentionExtractor(filename,
					filename + "-linkmention.tmp");
			le.extract();
			le.closeWriter();
			tmpFilenames.add(filename + "-linkmention.tmp");
		}

		new FileCombiner(tmpFilenames, enOutlinkPath).combineAndDelete();
	}

	public static void extractEnwikiOutlink(List<String> sourceFilenames,
			String enOutlinkPath) throws IOException {
		System.out.println("Start extracting enwiki outlink...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			LinkExtractor le = new LinkExtractor(filename, filename
					+ "-outlink.tmp");
			le.extract();
			le.closeWriter();
			tmpFilenames.add(filename + "-outlink.tmp");
		}

		new FileCombiner(tmpFilenames, enOutlinkPath).combineAndDelete();
	}

	public static void extractEnwikiCategory(List<String> sourceFilenames,
			String enCatePath) throws IOException {
		System.out.println("Start extracting enwiki category...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			CategoryExtractor ce = new CategoryExtractor(filename, filename
					+ "-category.tmp",Language.ENGLISH);
			ce.extract();
			ce.closeWriter();
			tmpFilenames.add(filename + "-category.tmp");
		}

		new FileCombiner(tmpFilenames, enCatePath).combineAndDelete();
	}

	/**
	 * 我加的
	 * 
	 * @param sourceFilenames
	 * @param enCatePath
	 * @throws IOException
	 */
	public static void extractEnwikiID(List<String> sourceFilenames,
			String enCatePath) throws IOException {
		System.out.println("Start extracting enwiki ID...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			IDExtractor ce = new IDExtractor(filename, filename + "-ID.tmp");
			ce.extract();
			ce.closeWriter();
			tmpFilenames.add(filename + "-ID.tmp");
		}

		new FileCombiner(tmpFilenames, enCatePath).combineAndDelete();
	}

	private static void extractEnwikiCategoryParent(
			List<String> sourceFilenames, String enCateParentPath)
			throws IOException {
		System.out.println("Start extracting enwiki category parent...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			CategoryParentExtractor ce = new CategoryParentExtractor(filename,
					filename + "-category-parent.tmp");
			ce.extract();
			ce.closeWriter();
			tmpFilenames.add(filename + "-category-parent.tmp");
		}

		new FileCombiner(tmpFilenames, enCateParentPath).combineAndDelete();
	}

	public static void extractEnwikiLength(List<String> sourceFilenames,
			String enLengthPath) throws IOException {
		System.out.println("Start extracting enwiki length...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			ArticleLengthExtractor ale = new ArticleLengthExtractor(filename,
					filename + "-length.tmp");
			ale.extract();
			ale.closeWriter();
			tmpFilenames.add(filename + "-length.tmp");
		}

		new FileCombiner(tmpFilenames, enLengthPath).combineAndDelete();
	}

	public static void extractEnwikiRedirect(List<String> sourceFilenames,
			String enRedirectPath) throws IOException {
		System.out.println("Start extracting enwiki redirect...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			RedirectExtractor re = new RedirectExtractor(filename, filename
					+ "-redirect.tmp");
			re.extract();
			re.closeWriter();
			tmpFilenames.add(filename + "-redirect.tmp");
		}

		new FileCombiner(tmpFilenames, enRedirectPath).combineAndDelete();
	}

	public static void extractEnwikiLanlinks(List<String> sourceFilenames,
			String enLanlinksPath) throws IOException {
		System.out.println("Start extracting enwiki langlink...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			LanLinkExtractor lle = new LanLinkExtractor(filename, filename
					+ "-lanlink.tmp", Language.ENGLISH, Language.CHINESE);
			lle.extract();
			lle.closeWriter();
			tmpFilenames.add(filename + "-lanlink.tmp");
		}

		new FileCombiner(tmpFilenames, enLanlinksPath).combineAndDelete();
	}

	public static void extractEnwikiCatetoryLanlinks(
			List<String> sourceFilenames, String enCateLanlinksPath)
			throws IOException {
		System.out.println("Start extracting enwiki category langlink...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			CategoryLanLinkExtractor lle = new CategoryLanLinkExtractor(
					filename, filename + "-category-lanlink.tmp",
					Language.ENGLISH, Language.CHINESE);
			lle.extract();
			lle.closeWriter();
			tmpFilenames.add(filename + "-category-lanlink.tmp");
		}

		new FileCombiner(tmpFilenames, enCateLanlinksPath).combineAndDelete();
	}

	public static void extractEnwikiAbstract(List<String> sourceFilenames,
			String enAbstractPath) throws IOException {
		System.out.println("Start extracting enwiki abstract...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			AbstractExtractor ae = new AbstractExtractor(filename, filename
					+ "-abstract.tmp");
			ae.extract();
			ae.closeWriter();
			tmpFilenames.add(filename + "-abstract.tmp");
		}

		new FileCombiner(tmpFilenames, enAbstractPath).combineAndDelete();
	}

	public static void extractEnwikiFirstImage(List<String> sourceFilenames,
			String enAbstractPath) throws IOException {
		System.out.println("Start extracting enwiki firstimage...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			Extractor ae = new FirstImageExtractor(filename, filename
					+ "-firstimage.tmp", Language.ENGLISH);
			ae.extract();
			ae.closeWriter();
			tmpFilenames.add(filename + "-firstimage.tmp");
		}

		new FileCombiner(tmpFilenames, enAbstractPath).combineAndDelete();
	}

	public static void extractEnwikiText(List<String> sourceFilenames,
			String enTextPath) throws IOException {
		System.out.println("Start extracting enwiki text...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			TextExtractor te = new TextExtractor(filename, filename
					+ "-text.tmp");
			te.extract();
			te.closeWriter();
			tmpFilenames.add(filename + "-text.tmp");
		}

		new FileCombiner(tmpFilenames, enTextPath).combineAndDelete();
	}

	public static void extractEnwikiTemplate(List<String> sourceFilenames,
			String enTextPath) throws IOException {
		System.out.println("Start extracting enwiki template...");
		List<String> tmpFilenames = new ArrayList<String>();

		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			TemplateExtractor te = new TemplateExtractor(filename, filename
					+ "-template.tmp");
			te.extract();
			te.closeWriter();
			tmpFilenames.add(filename + "-template.tmp");
		}

		new FileCombiner(tmpFilenames, enTextPath).combineAndDelete();
	}

	public static void extractEnwikiLinkText(List<String> sourceFilenames,
			String enLinkTextPath) throws IOException {
		System.out.println("Start extracting enwiki LinkText...");
		HashSet<String> set = new HashSet<String>();
		BufferedReader in = new BufferedReader(new FileReader(
				Output_Dir + "enwiki-title.dat"));
		String line = null;
		while (null != (line = in.readLine())) {
			set.add(line.trim());
		}
		in.close();

		HashMap<String, Set<String>> map = new HashMap<>();
		for (String filename : sourceFilenames) {
			System.out.println(">>Process " + filename);
			LinkTextExtractor te = new LinkTextExtractor(filename, map, set);
			te.extract();
			te.closeWriter();
		}

		FileManipulator.outputOneToMany(map,enLinkTextPath,"\t\t",";;");
//		BufferedWriter out = new BufferedWriter(new FileWriter(enLinkTextPath));
//		for (Map.Entry<String, HashSet<String>> e : map.entrySet()) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(e.getKey() + "\t\t");
//			for (String element : e.getValue())
//				sb.append(element + ";");
//			if (sb.charAt(sb.length() - 1) == ';')
//				sb.deleteCharAt(sb.length() - 1);
//			out.write(sb.toString() + "\n");
//			out.flush();
//		}
//		out.close();
	}
	
	public static void extractFrhwikiTitle(String wikiArticle, String frTitlePath)
			throws IOException {
		System.out.println("Start extracting frwiki title...");
		FRTItleExtractor te = new FRTItleExtractor(wikiArticle, frTitlePath);
		te.extract();
		te.closeWriter();
	}
	
	public static void extractFrwikiInfobox(String wikiArticle,
			String frInfoboxPath) throws IOException {
		System.out.println("Start extracting frwiki infobox...");
		FRInfoboxExtractor te = new FRInfoboxExtractor(wikiArticle,
				frInfoboxPath);
		te.extract();
		te.closeWriter();
	}

	public static void extractFrwikiOutlink(String wikiArticle,
			String frOutlinkPath) throws IOException {
		System.out.println("Start extracting frwiki outlink...");
		FRLinkExtractor te = new FRLinkExtractor(wikiArticle, frOutlinkPath);
		te.extract();
		te.closeWriter();
	}

	public static void extractFrwikiCategory(String wikiArticle,
			String frCatePath) throws IOException {
		System.out.println("Start extracting frwiki category...");
		CategoryExtractor te = new CategoryExtractor(wikiArticle,
				frCatePath,Language.FRENCH);
		te.extract();
		te.closeWriter();
	}

	public static void extractFrwikiCategoryParent(String wikiArticle,
			String frCateParentPath) throws IOException {
		System.out.println("Start extracting frwiki category page...");
		FRCategoryParentExtractor cpe = new FRCategoryParentExtractor(wikiArticle,
				frCateParentPath);
		cpe.extract();
		cpe.closeWriter();
	}
	
	public static void extractFrwikiLength(String wikiArticle,
			String frLengthPath) throws IOException {
		System.out.println("Start extracting frwiki length...");
		FRArticleLengthExtractor zale = new FRArticleLengthExtractor(
				wikiArticle, frLengthPath);
		zale.extract();
		zale.closeWriter();
	}

	public static void extractFrwikiRedirect(String wikiArticle,
			String frRedirectPath) throws IOException {
		System.out.println("Start extracting frwiki redirect...");
		FRRedirectExtractor re = new FRRedirectExtractor(wikiArticle,
				frRedirectPath);
		re.extract();
		re.writeToFile();
		re.closeWriter();
	}

	public static void extractFrwikiLanlinks(String wikiArticle,
			String frLanlinkPath) throws IOException {
		System.out.println("Start extracting frwiki langlink...");
		LanLinkExtractor lle = new LanLinkExtractor(wikiArticle, frLanlinkPath,
				Language.FRENCH, Language.ENGLISH);
		lle.extract();
		lle.closeWriter();
	}
	//?

	public static void extractFrwikiCategoryLanlinks(String wikiArticle,
			String frCateLanlinkPath) throws IOException {
		System.out.println("Start extracting frwiki category langlink...");
		CategoryLanLinkExtractor lle = new CategoryLanLinkExtractor(
				wikiArticle, frCateLanlinkPath, Language.FRENCH,
				Language.ENGLISH);
		lle.extract();
		lle.closeWriter();
	}

	public static void extractFrwikiAbstract(String wikiArticle,
			String zhAbstractPath) throws IOException {
		System.out.println("Start extracting frwiki abstract...");
		AbstractExtractor zae = new AbstractExtractor(wikiArticle,
				zhAbstractPath);
		zae.extract();
		zae.closeWriter();
	}
	//?

	public static void extractFrwikiText(String wikiArticle, String frTextPath)
			throws IOException {
		System.out.println("Start extracting frwiki text...");
		TextExtractor zae = new TextExtractor(wikiArticle, frTextPath);
		zae.extract();
		zae.closeWriter();
	}

	public static void extractFrwikiTemplate(String wikiArticle,
			String frTemplatePath) throws IOException {
		System.out.println("Start extracting frwiki template...");
		// FileUtils
		// .cleanDirectory(new File(
		// "/home/keg/wzg/Data/Wikipedia/Extracted Data/zhwiki/template/"));
		FRTemplateExtractor zae = new FRTemplateExtractor(wikiArticle,
				frTemplatePath);
		zae.extract();
		zae.closeWriter();
	}
	
	public static void extractFrwikiLinkText(String wikiArticle,
			String frLinkTextPath) throws IOException {
		System.out.println("Start extracting frwiki LinkText...");
		HashSet<String> set = new HashSet<String>();
		BufferedReader in = new BufferedReader(
				new FileReader(
						Output_Dir + "/frwiki-title.dat"));
		String line = null;
		while (null != (line = in.readLine())) {
			set.add(line.trim());
		}
		in.close();

		HashMap<String, Set<String>> map = new HashMap<>();
		FRLinkTextExtractor te = new FRLinkTextExtractor(wikiArticle, map, set);
		te.extract();
		te.closeWriter();

		FileManipulator.outputOneToMany(map,frLinkTextPath,"\t\t",";;");
//		BufferedWriter out = new BufferedWriter(new FileWriter(zhLinkTextPath));
//		for (Map.Entry<String, HashSet<String>> e : map.entrySet()) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(e.getKey() + "\t\t");
//			for (String element : e.getValue())
//				sb.append(element + ";");
//			if (sb.charAt(sb.length() - 1) == ';')
//				sb.deleteCharAt(sb.length() - 1);
//			out.write(sb.toString() + "\n");
//			out.flush();
//		}
//		out.close();
	}
	
	public static void extractFrwikiID(String wikiArticle,
			String outputDir) throws IOException {
			System.out.println("Start extracting frwiki  ID...");
			
			//We use a different wikiArticleFile for this one because it needs to be compatible with the file frwiki-latest-langlinks.sql
			wikiArticle = "dump/frwiki/frwiki-latest-pages-articles-multistream.xml";
			IDExtractor ce = new IDExtractor(wikiArticle, outputDir);
			ce.extract();
			ce.closeWriter();
	}

	public static void extractZhwikiTitle(String wikiArticle, String zhTitlePath)
			throws IOException {
		System.out.println("Start extracting zhwiki title...");
		ZHTItleExtractor te = new ZHTItleExtractor(wikiArticle, zhTitlePath);
		te.extract();
		te.closeWriter();
	}

	public static void extractZhwikiInfobox(String wikiArticle,
			String zhInfoboxPath) throws IOException {
		System.out.println("Start extracting zhwiki infobox...");
		ZHInfoboxExtractor te = new ZHInfoboxExtractor(wikiArticle,
				zhInfoboxPath);
		te.extract();
		te.closeWriter();
	}

	public static void extractZhwikiOutlink(String wikiArticle,
			String zhOutlinkPath) throws IOException {
		System.out.println("Start extracting zhwiki outlink...");
		ZHLinkExtractor te = new ZHLinkExtractor(wikiArticle, zhOutlinkPath);
		te.extract();
		te.closeWriter();
	}

	public static void extractZhwikiCategory(String wikiArticle,
			String zhCatePath) throws IOException {
		System.out.println("Start extracting zhwiki category...");
		ZHCategoryExtractor te = new ZHCategoryExtractor(wikiArticle,
				zhCatePath);
		te.extract();
		te.closeWriter();
	}

	public static void extractZhwikiCategoryParent(String wikiArticle,
			String zhCateParentPath) throws IOException {
		System.out.println("Start extracting zhwiki category page...");
		CategoryParentExtractor cpe = new CategoryParentExtractor(wikiArticle,
				zhCateParentPath);
		cpe.extract();
		cpe.closeWriter();
	}

	public static void extractZhwikiLength(String wikiArticle,
			String zhLengthPath) throws IOException {
		System.out.println("Start extracting zhwiki length...");
		ZHArticleLengthExtractor zale = new ZHArticleLengthExtractor(
				wikiArticle, zhLengthPath);
		zale.extract();
		zale.closeWriter();
	}

	public static void extractZhwikiRedirect(String wikiArticle,
			String zhRedirectPath) throws IOException {
		System.out.println("Start extracting zhwiki redirect...");
		ZHRedirectExtractor re = new ZHRedirectExtractor(wikiArticle,
				zhRedirectPath);
		re.extract();
		re.closeWriter();
	}

	public static void extractZhwikiLanlinks(String wikiArticle,
			String zhLanlinkPath) throws IOException {
		System.out.println("Start extracting zhwiki langlink...");
		LanLinkExtractor lle = new LanLinkExtractor(wikiArticle, zhLanlinkPath,
				Language.CHINESE, Language.ENGLISH);
		lle.extract();
		lle.closeWriter();
	}

	public static void extractZhwikiCategoryLanlinks(String wikiArticle,
			String zhCateLanlinkPath) throws IOException {
		System.out.println("Start extracting zhwiki category langlink...");
		CategoryLanLinkExtractor lle = new CategoryLanLinkExtractor(
				wikiArticle, zhCateLanlinkPath, Language.CHINESE,
				Language.ENGLISH);
		lle.extract();
		lle.closeWriter();
	}

	public static void extractZhwikiAbstract(String wikiArticle,
			String zhAbstractPath) throws IOException {
		System.out.println("Start extracting zhwiki abstract...");
		ZHAbstractExtractor zae = new ZHAbstractExtractor(wikiArticle,
				zhAbstractPath);
		zae.extract();
		zae.closeWriter();
	}

	public static void extractZhwikiText(String wikiArticle, String zhTextPath)
			throws IOException {
		System.out.println("Start extracting zhwiki text...");
		ZHTextExtractor zae = new ZHTextExtractor(wikiArticle, zhTextPath);
		zae.extract();
		zae.closeWriter();
	}

	public static void extractZhwikiTemplate(String wikiArticle,
			String zhTemplatePath) throws IOException {
		System.out.println("Start extracting zhwiki template...");
		// FileUtils
		// .cleanDirectory(new File(
		// "/home/keg/wzg/Data/Wikipedia/Extracted Data/zhwiki/template/"));
		TemplateExtractor zae = new TemplateExtractor(wikiArticle,
				zhTemplatePath);
		zae.extract();
		zae.closeWriter();
	}

	public static void extractZhwikiFirstImage(String wikiArticle,
											 String zhFirstImagePath) throws IOException {
		System.out.println("Start extracting zhwiki FirstImage...");
		// FileUtils
		// .cleanDirectory(new File(
		// "/home/keg/wzg/Data/Wikipedia/Extracted Data/zhwiki/template/"));
		ZHFirstImageExtractor zae = new ZHFirstImageExtractor(wikiArticle,
				zhFirstImagePath);
		zae.extract();
		zae.closeWriter();
	}

	public static void extractZhwikiLinkText(String wikiArticle,
			String zhLinkTextPath) throws IOException {
		System.out.println("Start extracting zhwiki LinkText...");
		HashSet<String> set = new HashSet<String>();
		BufferedReader in = new BufferedReader(
				new FileReader(
						Output_Dir + "/zhwiki-title.dat"));
		String line = null;
		while (null != (line = in.readLine())) {
			set.add(line.trim());
		}
		in.close();

		HashMap<String, Set<String>> map = new HashMap<>();
		LinkTextExtractor te = new LinkTextExtractor(wikiArticle, map, set);
		te.extract();
		te.closeWriter();

		FileManipulator.outputOneToMany(map,zhLinkTextPath,"\t\t",";;");
//		BufferedWriter out = new BufferedWriter(new FileWriter(zhLinkTextPath));
//		for (Map.Entry<String, HashSet<String>> e : map.entrySet()) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(e.getKey() + "\t\t");
//			for (String element : e.getValue())
//				sb.append(element + ";");
//			if (sb.charAt(sb.length() - 1) == ';')
//				sb.deleteCharAt(sb.length() - 1);
//			out.write(sb.toString() + "\n");
//			out.flush();
//		}
//		out.close();
	}

}
