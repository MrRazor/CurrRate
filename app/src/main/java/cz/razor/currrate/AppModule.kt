package cz.razor.currrate

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import cz.razor.currrate.api.FrankfurterApi
import cz.razor.currrate.data.CurrencyInfo
import cz.razor.currrate.data.CurrencyRate
import cz.razor.currrate.data.MyObjectBox
import cz.razor.currrate.helpers.NotificationHelper
import cz.razor.currrate.helpers.NotificationSchedulerHelper
import cz.razor.currrate.repository.CurrencyInfoRepository
import cz.razor.currrate.repository.CurrencyRateRepository
import cz.razor.currrate.repository.SettingsRepository
import cz.razor.currrate.viewmodels.CurrencyDetailViewModel
import cz.razor.currrate.viewmodels.CurrencyGraphViewModel
import cz.razor.currrate.viewmodels.CurrencyListViewModel
import cz.razor.currrate.viewmodels.SettingsViewModel
import io.objectbox.BoxStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val repositoryModule = module {
    single { CurrencyInfoRepository(get(named("currencyInfoBox"))) }
    single { CurrencyRateRepository(get(named("currencyRateBox"))) }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("settings") }
        )
    }
    single { SettingsRepository(get())}
}

val viewModelModule = module {
    viewModel { CurrencyListViewModel(get(), get(), get(), get()) }
    viewModel { CurrencyDetailViewModel(get(), get(), get(), get()) }
    viewModel { CurrencyGraphViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
}

val imageModule = module {
    single { provideImageLoader(androidContext()) }
}

val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideFrankfurterApi(get()) }
}

val objectBoxModule = module {
    single {
        MyObjectBox.builder()
            .androidContext(androidContext())
            .build()
    }
    single(named("currencyInfoBox")) { get<BoxStore>().boxFor(CurrencyInfo::class.java) }
    single(named("currencyRateBox")) { get<BoxStore>().boxFor(CurrencyRate::class.java) }
}

val helperModule = module {
    single { NotificationHelper(androidContext()) }
    single { NotificationSchedulerHelper(androidContext()) }
}

fun provideImageLoader(androidContext: Context): ImageLoader {
    return ImageLoader.Builder(androidContext)
        .memoryCache {
            MemoryCache.Builder(androidContext)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(androidContext.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()
}

fun provideOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.frankfurter.dev/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideFrankfurterApi(retrofit: Retrofit): FrankfurterApi {
    return retrofit.create(FrankfurterApi::class.java)
}