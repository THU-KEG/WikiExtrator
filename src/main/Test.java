package main;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	// GENERAL_PUNCTUATION 判断中文的“号

	// CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号

	// HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号

	public static boolean isChinese(char c) {

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

		|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

		|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

		|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

		|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

			return true;

		}

		return false;

	}

	public static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();

		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			System.out.println(c);
			if (!isChinese(c)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		Pattern imagePattern = Pattern.compile("File:.*\\.((?i)jpg|(?i)svg|(?i)png)");
		String url = "File: Li Zicheng book.JPG";
		Matcher matcher = imagePattern.matcher(url);
		matcher.find();
		System.out.println(matcher.group());
	}
}