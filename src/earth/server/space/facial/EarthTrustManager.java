package earth.server.space.facial;

/**
 * Created by God on 2017/2/11.
 */
import earth.server.Monitor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
public class EarthTrustManager implements X509TrustManager {
    /*
     * The default X509TrustManager returned by SunX509.  We'll delegate
     * decisions to it, and fall back to the logic in this class if the
     * default X509TrustManager doesn't trust it.
     */

    /*
     * Delegate to the default trust manager.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {

    }
    /*
     * Delegate to the default trust manager.
     */
    @Override
    public void checkServerTrusted(
            java.security.cert.X509Certificate[] certs, String authType)
            throws CertificateException {
        InputStream inStream = null;
        try {
            // Loading the CA cert
            URL u = getClass().getResource("dstca.cer");
            inStream = new FileInputStream(u.getFile());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate ca = (X509Certificate) cf.generateCertificate(inStream);
            inStream.close();
            //if(certs[0].getSignature().equals(ca.getSignature()))
            for (int i = 0; i < certs.length ; i++) {
                X509Certificate cert =  certs[i];
                // Verifing by public key
                try{
                    cert.verify(certs[i+1].getPublicKey());
                }catch (Exception e) {
                    cert.verify(ca.getPublicKey());
                }
            }
        } catch (Exception ex) {
            Monitor.logger(ex.getLocalizedMessage());
            ex.printStackTrace();
            throw new CertificateException(ex);
        } finally {
            try {
                inStream.close();
            } catch (IOException ex) {
                Monitor.logger(ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }

    }
    /*
     * Merely pass this through.
     */
    public X509Certificate[] getAcceptedIssuers() {
        //return sunJSSEX509TrustManager.getAcceptedIssuers();
        return null;
    }
}