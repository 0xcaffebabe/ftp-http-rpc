package wang.ismy.fttp.endpoint.util;


import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cjiping@linewell.com
 */
public class HttpClientFactory {
	private static final Logger log = LoggerFactory.getLogger(HttpClientFactory.class);
    private static final Integer MAX_TOTAL = 300;             //连接池最大连接数
    private static final Integer MAX_PER_ROUTE = 50;          //单个路由默认最大连接数
    private static final Integer REQ_TIMEOUT =  60 * 1000;     //请求超时时间ms
    private static final Integer CONN_TIMEOUT = 60 * 1000;     //连接超时时间ms
    private static final Integer SOCK_TIMEOUT = 60 * 1000;    //读取超时时间ms
    
 
	public static HttpClient createSimpleHttpClient(){
		SSLConnectionSocketFactory sf = SSLConnectionSocketFactory.getSocketFactory();
		return HttpClientBuilder.create()
		        .setSSLSocketFactory(sf)
		        .build();
    }
    
	public static CloseableHttpClient createHttpClient() {
		return defaultHttpClientBuilder().build();
	}

	public static HttpClientBuilder defaultHttpClientBuilder(){
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		poolingHttpClientConnectionManager.setMaxTotal(MAX_TOTAL);
		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(REQ_TIMEOUT)
				.setConnectTimeout(CONN_TIMEOUT).setSocketTimeout(SOCK_TIMEOUT)
				.setRedirectsEnabled(false)//设置不自动重定向
				.build();
		return HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).setDefaultRequestConfig(requestConfig);
	}
}