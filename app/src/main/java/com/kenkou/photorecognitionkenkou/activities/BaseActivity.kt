package com.kenkou.photorecognitionkenkou.activities

import android.support.v7.app.AppCompatActivity
import com.kenkou.photorecognitionkenkou.services.AppServiceApi
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseActivity : AppCompatActivity() {
    val appService by lazy {
        AppServiceApi.create()
    }

    private var disposables: CompositeDisposable? = null

    fun addDisposable(disposable: Disposable) {
        disposables?.add(disposable)
    }

    private fun clearDisposable() {
        disposables?.dispose()
        disposables?.clear()
    }

    override fun onStop() {
        super.onStop()

        clearDisposable()
    }

}
