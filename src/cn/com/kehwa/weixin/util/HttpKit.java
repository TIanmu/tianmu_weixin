package cn.com.kehwa.weixin.util;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLContexts;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;

import cn.com.kehwa.weixin.message.bean.Attachment;

import com.ning.http.client.Response;

/**
 * https 请求 微信为https的请求
 */ 
public class HttpKit {
	private static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * @return 返回类型:
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @description 功能描述: get 请求
     */
    public static String get(String url, Map<String, String> params, Map<String, String> headers) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.prepareGet(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                builder.addQueryParameter(key, params.get(key));
            }
        }

        if (headers != null && !headers.isEmpty()) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                builder.addHeader(key, params.get(key));
            }
        }
        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }

    /**
     * @return 返回类型:
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @description 功能描述: get 请求
     */
    public static String get(String url) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, IOException, ExecutionException, InterruptedException {
        return get(url, null);
    }

    /**
     * @return 返回类型:
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws UnsupportedEncodingException
     * @description 功能描述: get 请求
     */
    public static String get(String url, Map<String, String> params) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, IOException, ExecutionException, InterruptedException {
        return get(url, params, null);
    }

    /**
     * @return 返回类型:
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @description 功能描述: POST 请求
     */
    public static String post(String url, Map<String, String> params) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        builder.setHeader("Charsert", DEFAULT_CHARSET);
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                builder.addParameter(key, params.get(key));
            }
        }
        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }

    /**
     * 上传媒体文件
     *
     * @param url
     * @param file
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws KeyManagementException
     */
    public static String upload(String url, File file) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        String BOUNDARY = "----WebKitFormBoundaryiDGnV9zdZA1eM1yL"; // 定义数据分隔线
        builder.setHeader("connection", "Keep-Alive");
        builder.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
        builder.setHeader("Charsert", "UTF-8");
        builder.setHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
        builder.setBody(new UploadEntityWriter(end_data, file));

        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }

    /**
     * 下载资源
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static Attachment download(String url) throws ExecutionException, InterruptedException, IOException {
        Attachment att = new Attachment();
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.prepareGet(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        Future<Response> f = builder.execute();
        if (f.get().getContentType().equalsIgnoreCase("text/plain")) {
            att.setError(f.get().getResponseBody(DEFAULT_CHARSET));
        } else {
            BufferedInputStream bis = new BufferedInputStream(f.get().getResponseBodyAsStream());
            String ds = f.get().getHeader("Content-disposition");
            String fullName = ds.substring(ds.indexOf("filename=\"") + 10, ds.length() - 1);
            String relName = fullName.substring(0, fullName.lastIndexOf("."));
            String suffix = fullName.substring(relName.length() + 1);

            att.setFullName(fullName);
            att.setFileName(relName);
            att.setSuffix(suffix);
            att.setContentLength(f.get().getHeader("Content-Length"));
            att.setContentType(f.get().getContentType());
            att.setFileStream(bis);
        }
        http.close();
        return att;
    }

    /**
     * post提交，一般用于提交xml数据
     * @param url
     * @param s
     * @return
     */
    public static String post(String url, String s) {
        AsyncHttpClient http = null;
		String body = "";
		try {
			http = new AsyncHttpClient();
			AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
			builder.setBodyEncoding(DEFAULT_CHARSET);
			builder.setBody(s);
			Future<Response> f = builder.execute();
			body = f.get().getResponseBody(DEFAULT_CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			if (http != null) {
				 http.close();
			}
		}
        return body;
    }
    
    /**
     * post提交，一般用于提交xml数据
     * @param url
     * @param s
     * @param filePath 证书地址
     * @param filePath 证书密码
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static String post(String url, String s, String filePath, String password) throws IOException, ExecutionException, InterruptedException {
    	String body;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
	        FileInputStream instream = new FileInputStream(new File(filePath));//加载本地的证书进行https加密传输
	        try {
	            keyStore.load(instream, password.toCharArray());//设置证书密码
	        } catch (CertificateException e) {
	            e.printStackTrace();
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } finally {
	            instream.close();
	        }
			
			// Trust own CA and all self-signed certs
	        SSLContext sslContext = SSLContexts.custom()
	                .loadKeyMaterial(keyStore, password.toCharArray())
	                .build();
			
			Builder builder2 = new AsyncHttpClientConfig.Builder();
			builder2.setSSLContext(sslContext);
			AsyncHttpClient http = new AsyncHttpClient(builder2.build());
			AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
			builder.setBodyEncoding(DEFAULT_CHARSET);
			builder.setBody(s);
			Future<Response> f = builder.execute();
			body = f.get().getResponseBody(DEFAULT_CHARSET);
			http.close();
			return body;
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        return null;
    }
    
}