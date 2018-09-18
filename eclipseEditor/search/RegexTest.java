package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@prefix rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
public class RegexTest 
{
	public static void main(String[] args) 
	{
//		String example = "@base 	        <http://yuanhuicheng.ca/ontology/2012/01/studio/>.";
//	     Matcher m = Pattern.compile("\\<([^)]+)\\>").matcher(example);
//	     while(m.find()) {
//	       System.out.println(m.group(1));    
//	     }
	     
	     Matcher m1 = Pattern.compile("@prefix(.*)").matcher("@prefix rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
	     while(m1.find()) 
	     {
	    	 System.out.println(m1.group(1));    
		 }
	}
}
