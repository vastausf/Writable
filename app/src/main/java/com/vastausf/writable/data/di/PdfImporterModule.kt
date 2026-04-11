package com.vastausf.writable.data.di

import android.content.Context
import com.vastausf.writable.data.pdfImporter.PdfImporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PdfImporterModule {
    @Provides
    @Singleton
    fun providePdfImporter(
        @ApplicationContext context: Context,
    ): PdfImporter {
        return PdfImporter(context)
    }
}
