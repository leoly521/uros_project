package com.bamboocloud.uros.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import com.bamboocloud.uros.common.exception.UrosProtocolException;
import com.bamboocloud.uros.io.ByteBufferStream;
import com.bamboocloud.uros.io.UrosHelper;
import com.bamboocloud.uros.io.UrosMode;

public class UrosHttpClient extends UrosClient {
	private final ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<String, String>();
	private static boolean disableGlobalCookie = false;
	private static CookieManager globalCookieManager = new CookieManager();
	private CookieManager cookieManager = disableGlobalCookie ? new CookieManager() : globalCookieManager;
	private String proxyHost = null;
	private int proxyPort = 80;
	private String proxyUser = null;
	private String proxyPass = null;
	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT; // millisecond
	private int readTimeout = DEFAULT_READ_TIMEOUT; // millisecond
	private int idleTimeout = DEFAULT_IDLE_TIMEOUT; // millisecond
	private HostnameVerifier hv = null;
	private SSLSocketFactory sslsf = null;

	public static void setDisableGlobalCookie(boolean disableGlobalCookie) {
		UrosHttpClient.disableGlobalCookie = disableGlobalCookie;
	}

	public static boolean isDisableGlobalCookie() {
		return UrosHttpClient.disableGlobalCookie;
	}

	public UrosHttpClient() {
		super();
	}

	public UrosHttpClient(String uri) {
		super(uri.split(","));
	}

	public UrosHttpClient(UrosMode mode) {
		super(mode);
	}

	public UrosHttpClient(String uri, UrosMode mode) {
		super(uri.split(","), mode);
	}

	public static UrosClient create(String uri, UrosMode mode) throws IOException, URISyntaxException {
		String scheme = (new URI(uri)).getScheme().toLowerCase();
		if (!scheme.equals("http") && !scheme.equals("https")) {
			throw new UrosProtocolException("This client doesn't support " + scheme + " scheme.");
		}
		return new UrosHttpClient(uri, mode);
	}

	public void setHeader(String name, String value) {
		String nl = name.toLowerCase();
		if (!nl.equals("content-type") && !nl.equals("content-length") && !nl.equals("connection") && !nl.equals("keep-alive") && !nl.equals("host")) {
			if (value == null) {
				this.headers.remove(name);
			} else {
				this.headers.put(name, value);
			}
		}
	}

	public String getHeader(String name) {
		return this.headers.get(name);
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUser() {
		return this.proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPass() {
		return this.proxyPass;
	}

	public void setProxyPass(String proxyPass) {
		this.proxyPass = proxyPass;
	}

	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return this.readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public HostnameVerifier getHostnameVerifier() {
		return this.hv;
	}

	public void setHostnameVerifier(HostnameVerifier hv) {
		this.hv = hv;
	}

	public SSLSocketFactory getSSLSocketFactory() {
		return this.sslsf;
	}

	public void setSSLSocketFactory(SSLSocketFactory sslsf) {
		this.sslsf = sslsf;
	}
	
	private HttpURLConnection openConnection(int index) throws IOException {
		HttpURLConnection conn = null;
		String uri = null;
		if (index <= 0) {
			index = -1;
			uri = this.uris[this.lastUriIndex];
		} else {
			uri = this.uris[index];
		}
		try {
			URL url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(this.connectTimeout > 200 && this.connectTimeout <= 5000 ? this.connectTimeout : 3000);
			conn.connect();
			conn.disconnect();
		} catch (IOException e) {
			index++;
			while(index == this.lastUriIndex) {
				index++;
			}
			if (this.uris.length == 1 || this.uris.length <= index) {
				throw e;
			}
			conn = this.openConnection(index);
		}
		if (index >= 0) this.lastUriIndex = index;

		return conn;
	}

	@Override
	protected ByteBufferStream sendAndReceive(ByteBufferStream stream) throws IOException {
		Properties prop = System.getProperties();
		boolean keepAlive = this.idleTimeout > 0;
		prop.put("http.keepAlive", keepAlive);
		if (this.proxyHost != null) {
			prop.put("http.proxyHost", proxyHost);
			prop.put("http.proxyPort", Integer.toString(this.proxyPort));
		} else {
			prop.remove("http.proxyHost");
			prop.remove("http.proxyPort");
		}
		HttpURLConnection conn = this.openConnection(-1);
		URL url = conn.getURL();
		if (url.getProtocol().equals("https")) {
			if (this.hv != null)
				((HttpsURLConnection) conn).setHostnameVerifier(this.hv);
			if (this.sslsf != null)
				((HttpsURLConnection) conn).setSSLSocketFactory(this.sslsf);
		}
		if (this.connectTimeout > 0) conn.setConnectTimeout(this.connectTimeout);
		if (this.readTimeout > 0) conn.setReadTimeout(this.readTimeout);
		conn.setRequestProperty("Cookie", this.cookieManager.getCookie(url.getHost(), url.getFile(), url.getProtocol().equals("https")));
		if (keepAlive) {
			conn.setRequestProperty("Keep-Alive", Integer.toString(this.idleTimeout / 1000));
		}
		if (this.proxyUser != null && this.proxyPass != null) {
			conn.setRequestProperty("Proxy-Authorization", "Basic " + UrosHelper.base64Encode((proxyUser + ":" + proxyPass).getBytes()));
		}
		for (Entry<String, String> entry : this.headers.entrySet()) {
			conn.setRequestProperty(entry.getKey(), entry.getValue());
		}
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/uros");
		conn.setFixedLengthStreamingMode(stream.buffer.limit());
		OutputStream os = null;
		try {
			os = conn.getOutputStream();
			stream.writeTo(os);
			os.flush();
		} finally {
			if (os != null)
				os.close();
		}
		List<String> cookieList = new ArrayList<String>();
		int i = 1;
		String key;
		while ((key = conn.getHeaderFieldKey(i)) != null) {
			if (key.equalsIgnoreCase("set-cookie") || key.equalsIgnoreCase("set-cookie2")) {
				cookieList.add(conn.getHeaderField(i));
			}
			++i;
		}
		this.cookieManager.setCookie(cookieList, url.getHost());
		InputStream is = null;
		try {
			is = conn.getInputStream();
			stream.buffer.clear();
			stream.readFrom(is);
			return stream;
		} catch (IOException e) {
			InputStream es = null;
			try {
				es = conn.getErrorStream();
				if (es != null) {
					stream.buffer.clear();
					stream.readFrom(es);
					return stream;
				} else {
					throw e;
				}
			} finally {
				if (es != null)
					es.close();
			}
		} finally {
			if (is != null)
				is.close();
		}
	}
}
