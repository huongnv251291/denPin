package com.tohsoft.app.ui.history

import androidx.arch.core.util.Function
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.tohsoft.app.BaseApplication
import com.tohsoft.app.data.local.db.AppDatabase
import com.tohsoft.app.data.local.db.HistoryDao
import com.tohsoft.app.data.models.History
import com.utility.DebugLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

class HistoryViewModel : ViewModel() {
    private val mProgress = MutableLiveData<Boolean>()
    private val mQueryParams = MediatorLiveData<Boolean>()
    private val mSearchString = MutableLiveData<String>()

    private val mPublishProcess = PublishProcessor.create<String>()
    private val mCompositeDisposable = CompositeDisposable()
    private val mHistoryDao: HistoryDao = AppDatabase.getInstance(BaseApplication.instance!!).historyDao()

    init {
        mProgress.value = false

        val disposable = mPublishProcess.debounce(600, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mSearchString.value = it
                }

        mCompositeDisposable.add(disposable)
    }

    val histories: LiveData<List<History>> = liveData {
        val historyLiveData = mHistoryDao.getHistories()
        emitSource(historyLiveData)
    }

    val historiesPaged = mHistoryDao.getHistoriesPaging().toLiveData(20)

    val historiesSearchPaged = getSearchHistoryLiveData()

    val progressState: LiveData<Boolean>
        get() = mProgress

    // Do something
    fun initData() {
        mProgress.value = true
        viewModelScope.launch {
            insertTempItem()
            mProgress.value = false
        }
    }

    fun searchHistory(searchString: String?) {
        mPublishProcess.onNext(searchString)
    }

    private fun getSearchHistoryLiveData(): LiveData<PagedList<History>> {
        mQueryParams.addSource(mSearchString) {
            mQueryParams.value = true
        }

        return Transformations.switchMap(mQueryParams, Function {
            if (mSearchString.value == null) return@Function null

            return@Function mHistoryDao.searchHistoryWithTitle(mSearchString.value!!).toLiveData(20)
        })
    }

    private suspend fun insertTempItem() {
        withContext(Dispatchers.Default) {
            val histories = mutableListOf<History>()
            for (i in 0..10000) {
                val title = "History item $i"
                val history = History(0, UUID.randomUUID().toString(), title, System.currentTimeMillis())
                histories.add(history)
            }

            mHistoryDao.insertAll(histories)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            mHistoryDao.deleteAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }

}