package io.github.jonathanxd.JwUtils.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtils {
	
	public static class FileInternalData{
	
		public static void writeToFile(File f, byte[] b, boolean append) throws IOException{
			Files.write(Paths.get(f.toURI()), b, StandardOpenOption.CREATE, (append ? StandardOpenOption.APPEND : StandardOpenOption.WRITE));
		}
		
		public static byte[] readFromFile(File f) throws IOException{
			return Files.readAllBytes(Paths.get(f.toURI()));
		}
	}
	
	public static class FileExternalData{
		public static void renameFile(File f, File newFileName, boolean removeIfNewFileExists){
			if(newFileName.exists() && removeIfNewFileExists){
				FileFileSystem.fileDelete(newFileName);
			}
			f.renameTo(newFileName);
		}
	}
	
	public static class FileFileSystem{ //Delete, Move, Rename (FileExternalData hook) and related!
		public static FileOperation fileInUse(File f){
			try{
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				FileChannel fc = raf.getChannel();
				FileLock fl = fc.lock();
				boolean ok = false;
				try {
				    fl = fc.tryLock();
				    ok = true;
				    fl.release();
				} catch (OverlappingFileLockException e) {
					ok = false;
					fl.release();
				}
			    fc.close();
			    raf.close();
			    if(ok)return FileOperation.FILE_NOT_IN_USE;
			    else return FileOperation.FILE_IN_USE;
			}catch(Throwable t){
			    return FileOperation.ERROR;				
			}
		}
		
		public static FileOperation fileDelete(File f){
			if(!fileInUse(f).isStatePositive()) return FileOperation.FILE_IN_USE;
			if(f.delete()){
				return FileOperation.DELETE_OK;
			}else return FileOperation.DELETE_FAIL;
		}
		
		public static void fileRename(File f, File newFileName, boolean removeIfNewFileExists){
			FileExternalData.renameFile(f, newFileName, removeIfNewFileExists);
		}
				
	}	

}
