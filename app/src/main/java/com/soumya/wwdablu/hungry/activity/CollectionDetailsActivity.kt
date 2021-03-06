package com.soumya.wwdablu.hungry.activity

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.soumya.wwdablu.hungry.databinding.ActivityCollectionDetailsBinding
import com.soumya.wwdablu.hungry.iface.RestaurantItemSelector
import com.soumya.wwdablu.hungry.adapter.GenericSearchResultAdapter
import com.soumya.wwdablu.hungry.network.model.collections.CollectionInfo
import com.soumya.wwdablu.hungry.network.model.search.RestaurantInfo
import com.soumya.wwdablu.hungry.network.model.search.SearchModel
import com.soumya.wwdablu.hungry.repository.HungryRepo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class CollectionDetailsActivity : HungryActivity() {

    private lateinit var mViewBinding: ActivityCollectionDetailsBinding
    private lateinit var mSearchModel: SearchModel
    private lateinit var mAdapter: GenericSearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = ActivityCollectionDetailsBinding.inflate(layoutInflater)

        mViewBinding.rvResByCollection.layoutManager = LinearLayoutManager(this)
        getRestaurantsByCollectionId(intent.getStringExtra("collection_id") ?: "1")

        val collectionInfoParcelable: Parcelable? = intent.getParcelableExtra("collection_info")
        if(collectionInfoParcelable != null) {
            val model: CollectionInfo = collectionInfoParcelable as CollectionInfo
            mViewBinding.collectionModel = model
            loadImageIntoImageView(model.imageUrl, mViewBinding.ivRestaurantImage)
        }

        setContentView(mViewBinding.root)
    }

    private fun getRestaurantsByCollectionId(collectionId: String) {

        HungryRepo.searchByCollectionId(collectionId.toInt())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object: DisposableObserver<SearchModel>() {
                override fun onNext(t: SearchModel?) {

                    if(t != null) {
                        mSearchModel = t
                    }
                }

                override fun onError(e: Throwable?) {
                    Timber.e(e)
                    finish()
                }

                override fun onComplete() {

                    if(this@CollectionDetailsActivity::mSearchModel.isInitialized &&
                            !this@CollectionDetailsActivity::mAdapter.isInitialized) {
                        mAdapter = GenericSearchResultAdapter(mSearchModel, mListener)
                        mViewBinding.rvResByCollection.adapter = mAdapter

                        mViewBinding.lotLoading.cancelAnimation()
                        mViewBinding.lotLoading.visibility = View.GONE
                        mViewBinding.rvResByCollection.visibility = View.VISIBLE
                    }
                }
            })
    }

    private val mListener = object: RestaurantItemSelector {
        override fun onRestaurantClicked(restaurant: RestaurantInfo) {
            runOnUiThread {
                val intent: Intent = Intent(this@CollectionDetailsActivity, RestaurantDetailsActivity::class.java)
                intent.putExtra("res_details", restaurant)
                startActivity(intent)
            }
        }
    }
}