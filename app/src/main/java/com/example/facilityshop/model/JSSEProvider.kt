package com.example.sendmail

import java.security.AccessController
import java.security.PrivilegedAction
import java.security.Provider

class JSSEProvider(name: String?, version: Double, info: String?) : Provider(name, version, info) {

    fun access() {
        AccessController.doPrivileged(PrivilegedAction<Void> {
            put(
                "SSLContext.TLS",
                "org.apache.harmony.xnet.provider.jsse.SSLContextImpl"
            )
            put("Alg.Alias.SSLContext.TLSv1", "TLS")
            put(
                "KeyManagerFactory.X509",
                "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl"
            )
            put(
                "TrustManagerFactory.X509",
                "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl"
            )
            null
        })
    }

}