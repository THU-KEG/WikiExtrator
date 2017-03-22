package preprocessing.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GetListArticles {
	
	public static void extract(String listPath,String input,String output) throws IOException{
		//load article list
		HashSet<String> titleSet=new HashSet<String>();
		ArrayList<String> titleList=new ArrayList<String>();
		BufferedReader reader= new BufferedReader(new FileReader(listPath));
		String line=null;
		while(null!=(line=reader.readLine())){
			if(!line.equals("")){
				titleList.add(line.trim());
			}
		}
		reader.close();
		titleSet.addAll(titleList);
		//extract information
		HashMap<String,String> result=new HashMap<String,String>();
		BufferedReader reader2= new BufferedReader(new FileReader(input));
		String line2=null;
		while(null!=(line2=reader2.readLine())){
			String [] temp=line2.split("\t\t");
			if(titleSet.contains(temp[0])){
				result.put(temp[0], line2);
			}
		}
		reader2.close();
		//output
		BufferedWriter writer=new BufferedWriter(new FileWriter(output));
		for(String t:titleList){
			String info=result.get(t);
			if(info!=null){
				writer.write(info);
			}else{
				writer.write(t);
			}
			writer.newLine();
		}
		writer.close();
	}

}
