package preprocessing.wikipedia;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.language.Language;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ZHENLanlinksExtractor extends Extractor{
	
	public ZHENLanlinksExtractor(){
		super();
	}
	
	public ZHENLanlinksExtractor(String xmlName,String outputFile){
		super();
		setParser(xmlName);
		setWriter(outputFile);
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

					String title = page.getTitle().replaceAll("\n","").trim();
					if(!validTitle(title))
						return;
					
					title=ChineseUtil.translate(title);
					
					String translation=page.getTranslatedTitle(Language.ENGLISH);
					if(translation==null || translation.equals("")){
						return;
					}
					
					line.append(title+"\t\t"+translation);					
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
