package uk.staygrounded.httpstubby.server.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm;

public class SelfSignedSSLContextFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SelfSignedSSLContextFactory.class);

    public SSLContext createContext(String keyStorePath, String keyStorePassword) {
        try {
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(getDefaultAlgorithm());
            keyManagerFactory.init(loadKeystore(keyStorePath, keyStorePassword), keyStorePassword.toCharArray());

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustAllTrustManager(), null);

            return sslContext;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private KeyStore loadKeystore(String keyStorePath, String keyStorePassword) throws KeyStoreException {
        try {
            final KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(this.getClass().getResourceAsStream(keyStorePath), keyStorePassword.toCharArray());
            return keyStore;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            throw new RuntimeException("Configuration Error: Can't load JKS keystore from file " + keyStorePath);
        }
    }

    private TrustManager[] trustAllTrustManager() {
        return new TrustManager[]{new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
    }

}
