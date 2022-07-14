package com.common.library.frame.config;

import android.content.Context;

import androidx.room.RoomDatabase;

import com.common.library.frame.di.module.ConfigModule;
import com.common.library.frame.http.InterceptorConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;


/**
 * 为框架提供一些配置参数入口
 */
public interface AppliesOptions {

    /**
     * 为框架提供一些配置参数入口
     *
     * @param context context
     * @param builder builder
     */
    void applyOptions(Context context, ConfigModule.Builder builder);

    /**
     * 为框架中的{@link Retrofit}提供配置参数入口
     */
    interface RetrofitOptions {
        void applyOptions(Retrofit.Builder builder);
    }

    /**
     * 为框架中的{@link OkHttpClient}提供配置参数入口
     */
    interface OkHttpClientOptions {
        void applyOptions(OkHttpClient.Builder builder);
    }

    /**
     * 为框架中的{@link Gson}提供配置参数入口
     */
    interface GsonOptions {
        void applyOptions(GsonBuilder builder);
    }

    /**
     * 为框架中的{@link InterceptorConfig}提供配置参数入口
     */
    interface InterceptorConfigOptions {
        void applyOptions(InterceptorConfig.Builder builder);
    }

    /**
     * 为框架中的{@link RoomDatabase}提供配置参数入口
     */
    interface RoomDatabaseOptions<T extends RoomDatabase> {
        void applyOptions(RoomDatabase.Builder<T> builder);
    }

}
