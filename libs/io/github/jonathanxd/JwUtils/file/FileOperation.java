package io.github.jonathanxd.JwUtils.file;

public enum FileOperation {
	FILE_WRITE_FAIL,
	FILE_WRITE_OK,
	FILE_READ_FAIL,
	FILE_READ_OK,
	FILE_IN_USE,
	FILE_NOT_IN_USE,
	DELETE_FAIL,
	DELETE_OK,
	ERROR;
	
	public boolean isStatePositive(){
		if(this.name().contains("OK")) return true;
		if(this.name().contains("FAIL")) return false;
		if(this == ERROR) return false;
		if(this == FILE_IN_USE) return false;
		if(this == FILE_NOT_IN_USE) return true;
		
		return false;
	}
	
}
