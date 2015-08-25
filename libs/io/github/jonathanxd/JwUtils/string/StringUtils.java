package io.github.jonathanxd.JwUtils.string;

import java.nio.charset.Charset;

public class StringUtils {
	
	public static String stringFromBytes(byte[] b){
		return new String(b);
	}

	public static String stringFromBytes(byte[] b, Charset charset){
		return new String(b, charset);
	}

}
