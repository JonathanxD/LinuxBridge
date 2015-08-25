package io.github.jonathanxd.linuxbridge;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class LinuxBridgeMain {
	
	private static String[] arguments = new String[0];
	private static final String resourceDirectory = "resources/";
	protected final Properties properties = new Properties();
	private DyRunner runner;
	
	static class PROPERTIES{
		static final String INSTALLATION_DIR = "linuxbridge.installatio.dir";
		static final String JAR_FILE = "linuxbridge.jarfile";
		static final String RESOURCE_LIST = "linuxbridge.resource.list";
	}
	
	private LinuxBridgeMain() {
		
	}
	
	private static LinuxBridgeMain create(){
		LinuxBridgeMain linuxBridgeMain = new LinuxBridgeMain();
		linuxBridgeMain.load();
		return linuxBridgeMain;
	}
	
	private void load() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		int found = 0;
		System.out.println("Checking classes...");
		for(StackTraceElement ste : stack){
			if(ste.getClassName().equals(Thread.class.getName())){
				++found;
			}
			if(!ste.getClassName().contains("java.") && !ste.getClassName().equals(LinuxBridgeMain.class.getName())){
				--found;
			}
			if(ste.getClassName().equals(LinuxBridgeMain.class.getName())){				
				++ found;
			}
		}
		
		if(found == 4){			
			try {
				
				
				Class<?> c = Class.forName(stack[stack.length-1].getClassName());
				
				ArrayList<String> resourceProcess = new ArrayList<String>();
				
				boolean isJar = false;
				
				String file;
				String RESOURCE_FOLDER;
				file = c.getProtectionDomain().getCodeSource().getLocation().getFile();
				if(!file.endsWith(".jar")){
					if(arguments.length == 0){
						throw new RuntimeException("[ERROR] Not a jar file, please specify resources folder: java [COMMAND] [RESOURCE_DIR]");
					}
					System.out.println("[DEBUG] ${RESOURCE_FOLDER} = "+(RESOURCE_FOLDER = arguments[0]));					
				}else{
					RESOURCE_FOLDER = file;
				}
				
				if(RESOURCE_FOLDER.endsWith(".jar")){
					isJar = true;
					
					JarFile jarFile = new JarFile(new File(RESOURCE_FOLDER));
					Enumeration<JarEntry> jes = jarFile.entries();
					while(jes.hasMoreElements()){
						JarEntry entry = jes.nextElement();
						if(entry.getName().startsWith(resourceDirectory) && !entry.getName().equals(resourceDirectory)){
							System.out.println("[DEBUG] Current Resource: "+entry.getName());
							resourceProcess.add(entry.getName());
						}
						
					}
					jarFile.close();
				}else{
					File files = new File(RESOURCE_FOLDER);
					for(File currentFile : files.listFiles()){
						System.out.println("[DEBUG] Current Resource: "+currentFile.getName());
						resourceProcess.add(currentFile.getAbsolutePath());
					}
				}
				System.out.println("[DEBUG] Resources processed, total "+resourceProcess.size());
				
				if(isJar){
					String home = System.getProperty("user.home");
					String installationDir = (arguments.length == 2 ? arguments[2] : new File(home, "LinuxBridge").getAbsolutePath());
					File installDir;
					if((installDir = new File(installationDir)).exists()){
						if(installDir.delete()){
							throw new RuntimeException("[ERROR] Cannot install in directory "+installationDir+", already exists and cannot be deleted!");
						}
					}
					if(!installDir.exists()){
						installDir.mkdirs();
					}
					System.out.println("================Installation Task================");
					System.out.println("Installing on "+installationDir+"...");
					properties.put(PROPERTIES.INSTALLATION_DIR, installationDir);
						
					for(int i = 0; i < resourceProcess.size(); ++i){
						String resource = resourceProcess.get(i);
						System.out.println("Saving Resource: "+resource);
						
						File save = new File(installDir, resource);

						if(save.getParentFile() != null){
							save.getParentFile().mkdirs();
						}

						if(!save.exists()){
							save.createNewFile();
						}
						
						InputStream is = LinuxBridgeMain.class.getResourceAsStream("/"+resource);

						
						Files.copy(is, Paths.get(save.toURI()), StandardCopyOption.REPLACE_EXISTING);
						resourceProcess.set(i, save.getAbsolutePath());
						System.out.println("Saved Resource: "+resource+"!");
					}
					System.out.println("Installation task finished!");
					System.out.println("================Installation Task================");
				}else{
					properties.put(PROPERTIES.INSTALLATION_DIR, arguments[0]);
				}
				
				properties.put(PROPERTIES.JAR_FILE, isJar);
				properties.put(PROPERTIES.RESOURCE_LIST, resourceProcess);
				
				runner = new DyRunner(this); 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
			throw new RuntimeException("Cannot call load... reflection?!");
		}
		
	}

	public static void main(String[] args) {
		
		arguments = args;
		LinuxBridgeMain linuxBridgeMain = LinuxBridgeMain.create();
		DyRunner runner = linuxBridgeMain.runner;
		Scanner scan = new Scanner(System.in);
		String next;
		runner.run("notify-send");
		/*while((next = scan.nextLine()) != null){
			if(next.equals("quit")){
				System.exit(0);
			}else{
				runner.run(next);
			}
		}*/
	}

}
