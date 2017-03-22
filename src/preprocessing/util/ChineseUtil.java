package preprocessing.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class ChineseUtil {

	public static Hashtable<String, String> mappings = null;
	public static String mappingFile = "etc/util/ch_map.dat";

	public static void loadMappings() {
		try {
			mappings = new Hashtable<String, String>();

			FileInputStream fis = new FileInputStream(ChineseUtil.mappingFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {
				String[] characters = line.split(" ");
				mappings.put(characters[0], characters[1]);
			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static String translate(String traditionalStr) {
		/*if (mappings == null)
			loadMappings();
		StringBuffer sbuf = new StringBuffer(traditionalStr.length());
		for (int i = 0; i < traditionalStr.length(); i++) {
			String character = traditionalStr.substring(i, i + 1);
			if (mappings.containsKey(character))
				sbuf.append(mappings.get(character));
			else
				sbuf.append(character);
		}
		return sbuf.toString().trim();*/
		return HanLP.convertToSimplifiedChinese(traditionalStr).trim();
	}

	public static boolean isChinese(char ch) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
			return true;
		return false;
	}



	public static void main(String[] args) {
		System.out.print(ChineseUtil.translate("tinghua university"));
	}
}
