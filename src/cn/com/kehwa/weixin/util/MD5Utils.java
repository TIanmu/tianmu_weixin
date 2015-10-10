package cn.com.kehwa.weixin.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 文件MD5值的计算方法
 * 
 */
public class MD5Utils {
	
	public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	public static final String MD5_HASH ="MD5";
	public static final String SHA1_HASH="SHA1";
	public static final String SHA_256_HASH="SHA-256";
	public static final String SHA_384_HASH="SHA-384";
	public static final String SHA_512_HASH="SHA-512";
	
	/**
	 * 得到文件的Hash值，可以为MD5,SHA1,SHA-256,SHA-384,SHA-512
	 * @param filePath
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String getFileHash(String filePath,String type) throws Exception{
		
		if(!new File(filePath).exists()){
			return null;
		}
		
		if(MD5_HASH.equals(type)){
			return getHash(filePath,MD5_HASH);
		}else if(SHA1_HASH.equals(type)){
			return getHash(filePath,SHA1_HASH);
		}else if(SHA_256_HASH.equals(type)){
			return getHash(filePath,SHA_256_HASH);
		}else if(SHA_384_HASH.equals(type)){
			return getHash(filePath,SHA_384_HASH);
		}else if(SHA_512_HASH.equals(type)){
			return getHash(filePath,SHA_512_HASH);
		}else{
			return null;
		}
	}
	
	
	/**
	 * 默认取文件Hash的MD5值
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String getFileHash(String filePath) throws Exception{
		return getHash(filePath,MD5_HASH);
	}
	
	private static String getHash(String fileName, String hashType)
			throws Exception {
		InputStream fis;
		fis = new FileInputStream(fileName);
		byte[] buffer = new byte[1024];
		MessageDigest md5 = MessageDigest.getInstance(hashType);
		int numRead = 0;
		while ((numRead = fis.read(buffer)) > 0) {
			md5.update(buffer, 0, numRead);
		}
		fis.close();
		return toHexString(md5.digest());
	}

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	
	/**
	 * 得到字符串的 MD5
	 * @param str
	 * @return
	 */
	public static String getMD5(String str){
		 MessageDigest digest = null;
		    ByteArrayInputStream in=null;
		    byte buffer[] = new byte[1024];
		    int len;
		    try {
				digest = MessageDigest.getInstance("MD5");
		       in = new ByteArrayInputStream(str.getBytes("utf-8"));
		      while ((len = in.read(buffer, 0, 1024)) != -1) {
		        digest.update(buffer, 0, len);
		      }
		      in.close();
		    } catch (Exception e) {
		      e.printStackTrace();
		      return null;
		    }
		    BigInteger bigInt = new BigInteger(1, digest.digest());
		    String _temp= bigInt.toString(16);
		    if(_temp.length()==31) return ("0"+_temp).toUpperCase();
		    else
		    	return _temp.toUpperCase();
	}
	
	
	public static String getFileMD5(File file) {
	    if (!file.isFile()){
	      return null;
	    }
	    MessageDigest digest = null;
	    FileInputStream in=null;
	    byte buffer[] = new byte[1024];
	    int len;
	    try {
			digest = MessageDigest.getInstance("MD5");
	       in = new FileInputStream(file);
	      while ((len = in.read(buffer, 0, 1024)) != -1) {
	        digest.update(buffer, 0, len);
	      }
	      in.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	      return null;
	    }
	    BigInteger bigInt = new BigInteger(1, digest.digest());
	    String _temp= bigInt.toString(16);
	    if(_temp.length()==31) return ("0"+_temp).toUpperCase();
	    else
	    	return _temp.toUpperCase();
	  }
	
	public static String MD5Encode(String origin, String charsetname) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception exception) {
		}
		return resultString;
	}
	
	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}
	
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return String.valueOf(hexChar[d1]) + String.valueOf(hexChar[d2]);
	}
}
