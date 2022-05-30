package com.common.library.frame.di.module;

import com.common.library.frame.data.DataRepository;
import com.common.library.frame.data.IDataRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 *
 */
@InstallIn(SingletonComponent.class)
@Module
public abstract class RepositoryModule {

    @Binds
    abstract IDataRepository bindDataRepository(DataRepository dataRepository);
}
