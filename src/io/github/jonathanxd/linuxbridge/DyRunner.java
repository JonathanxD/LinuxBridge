package io.github.jonathanxd.linuxbridge;

import io.github.jonathanxd.JwUtils.file.FileUtils;
import io.github.jonathanxd.JwUtils.string.StringUtils;
import io.github.jonathanxd.linuxbridge.processor.DyProcessor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;

public class DyRunner {

	private final LinuxBridgeMain main;
	
	public DyRunner(LinuxBridgeMain linuxBridgeMain) {
		main = linuxBridgeMain;
	}
	public LinkedList<DyProcessor.ProcessResult> run(String command){
		return run(command, null);
	}
	/**
	 * 
	 * @param command Command
	 * @param variables map of variables
	 * @return Result list of scripts result
	 */
	public LinkedList<DyProcessor.ProcessResult> run(String command, HashMap<String, String> variables){
		String install_dir = (String) main.properties.get(LinuxBridgeMain.PROPERTIES.INSTALLATION_DIR);
		LinkedList<DyProcessor.ProcessResult> results = new LinkedList<DyProcessor.ProcessResult>();
		System.out.println("Install: "+install_dir);
		File installDirFile = new File(install_dir);
		int x = 0;
		for(File file : installDirFile.listFiles()){
			if(file.getName().equals("linuxbridge.dy")){
				
				try {
					String linuxBridgeReferences = StringUtils.stringFromBytes(FileUtils.FileInternalData.readFromFile(file), Charset.defaultCharset());
					for(String currentReference : linuxBridgeReferences.split("\n")){
						String name = DyProcessor.Reference.getName(currentReference);
						if(name.trim().equals("."+command)){
							if(DyProcessor.Reference.isDirectory(name)){
								String directory = DyProcessor.Reference.getDirectoryName(name);
								File directoryFile = new File(installDirFile, directory);
								
								if(!directoryFile.exists()){
									System.out.println("Line parse error: line "+(x+1)+"! Directory not found "+directory+" in "+directoryFile.getAbsolutePath());
								}else{
									
									for(File currentFile : directoryFile.listFiles(new FilenameFilter() {@Override public boolean accept(File dir, String name) {return name.endsWith(".javady");}})){
										
										String scriptContent = StringUtils.stringFromBytes(FileUtils.FileInternalData.readFromFile(currentFile), Charset.defaultCharset());
										DyProcessor.ProcessResult processResult = DyProcessor.processScript(scriptContent, variables);
										/**
										 * @TEMP
										 */
										go(currentFile.getAbsoluteFile().getParentFile(), processResult.output);
										results.add(processResult);
									}
										
								}
							}
						}
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
			++x;
		}
		return results;
	}

	private static void go(File dir, String runner){
		ProcessBuilder pb = new ProcessBuilder(runner.split(" "));
		pb.directory(dir);
		try {
			Process process = pb.start();
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
