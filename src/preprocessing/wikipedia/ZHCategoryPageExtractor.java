package preprocessing.wikipedia;

import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import preprocessing.util.ChineseUtil;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class ZHCategoryPageExtractor extends Extractor{
	public ZHCategoryPageExtractor(){
		super();
	}
	
	public ZHCategoryPageExtractor(String xmlName,String outputFile){
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
					title=ChineseUtil.translate(title);
					
					if(!title.startsWith("Category:")){
						return;
					}
					
					title=StringUtils.substringAfter(title, "Category:");
					line.append(title);
					line.append("\t\t");
					line.append(page.getText());
					
					/*//get all the outlinks and recorde their ids
					Vector<String> links=page.getLinks();
					//page.getCategories()
					if((links!=null)&&(links.size()>0)){
						for(int i=0;i<links.size();i++){
							String outlink=links.get(i);
							//if(outlink.startsWith("#"))continue;
							//outlink=StringUtils.substringBefore(outlink, "#");
							outlink=ChineseUtil.translate(outlink);
							//if(!outlink.startsWith("分类:"))continue;
							//outlink=StringUtils.substringAfter(outlink, "分类:");
							line.append(outlink+";");
						}
					}*/
					
									
					
					
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
