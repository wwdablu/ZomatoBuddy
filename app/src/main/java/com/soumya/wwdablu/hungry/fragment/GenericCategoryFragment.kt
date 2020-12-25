package com.soumya.wwdablu.hungry.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.soumya.wwdablu.hungry.activity.RestaurantDetailsActivity
import com.soumya.wwdablu.hungry.adapter.GenericSearchModelAdapter
import com.soumya.wwdablu.hungry.databinding.FragCategoryGenericBinding
import com.soumya.wwdablu.hungry.defines.CategoryEnum
import com.soumya.wwdablu.hungry.iface.RestaurantItemSelector
import com.soumya.wwdablu.hungry.model.network.search.RestaurantInfo
import com.soumya.wwdablu.hungry.model.network.search.SearchModel
import com.soumya.wwdablu.hungry.repository.HungryRepo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class GenericCategoryFragment(category: CategoryEnum) : HungryFragment<FragCategoryGenericBinding>(),
        RestaurantItemSelector {

    private val mCategoryEnum: CategoryEnum = category
    private lateinit var mGenericSearchModelAdapter: GenericSearchModelAdapter
    private lateinit var mSearchModel: SearchModel

    init {
        getByCategory()
    }

    override fun onCreateViewExt(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        mViewBinding = FragCategoryGenericBinding.inflate(inflater)

        mViewBinding.rvCatList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        if(this::mSearchModel.isInitialized) {
            mViewBinding.rvCatList.adapter = mGenericSearchModelAdapter
        }

        return mViewBinding.root
    }

    private fun getByCategory() {

        HungryRepo.searchByCategoryId(mCategoryEnum)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object: DisposableObserver<SearchModel>() {
                override fun onNext(t: SearchModel?) {

                    if(t != null) {
                        mSearchModel = t
                        mGenericSearchModelAdapter = GenericSearchModelAdapter(t, this@GenericCategoryFragment)
                    } else {
                        getByCollectionId()
                    }
                }

                override fun onError(e: Throwable?) {
                    Timber.e(e)
                }

                override fun onComplete() {

                    if(this@GenericCategoryFragment::mGenericSearchModelAdapter.isInitialized) {
                        mViewBinding.rvCatList.adapter = mGenericSearchModelAdapter
                    }
                }
            })
    }

    private fun getByCollectionId() {

        HungryRepo.searchByCollectionId(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object: DisposableObserver<SearchModel>() {
                override fun onNext(t: SearchModel?) {

                    if(t != null) {
                        mSearchModel = t
                        mGenericSearchModelAdapter = GenericSearchModelAdapter(t, this@GenericCategoryFragment)
                    }
                }

                override fun onError(e: Throwable?) {
                    Timber.e(e)
                }

                override fun onComplete() {

                    if(this@GenericCategoryFragment::mGenericSearchModelAdapter.isInitialized) {
                        mViewBinding.rvCatList.adapter = mGenericSearchModelAdapter
                    }
                }
            })
    }

    override fun onRestaurantClicked(restaurant: RestaurantInfo) {

        activity?.runOnUiThread {
            val intent: Intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("res_details", restaurant)
            startActivity(intent)
        }
    }
}