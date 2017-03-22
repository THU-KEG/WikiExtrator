package preprocessing.wikipedia;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import preprocessing.util.InfoUnit;

public class InlinkGenerator {
	
	public static void generate(String input,String output) throws IOException{
		File f = new File(output);
		if(!f.exists()){
			f.createNewFile();
		}
		BufferedWriter writer=new BufferedWriter(new FileWriter(output));
		
		HashMap<String,HashSet<String>> result=new HashMap<String,HashSet<String>>();
		HashSet<String> titles=new HashSet<String>();
		HashSet<String> oldtitles=new HashSet<String>();
		boolean over=false;
		int count=0;
		
		while(!over){
			count++;
			System.out.println(count);
		BufferedReader reader= new BufferedReader(new FileReader(input));
		String line=null;
		while(null!=(line=reader.readLine())){
			InfoUnit info=new InfoUnit(line);
			if(!oldtitles.contains(info.getKey())){
				titles.add(info.getKey());
			}
			if(titles.size()>=800000)break;
		}
		if(titles.size()<800000)over=true;
		reader.close();
		
		
		BufferedReader reader2= new BufferedReader(new FileReader(input));
		String line2=null;
		while(null!=(line2=reader2.readLine())){
			InfoUnit info=new InfoUnit(line2);
			if(info.getKey()==null)continue;
			for(String s:info.getValues()){
				if(titles.contains(s)){
					HashSet<String> tempList=result.get(s);
					if(tempList==null){
						tempList=new HashSet<String>();
						result.put(s, tempList);
					}
					tempList.add(info.getKey());
				}
			}
		}
		reader2.close();
		
		
		Iterator iter=result.keySet().iterator();
		while(iter.hasNext()){
			String key=(String) iter.next();
			HashSet<String> value=result.get(key);
			
			StringBuffer sb=new StringBuffer();
			sb.append(key+"\t\t");
			for(String s:value){
				sb.append(s+";");
			}
			
			writer.write(sb.toString());
			writer.newLine();
		}
		
		
		oldtitles.addAll(titles);
		result=new HashMap<String,HashSet<String>>();
		titles=new HashSet<String>();
		}
		writer.close();
		
	}

}
