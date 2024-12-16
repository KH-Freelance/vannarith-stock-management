package com.hfsolution.app.config.restclient;


import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.Client;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

public abstract class BaseRestClientConfig {

    // @Value("${feign.allow-host-names.rest}")
    // private String allowHostNames;

    // @Bean
	// Client feignClient() {
	// 	return new Client.Default(getSSLSocketFactory(), getHostnameVerifier(allowHostNames));
	// }

    @Bean
    Client feignClient() {
        return new Client.Default(getSSLSocketFactory(), getHostnameVerifier());
    }

  @Bean
  Encoder hsmFeignEncoder() {
    return new GsonEncoder();
  }

  @Bean
  Decoder hsmFeignDecoder() {
    return new GsonDecoder();
  }

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

    protected static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = { new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        }
        };
        return trustAllCerts;
    }

    protected static HostnameVerifier getHostnameVerifier(String allowHostNames) {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostName, SSLSession sslSession) {
                boolean isValidHostName = allowHostNames.contains(hostName);
                if (isValidHostName) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        return hostnameVerifier;
    }

    private static HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
          @Override
          public boolean verify(String s, SSLSession sslSession) {
            return true;
          }
        };
        return hostnameVerifier;
      }
}
