package preprocessing.wikipedia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

import preprocessing.util.Index;

/**
 * for jamom
 * 
 * @author Tony
 *
 */

public class wikiextractor extends Extractor{
	
	private HashSet<String> titles;
	
	
	public wikiextractor(){
		super();
	}
	
	public wikiextractor(String xmlName,String outputFile,String titlePath){
		super();
		setParser(xmlName);
		setWriter(outputFile);
		try {
			readTitles(titlePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readTitles(String path) throws IOException{
		titles=new HashSet<String>();
		BufferedReader reader= new BufferedReader(new FileReader(path));
		String line=null;
		while(null!=(line=reader.readLine())){
			if(!line.equals("")){
				titles.add(line);
			}
		}
		reader.close();
	}
	
	@Override
	public void extract() {
		// TODO Auto-generated method stub
		try {
			parser.setPageCallback(new PageCallbackHandler() {
				
				@Override
				public void process(WikiPage page) {
					// TODO Auto-generated method stub
					StringBuilder line = new StringBuilder();
					//get title
					String title = page.getTitle().replaceAll("\n","").trim();
					if(!validTitle(title))
						return;
					if(!titles.contains(title))
						return;
					//outlink
					
					//infobox
					
					//article length
					
					//categories
					
					
					write(line.toString());
				}
			});
			parser.parse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
