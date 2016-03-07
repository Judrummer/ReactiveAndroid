package com.github.kittinunf.reactiveandroid.rx

import com.github.kittinunf.reactiveandroid.MutablePropertyType
import rx.Observable
import rx.Observer
import rx.Subscription

fun <T, U, X> Observable<T>.bindTo(u: U,
                                onNext: U.(T) -> X,
                                onError: (U.(Throwable?) -> Unit)? = null,
                                onCompleted: (U.() -> Unit)? = null): Subscription {

    return subscribe(object : Observer<T> {
        override fun onNext(t: T) {
            u.onNext(t)
        }

        override fun onError(e: Throwable?) {
            onError?.let {
                u.onError(e)
            }
        }

        override fun onCompleted() {
            onCompleted?.let {
                u.onCompleted()
            }
        }
    })
}

fun <T, U, X> Observable<T>.bindTo(u: U,
                                onNext: U.() -> X,
                                onError: (U.(Throwable?) -> Unit)? = null,
                                onCompleted: (U.() -> Unit)? = null) : Subscription {
    return subscribe(object : Observer<T> {
        override fun onNext(t: T) {
            u.onNext()
        }

        override fun onError(e: Throwable?) {
            onError?.let {
                u.onError(e)
            }
        }

        override fun onCompleted() {
            onCompleted?.let {
                u.onCompleted()
            }
        }
    })
}

fun <T> Observable<T>.bindTo(property: MutablePropertyType<T>): Subscription {
    return subscribe {
        property.value = it
    }
}

fun <T : Pair<A, B>, A, B> Observable<T>.subscribe(onNext: (A, B) -> Unit,
                                                   onError: ((Throwable?) -> Unit)? = null,
                                                   onCompleted: (() -> Unit)? = null): Subscription {
    return subscribe(object : Observer<T> {
        override fun onNext(t: T) {
            val (first, second) = t
            onNext(first, second)
        }

        override fun onError(e: Throwable?) {
            onError?.let {
                it(e)
            }
        }

        override fun onCompleted() {
            onCompleted?.let {
                it()
            }
        }
    })
}

fun <T : Triple<A, B, C>, A, B, C> Observable<T>.subscribe(onNext: (A, B, C) -> Unit,
                                                           onError: ((Throwable?) -> Unit)? = null,
                                                           onCompleted: (() -> Unit)? = null): Subscription {
    return subscribe(object : Observer<T> {
        override fun onNext(t: T) {
            val (first, second, third) = t
            onNext(first, second, third)
        }

        override fun onError(e: Throwable?) {
            onError?.let {
                it(e)
            }
        }

        override fun onCompleted() {
            onCompleted?.let {
                it()
            }
        }
    })
}
