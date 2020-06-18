package com.tohsoft.app.data.network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tohsoft.app.data.models.MoreApps;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Phong on 11/9/2016.
 */

public interface RemoteApiService {

    @GET("moreapp.php")
    Single<MoreApps> moreApps(@QueryMap Map<String, String> params);


    class Creator {
        private static final String ENDPOINT = "http://linkapp.tohapp.com/";

        public static Retrofit newRetrofitInstance() {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS);
            /**
             * Need if ENDPOINT is https.
             */
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA)
                        .build();
                builder.connectionSpecs(Collections.singletonList(spec));
            }*/

            OkHttpClient client = builder.build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:SSS'Z'")
                    .create();

            return new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

        }

    }
}
