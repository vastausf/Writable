package com.vastausf.writable.data.di

import android.content.Context
import com.vastausf.writable.data.pdfImporter.AndroidPdfPageRenderer
import com.vastausf.writable.data.pdfImporter.AppFileStorage
import com.vastausf.writable.data.pdfImporter.FileStorage
import com.vastausf.writable.data.pdfImporter.PdfImporter
import com.vastausf.writable.data.pdfImporter.PdfPageRenderer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PdfImporterModule {
    @Singleton
    @Provides
    fun providePdfImporter(
        @ApplicationContext
        context: Context,
        fileStorage: FileStorage,
        pdfPageRenderer: PdfPageRenderer,
    ): PdfImporter {
        return PdfImporter(context, fileStorage, pdfPageRenderer)
    }

    @Singleton
    @Provides
    fun provideFileStore(
        @ApplicationContext
        context: Context,
    ): FileStorage {
        return AppFileStorage(context)
    }

    @Singleton
    @Provides
    fun providePdfPageRenderer(
        @ApplicationContext
        context: Context,
    ): PdfPageRenderer {
        return AndroidPdfPageRenderer(context)
    }
}
