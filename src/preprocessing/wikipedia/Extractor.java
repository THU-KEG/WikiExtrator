package preprocessing.wikipedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

import preprocessing.util.Index;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public abstract class Extractor {
	protected BufferedReader reader;
	protected BufferedWriter writer;
	protected Hashtable<String, String> category;

	protected WikiXMLParser parser;
	protected Index index = null;

	// public static int EnWiki = 1;
	// public static int ZhWiki = 2;
	// protected int lan;

	public Extractor() {
		reader = null;
		writer = null;
		category = null;
		// lan = EnWiki;
	}

	public abstract void extract();

	// protected void setLan(int _lan) {
	// lan = _lan;
	// }
	//
	// protected int getLan() {
	// return lan;
	// }

	protected void write(String line) {
		try {
			writer.write(line);
			writer.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void initCategories() {
		this.category = new Hashtable<String, String>();
	}

	@SuppressWarnings("unused")
	private void setCategories(Hashtable<String, String> _categories) {
		if (this.category != null) {
			this.category.clear();
		}
		this.category = _categories;
	}

	protected BufferedReader generateReader(String filename) {
		try {
			return new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void closeReader(Reader reader) {
		if (reader != null)
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		reader = null;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public void setReader(String filename) {
		try {
			if (filename == null)
				return;
			this.reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeReader() throws IOException {
		if (this.reader != null)
			this.reader.close();
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public void setWriter(String filename) {
		try {
			if (filename == null)
				return;
			this.writer = new BufferedWriter(new FileWriter(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeWriter() throws IOException {
		if (this.writer != null)
			this.writer.close();
	}

	protected boolean validTitle(String title) {
		title = title.toLowerCase();
		if (title.startsWith("template:") || title.startsWith("file:")
				|| title.startsWith("category:")
				|| title.startsWith("wikipedia:")
				|| title.startsWith("mediawiki:")
				|| title.startsWith("portal:") || title.startsWith("help:")) {
			return false;
		}
		return true;
	}

	protected void setParser(String xmlName) {
		this.parser = WikiXMLParserFactory.getSAXParser(xmlName);
	}

}
