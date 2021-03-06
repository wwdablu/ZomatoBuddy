package com.soumya.wwdablu.hungry.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soumya.wwdablu.hungry.activity.CollectionDetailsActivity
import com.soumya.wwdablu.hungry.activity.CollectionsActivity
import com.soumya.wwdablu.hungry.activity.RestaurantDetailsActivity
import com.soumya.wwdablu.hungry.activity.SearchActivity
import com.soumya.wwdablu.hungry.adapter.CuratedCollectionsAdapter
import com.soumya.wwdablu.hungry.databinding.FragRecommendedBinding
import com.soumya.wwdablu.hungry.adapter.GenericSearchResultAdapter
import com.soumya.wwdablu.hungry.iface.CollectionItemSelector
import com.soumya.wwdablu.hungry.iface.RestaurantItemSelector
import com.soumya.wwdablu.hungry.network.model.collections.CollectionInfo
import com.soumya.wwdablu.hungry.network.model.collections.CuratedCollection
import com.soumya.wwdablu.hungry.network.model.search.RestaurantInfo
import com.soumya.wwdablu.hungry.network.model.search.SearchModel
import com.soumya.wwdablu.hungry.repository.HungryRepo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class RecommendedFragment private constructor() : HungryFragment<FragRecommendedBinding>(),
        RestaurantItemSelector, CollectionItemSelector {

    private lateinit var mCollectionAdapter: CuratedCollectionsAdapter
    private lateinit var mGenericSearchResultAdapter: GenericSearchResultAdapter

    companion object {

        fun newInstance() : RecommendedFragment {
            return RecommendedFragment()
        }
    }

    override fun onCreateViewExt(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        mViewBinding = FragRecommendedBinding.inflate(inflater, container, false)
        mViewBinding.city = HungryRepo.getCityModel().model[0]

        mViewBinding.rvCuratedCollection.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        getCollection()

        mViewBinding.rvRecommendedForYou.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mViewBinding.rvRecommendedForYou.addOnItemTouchListener(mItemTouchListener)
        getRecommendation()

        mViewBinding.tvCollectionSeeall.setOnClickListener {
            startActivity(Intent(context, CollectionsActivity::class.java))
        }

        mViewBinding.searchBar.rlSearchContainer.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        return mViewBinding.root
    }

    private fun getRecommendation() {

        HungryRepo.searchByCollectionId(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object: DisposableObserver<SearchModel>() {
                override fun onNext(t: SearchModel?) {

                    if(t == null) {
                        return
                    }

                    mGenericSearchResultAdapter = GenericSearchResultAdapter(t, this@RecommendedFragment)
                }

                override fun onError(e: Throwable?) {
                    Timber.e(e)
                }

                override fun onComplete() {

                    if(this@RecommendedFragment::mGenericSearchResultAdapter.isInitialized) {
                        mViewBinding.lotRecommendedLoading.cancelAnimation()
                        mViewBinding.lotRecommendedLoading.visibility = View.GONE
                        mViewBinding.rvRecommendedForYou.visibility = View.VISIBLE
                        mViewBinding.rvRecommendedForYou.adapter = mGenericSearchResultAdapter
                    }
                }
            })
    }

    private fun getCollection() {

        HungryRepo.getCollections()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object: DisposableObserver<List<CuratedCollection>>() {
                override fun onNext(t: List<CuratedCollection>?) {

                    if(t != null) {
                        mCollectionAdapter = CuratedCollectionsAdapter(t, this@RecommendedFragment)
                    }
                }

                override fun onError(e: Throwable?) {
                    Timber.e(e)
                }

                override fun onComplete() {

                    if(this@RecommendedFragment::mCollectionAdapter.isInitialized) {
                        mViewBinding.lotCollectionLoading.cancelAnimation()
                        mViewBinding.lotCollectionLoading.visibility = View.GONE
                        mViewBinding.rvCuratedCollection.visibility = View.VISIBLE
                        mViewBinding.rvCuratedCollection.adapter = mCollectionAdapter
                    }
                }
            })
    }

    private val mItemTouchListener: RecyclerView.OnItemTouchListener = object: RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            val view: View = rv.findChildViewUnder(e.x, e.y) ?: return false
            val viewHolder: RecyclerView.ViewHolder = rv.findContainingViewHolder(view) ?: return false
            mGenericSearchResultAdapter.handleTouchEvent(e, viewHolder)

            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            //
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            //
        }
    }

    override fun onRestaurantClicked(restaurant: RestaurantInfo) {

        activity?.runOnUiThread {
            val intent: Intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("res_details", restaurant)
            startActivity(intent)
        }
    }

    override fun onCollectionClicked(collection: CollectionInfo) {
        activity?.runOnUiThread {
            val intent: Intent = Intent(context, CollectionDetailsActivity::class.java)
            intent.putExtra("collection_id", collection.id)
            intent.putExtra("collection_info", collection)
            startActivity(intent)
        }
    }
}