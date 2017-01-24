package com.snail.common.http.client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.SSLContextBuilder;

public class HttpUtils {
	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				//信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			CookieStore cookieStore = new BasicCookieStore();
			cookieStore.addCookie(newCookie("MM_WX_NOTIFY_STATE", "1", "wx2.qq.com", "/"));

			cookieStore.addCookie(newCookie("MM_WX_SOUND_STATE", "1", "wx2.qq.com", "/"));

			cookieStore.addCookie(newCookie("RK", "vVnriPfqW5", ".qq.com", "/"));

			cookieStore.addCookie(newCookie("_ga", "GA1.2.1954057662.1473405828", ".qq.com", "/"));

			cookieStore.addCookie(newCookie("last_wxuin", "2418338529", "wx2.qq.com", "/"));

			cookieStore.addCookie(newCookie("login_frequency", "1", "wx2.qq.com", "/"));

			cookieStore.addCookie(newCookie("mm_lang", "zh_CN", "wx2.qq.com", "/"));
			

			cookieStore.addCookie(newCookie("mobileUV", "1_157fe801a97_551b4",".qq.com","/"));

			cookieStore.addCookie(newCookie("o_cookie", "362657746",".qq.com","/"));

			cookieStore.addCookie(newCookie("pac_uid", "1_362657746",".qq.com","/"));

			cookieStore.addCookie(newCookie("pgv_info", "ssid=s4608462076",".qq.com","/"));

			cookieStore.addCookie(newCookie("pgv_pvi", "9281121280","qq.com","/"));

			cookieStore.addCookie(newCookie("pgv_pvid", "8198614911",".qq.com","/"));

			cookieStore.addCookie(newCookie("pgv_si", "s5226785792",".qq.com","/"));

			cookieStore.addCookie(newCookie("pt2gguin", "o0362657746",".qq.com","/"));

			cookieStore.addCookie(newCookie("ptcz", "fce4c0ff0aef1ffbc527b21abf2b3f1e9d3f06b8bf08859dfff2664d86006d63",".qq.com","/"));

			cookieStore.addCookie(newCookie("ptisp", "ctc",".qq.com","/"));

			cookieStore.addCookie(newCookie("skey", "@eMMkAbNwp",".qq.com","/"));

			cookieStore.addCookie(newCookie("tvfe_boss_uuid", "83d3f47782353b69",".qq.com","/"));

			cookieStore.addCookie(newCookie("uin", "o0362657746",".qq.com","/"));

			cookieStore.addCookie(newCookie("webwx_auth_ticket", "CIsBEP/OrP0KGoABbFcii/nFEx2QfDTuQmTxJp/YuhwkH3jTBrd61o4p+dfOAFMOpev/4oaniJuSUy5jhqluy1ZPmd8gNWWyjxdZO7ck6BaYR1ggvJxnt9kjcfppB2tIsEkcc2K4ZIM2qtDSv4EaZ69b7uL7bF/WBbcpLVkNXA8CTG6ojqPTC2WUEHU=","wx2.qq.com","/"));

			cookieStore.addCookie(newCookie("webwx_data_ticket", "gScZ9VqKLcrXAqK2IQCrxLH0",".qq.com","/"));

			cookieStore.addCookie(newCookie("webwxuvid", "4abda904b1648bbc29272104beea6bd17796221e1e61cd21134602f4cd51a1a112461ec72201569ed8ac289efa99fc3d","wx2.qq.com","/"));

			cookieStore.addCookie(newCookie("wxloadtime", "1485255450_expired","wx2.qq.com","/"));

			cookieStore.addCookie(newCookie("wxpluginkey", "1485252362","wx2.qq.com","/"));

			cookieStore.addCookie(newCookie("wxsid", "xuhxD1tgs9XeoP1h","wx2.qq.com","/"));

			cookieStore.addCookie(newCookie("wxuin", "2418338529","wx2.qq.com","/"));
			
			return HttpClients.custom().setDefaultCookieStore(cookieStore).setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	public static void main(String[] args) {
		CloseableHttpClient httpClient = createSSLClientDefault();
		
		HttpGet get = new HttpGet();
		try {
			get.setURI(new URI("https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxgetmsgimg?&MsgID=3720859072061269005&skey=%40crypt_ef9f8f62_78a7ecfae32bb73d534e75bba6beda98&type=slave"));
			CloseableHttpResponse response = httpClient.execute(get);
			OutputStream outstream = new FileOutputStream("d:/tmp/zzz.png");

			response.getEntity().writeTo(outstream);
			System.out.println(response.getEntity().getContentType());
			System.out.println(response.getEntity().getContentLength());
			
			response.close();
			outstream.close();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BasicClientCookie newCookie(String name, String value, String domain, String path ) {
		BasicClientCookie result = new BasicClientCookie(name, value);
		result.setDomain(domain);
		result.setPath(path);
		return result;
	}
}
