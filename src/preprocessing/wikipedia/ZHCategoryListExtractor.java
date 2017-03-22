package preprocessing.wikipedia;

import java.util.HashSet;
import java.util.Vector;


import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ZHCategoryListExtractor extends Extractor{
	
	private HashSet<String> categoryNames=new HashSet<String>();
	
	public ZHCategoryListExtractor(){
		super();
	}
	
	public ZHCategoryListExtractor(String xmlName,String outputFile){
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
					
					//title=ChineseUtil.translate(title);
					
					Vector<String> categories=page.getCategories();
					if(categories!=null){
						for(int i=0;i<categories.size();i++){
							String cate=categories.get(i);
							if(cate=="")continue;
							if(categoryNames.contains(cate))continue;
							categoryNames.add(cate);
							if(i<categories.size()-1){
								line.append(cate+"\n");
							}else{
								line.append(cate);
							}
							
						}
						write(line.toString());
					}	
										
					
				}
			});
			parser.parse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
