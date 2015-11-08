package io.github.jonathanxd.linuxbridge.processor;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DyProcessor {

	public static class ProcessResult{
		public final String output;
		ProcessResult(String output) {
			this.output = output;
		}
	}
	
	public static ProcessResult processScript(String javady, HashMap<String, String> variables){
		String output = "";
		for(String current : javady.split("\n")){
			if(current.startsWith("run ")){
				current = current.substring("run ".length());
				Matcher matcher = Pattern.compile("((@|@->-[\\w]+:)[\\w]+)").matcher(current);
				while(matcher.find()){
					String var = matcher.group(0);
					String sp = null;
					String varsp = null;
					if(Pattern.matches("(@->-[\\w]+:[\\w]+)", var)){
						sp = var.split("@")[1].split(":")[0].substring(2);
						varsp = "@"+var.split("@")[1].split(":")[1];
					}
					
					if(variables != null && variables.containsKey((varsp != null ? varsp : var).substring(1))){
						current = current.replace(var, (sp != null ? sp+" " : "")+variables.get((varsp != null ? varsp : var).substring(1)));
					}else{
						current = current.replaceAll(var+"(\\ |)", "");
					}
				}
				output = current;				
			}
		}
		
		return new ProcessResult(output);
	}
	
	
	public static ProcessResult processScript(String javady){
		return processScript(javady, null);
	}
	
	public static class Reference {
		
		public static String getName(String reference){
			return reference.contains("=") ? reference.split("=")[0] : reference;
		}

		public static boolean isDirectory(String reference) {			
			return getName(reference).startsWith(".");
		}
		
		public static String getDirectoryName(String reference) {			
			return isDirectory(getName(reference)) ? getName(reference).split("\\.")[1].trim() : null;
		}
		
	}
	
}
