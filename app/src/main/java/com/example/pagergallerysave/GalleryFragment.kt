package com.example.pagergallerysave


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {
    private val galleryViewModel by activityViewModels<GalleryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.swipeIndicator -> {
                swipeLayoutGallery.isRefreshing = true
                Handler().postDelayed({galleryViewModel.ResetQuery()  },1000)
            }
            R.id.menuRetry->{
                Handler().postDelayed({galleryViewModel.retry()},1000)
            }
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val galleryAdapter = GalleryAdapter(galleryViewModel)
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }

       // galleryViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(GalleryViewModel::class.java)
       /* galleryViewModel.photoListLive.observe(this, Observer {
            galleryAdapter.submitList(it)
            swipeLayoutGallery.isRefreshing = false
        })

        galleryViewModel.photoListLive.value?:galleryViewModel.fetchData()

        swipeLayoutGallery.setOnRefreshListener {
            galleryViewModel.fetchData()
        }*/
        galleryViewModel.pagedListPhotoLiveData.observe(viewLifecycleOwner, Observer {
            galleryAdapter.submitList(it)

        })
        swipeLayoutGallery.setOnRefreshListener {
            galleryViewModel.ResetQuery()
        }
        galleryViewModel.networkStatus.observe(viewLifecycleOwner, Observer {
            Log.d("hello","loadAfter:$it")
            galleryAdapter.updateNetworkStatus(it)
            swipeLayoutGallery.isRefreshing=it==NetworkStatus.INITIAL_LOADING
        })
    }

}
