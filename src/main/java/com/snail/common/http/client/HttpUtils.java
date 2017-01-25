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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
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
			HttpHost proxy = new HttpHost("127.0.0.1", 8888);
			RequestConfig config = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).setProxy(proxy).build();
			String cookieString = "tvfe_boss_uuid=83d3f47782353b69; _ga=GA1.2.1954057662.1473405828; mobileUV=1_157fe801a97_551b4; webwxuvid=4abda904b1648bbc29272104beea6bd17796221e1e61cd21134602f4cd51a1a112461ec72201569ed8ac289efa99fc3d; pac_uid=1_362657746; pgv_pvi=9281121280; pgv_si=s5226785792; pt2gguin=o0362657746; uin=o0362657746; skey=@eMMkAbNwp; ptisp=ctc; RK=vVnriPfqW5; ptcz=fce4c0ff0aef1ffbc527b21abf2b3f1e9d3f06b8bf08859dfff2664d86006d63; pgv_info=ssid=s4608462076; pgv_pvid=8198614911; o_cookie=362657746; MM_WX_NOTIFY_STATE=1; MM_WX_SOUND_STATE=1; webwx_auth_ticket=CIsBEP/OrP0KGoABbFcii/nFEx2QfDTuQmTxJp/YuhwkH3jTBrd61o4p+dfOAFMOpev/4oaniJuSUy5jhqluy1ZPmd8gNWWyjxdZO7ck6BaYR1ggvJxnt9kjcfppB2tIsEkcc2K4ZIM2qtDSv4EaZ69b7uL7bF/WBbcpLVkNXA8CTG6ojqPTC2WUEHU=; login_frequency=1; last_wxuin=2418338529; wxpluginkey=1485304988; wxuin=2418338529; wxsid=xuhxD1tgs9XeoP1h; webwx_data_ticket=gScZ9VqKLcrXAqK2IQCrxLH0";
			List<BasicClientCookie> cookies = generateCookieListByString(cookieString);
			for ( BasicClientCookie cookie : cookies ) {
				cookieStore.addCookie(cookie);
			}

			return HttpClients.custom().setDefaultRequestConfig(config).setDefaultCookieStore(cookieStore).setSSLSocketFactory(sslsf).build();
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
			get.setHeader("Accept", "image/webp,image/*,*/*;q=0.8");
			get.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
			get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
			get.setHeader("Referer", "https://wx2.qq.com/?&lang=zh_CN");

			get.setURI(new URI("https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxgetmsgimg?&MsgID=7074751255620380250&skey=%40crypt_ef9f8f62_78a7ecfae32bb73d534e75bba6beda98"));
			CloseableHttpResponse response = httpClient.execute(get);
			OutputStream outstream = new FileOutputStream("d:/tmp/zzz.png");

			response.getEntity().writeTo(outstream);
			System.out.println(response.getStatusLine());
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

	public static BasicClientCookie newCookie(String name, String value, String domain, String path) {
		BasicClientCookie result = new BasicClientCookie(name, value);
		result.setDomain(domain);
		result.setPath(path);
		return result;
	}

	public static List<BasicClientCookie> generateCookieListByString(String allCookie) {
		String[] pieces = allCookie.split(";");
		List<BasicClientCookie> result = new ArrayList<BasicClientCookie>();

		for (String piece : pieces) {
			piece = piece.trim();
			if (piece.length() > 0) {
				String[] cookiePieces = piece.split("=");
				if (cookiePieces.length == 2) {
					result.add(newCookie(cookiePieces[0], cookiePieces[1], "wx2.qq.com", "/"));
				}
			}
		}

		return result;
	}
}
