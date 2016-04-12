package com.github.kittinunf.reactiveandroid.support.v7.widget

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import rx.Observable
import rx.Subscription

/**
 * Created by tipatai on 4/12/16 AD.
 */

abstract class MultiTypeRecyclerViewProxyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var items: List<Any> = listOf()

    abstract var createViewHolder: (ViewGroup?, Int) -> RecyclerView.ViewHolder
    abstract var bindViewHolder: (RecyclerView.ViewHolder, Int, Any) -> Unit
    abstract var itemViewType: (Int) -> Int

    override fun getItemCount(): Int = items.size

    fun getItem(position: Int) = items[position]
    operator fun get(position: Int) = items[position]

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder = createViewHolder.invoke(parent, viewType)

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        bindViewHolder.invoke(viewHolder, position, items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return itemViewType.invoke(position)
    }

}

fun RecyclerView.rx_itemsWithMultiType(observable: Observable<List<Any>>,
                                       itemViewType: (Int) -> Int,
                                       onCreateViewHolder: (ViewGroup?, Int) -> RecyclerView.ViewHolder,
                                       onBindViewHolder: (RecyclerView.ViewHolder, Int, Any) -> Unit): Subscription {
    val proxyAdapter = object : MultiTypeRecyclerViewProxyAdapter() {
        override var createViewHolder: (ViewGroup?, Int) -> RecyclerView.ViewHolder = onCreateViewHolder

        override var bindViewHolder: (RecyclerView.ViewHolder, Int, Any) -> Unit = onBindViewHolder

        override var itemViewType: (Int) -> Int = itemViewType
    }
    return rx_itemsWithMultiType(observable, proxyAdapter)
}

fun RecyclerView.rx_itemsWithMultiType(observable: Observable<List<Any>>,
                                       recyclerProxyAdapter: MultiTypeRecyclerViewProxyAdapter): Subscription {
    adapter = recyclerProxyAdapter
    return observable.subscribe {
        recyclerProxyAdapter.items = it
        post { recyclerProxyAdapter.notifyDataSetChanged() }
    }
}