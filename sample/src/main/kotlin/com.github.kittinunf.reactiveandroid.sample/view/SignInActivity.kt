package com.github.kittinunf.reactiveandroid.sample.view

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.github.kittinunf.reactiveandroid.rx.addTo
import com.github.kittinunf.reactiveandroid.rx.bindTo
import com.github.kittinunf.reactiveandroid.sample.R
import com.github.kittinunf.reactiveandroid.sample.viewmodel.SignInViewAction
import com.github.kittinunf.reactiveandroid.sample.viewmodel.SignInViewModel
import com.github.kittinunf.reactiveandroid.scheduler.AndroidThreadScheduler
import com.github.kittinunf.reactiveandroid.view.rx_click
import com.github.kittinunf.reactiveandroid.view.rx_visibility
import com.github.kittinunf.reactiveandroid.widget.rx_afterTextChanged
import com.github.kittinunf.reactiveandroid.widget.rx_applyAction
import com.github.kittinunf.reactiveandroid.widget.rx_text
import kotlinx.android.synthetic.main.activity_sign_in.*
import rx.subscriptions.CompositeSubscription

class SignInActivity : AppCompatActivity(), SignInViewAction {

    val subscriptions = CompositeSubscription()

    val viewModel by lazy(LazyThreadSafetyMode.NONE) { SignInViewModel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //title
        titleTextView.text = javaClass.simpleName

        //button
        viewModel.signInAction.apply {
            values.observeOn(AndroidThreadScheduler.mainThreadScheduler)
                    .map { "Your account have been successfully created, please check your email inbox at ${it.first}" }
                    .bindTo(this@SignInActivity, SignInActivity::handleSuccess)
                    .addTo(subscriptions)

            errors.observeOn(AndroidThreadScheduler.mainThreadScheduler)
                    .bindTo(this@SignInActivity, SignInActivity::handleFailure)
                    .addTo(subscriptions)
        }
        signInButton.rx_applyAction(viewModel.signInAction).addTo(subscriptions)

        //progressBar
        loadingProgressBar.rx_visibility.bindTo(viewModel.signInAction.executing.map { if (it) View.VISIBLE else View.INVISIBLE }).addTo(subscriptions)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.unsubscribe()
    }

    override fun usernameObservable() = userNameEditText.rx_afterTextChanged().map { it.toString() }

    override fun passwordObservable() = passwordEditText.rx_afterTextChanged().map { it.toString() }

    override fun signInObservable() = signInButton.rx_click()

    override fun username() = userNameEditText.rx_text

    override fun password() = passwordEditText.rx_text

    fun handleSuccess(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage(message)
            setPositiveButton("OK", null)
        }.create().show()
    }

    fun handleFailure(e: Throwable) {
        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
    }

}

