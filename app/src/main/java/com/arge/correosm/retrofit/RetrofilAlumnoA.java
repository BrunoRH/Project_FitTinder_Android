package com.arge.correosm.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofilAlumnoA {



    public  static Retrofit getAlumnoA(String url){


        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        return  retrofit;
    }

    public  static Retrofit getAlumnoAobject(String url){


        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        return  retrofit;
    }
}
