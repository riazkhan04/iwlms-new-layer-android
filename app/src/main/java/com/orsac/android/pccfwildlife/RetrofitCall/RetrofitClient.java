package com.orsac.android.pccfwildlife.RetrofitCall;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    public static String MAIN_URL="http://164.164.122.69:8081";//live url
    public static String IMAGE_URL="http://wildlife.odisha.gov.in/assets/javaupload/";//for image

//    public static String MAIN_URL="http://wildlife.odisha.gov.in:8081/wildlife/api/v1/";//live url
    //Live Server---
//    public static String IMAGE_URL="http://164.164.122.69:4200/assets/javaupload/";

    //Test Server------
//    public static String IMAGE_URL="http://164.164.122.69:4200/assets/javaupload/";
    public static String BASE_URL1=MAIN_URL+"/wildlife/api/v1/masters/";
    public static String BASE_URL_SIGNIN=MAIN_URL+"/wildlife/api/v1/auth/";
    public static String BASE_URL_REPORTS=MAIN_URL+"/wildlife/api/v1/reports/";//reports changes to auth for now
    public static String BASE_URL_New_Upload=MAIN_URL+"/wildlife/api/v1/uploadController/";
    public static String BASE_URL_GAJA_BANDHU=MAIN_URL+"/wildlife/api/v1/gajasathiController/";
    public static String BASE_URL_GAJA_BANDHU_PROFILE=MAIN_URL+"/wildlife/api/v1/uploadController/";
    public static String BASE_URL_INCIDENT_REPORTS=MAIN_URL+"/wildlife/api/v1/IncidentReport/";//reports changes fot incident report
    public static String Map_URL="https://odishaforestgis.in/ofms/index.php/webservice_api/";//


    static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS);

    public static Retrofit getClient(String is_signin_type) {

        if (is_signin_type.equalsIgnoreCase("")){

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL1)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        else {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_SIGNIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit;
    }
    public static Retrofit getReportClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_REPORTS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }

    public static Retrofit getNewFileUploadClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_New_Upload)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }
    public static Retrofit getNewGajaBandhuProfileUpload() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_GAJA_BANDHU_PROFILE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }
    public static Retrofit getIncidentClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_INCIDENT_REPORTS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }

    public static Retrofit getGajaBandhuRequestClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_GAJA_BANDHU)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }
    public static Retrofit getMapRequestClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(Map_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }
}
