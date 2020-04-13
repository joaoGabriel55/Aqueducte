package br.imd.aqueducte.httpclientssl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;

public class HttpSSLClient {


    @Test(expected = SSLPeerUnverifiedException.class)
    public void whenHttpsUrlIsConsumed_thenException() throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String urlOverHttps = "https://www.globo.com/";
        HttpGet getMethod = new HttpGet(urlOverHttps);

        HttpResponse response = httpClient.execute(getMethod);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public final void givenAcceptingAllCertificates_whenHttpsUrlIsConsumed_thenOk()
            throws GeneralSecurityException, IOException {
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(connectionManager)
                .build();

        String urlOverHttps = "http://sgeolayers.imd.ufrn.br/sgeol-test-sec/";
        HttpGet getMethod = new HttpGet(urlOverHttps);
        HttpResponse response = httpClient.execute(getMethod);

        assertEquals(200, response.getStatusLine().getStatusCode());
    }
}
