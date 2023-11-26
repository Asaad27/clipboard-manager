package di


import dao.ClipboardDao
import dao.IClipboardDao
import database.DriverManager
import org.koin.dsl.module
import repository.ClipboardRepository
import repository.WindowsSystemClipboardRepository
import repository.interfaces.IClipboardRepository
import repository.interfaces.ISystemClipboardRepository
import services.ClipboardService
import services.WindowsSystemClipboardService
import services.interfaces.IClipboardService
import services.interfaces.ISystemClipboardService
import viewmodel.ClipboardViewModel


val appModule = module {
    single<DriverManager> { DriverManager() }
    single<IClipboardDao> { ClipboardDao(get()) }

    single<IClipboardRepository> { ClipboardRepository(get()) }
    single<ISystemClipboardRepository> { WindowsSystemClipboardRepository() }

    single<ISystemClipboardService> { WindowsSystemClipboardService(get()) }
    single<IClipboardService> { ClipboardService(get()) }

    single<ClipboardViewModel> { ClipboardViewModel(get(), get()) }
}
