package com.bravepeople.onggiyonggi.presentation.main.home.store_register

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivityStoreRegisterBinding
import com.bravepeople.onggiyonggi.extension.SearchState
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.ReviewRegisterActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class StoreRegisterActivity:AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityStoreRegisterBinding

    private val storeRegisterViewModel: StoreRegisterViewModel by viewModels()
    private lateinit var mapView:MapView
    private var naverMap: NaverMap? = null
    private lateinit var newMarker: Marker

    private var pendingLatLng: LatLng? = null
    private var pendingTitle: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setting(savedInstanceState)
    }

    private fun init(){
        binding=ActivityStoreRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnBack) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = topInset + 5.dpToPx()  // 원래 marginTop이 16dp였다면 이렇게 추가
            }
            insets
        }

        mapReset(savedInstanceState)
        getResultList()
        setText()
        searchStore()
        clickBackButton()
        clickTypeButton()
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun mapReset(savedInstanceState: Bundle?){
        mapView=binding.mvMap
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    private fun getResultList(){
        lifecycleScope.launch {
            storeRegisterViewModel.searchState.collect{searchState->
                when(searchState){
                    is SearchState.Success->{
                        val storeRegisterAdapter = StoreRegisterAdapter(
                            clickStore = {store->
                                binding.mvMap.visibility=View.VISIBLE
                                binding.rvResult.visibility=View.GONE
                                binding.btnRegister.visibility=View.VISIBLE

                                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(binding.svSearch.windowToken, 0)
                                binding.svSearch.clearFocus()

                                val position = LatLng(store.mapy.toDouble() / 1e7, store.mapx.toDouble() / 1e7)

                                if (naverMap != null) {
                                    addMarkerAndMoveCamera(position, store.title)
                                } else {
                                    pendingLatLng = position
                                    pendingTitle = store.title
                                }
                            }
                        )
                        binding.rvResult.adapter=storeRegisterAdapter
                        storeRegisterAdapter.getList(searchState.searchDto.items)
                    }
                    is SearchState.Loading->{}
                    is SearchState.Error-> Timber.e("get search state error!")
                }
            }
        }
    }

    private fun addMarkerAndMoveCamera(position: LatLng, title: String) {
        if (::newMarker.isInitialized) newMarker.map = null

        newMarker = Marker().apply {
            this.position = position
            this.map = naverMap
            this.setIconPerspectiveEnabled(true)
            this.setCaptionText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY).toString())
        }

        naverMap?.moveCamera(CameraUpdate.scrollTo(position))
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationOverlay.isVisible = false
        naverMap.uiSettings.apply {
            isScrollGesturesEnabled = false
            isZoomGesturesEnabled = false
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
        }

        if (pendingLatLng != null && pendingTitle != null) {
            addMarkerAndMoveCamera(pendingLatLng!!, pendingTitle!!)
            pendingLatLng = null
            pendingTitle = null
        }
    }

    private fun setText(){
        val type = intent.getStringExtra("type")
        if(type=="new"){
            binding.tvTitle.text=getString(R.string.store_register_new)
        }else binding.tvTitle.text=getString(R.string.store_register_ban)

        clickRegisterButton(type)
    }

    private fun searchStore(){
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=null) {
                    binding.rvResult.visibility = View.VISIBLE
                    storeRegisterViewModel.searchQueryInfo(newText)
                }
                else{
                    binding.rvResult.visibility=View.GONE
                }
                return false
            }
        })
    }

    private fun clickBackButton(){
        binding.btnBack.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }
    }

    private fun clickTypeButton(){
        binding.btnCafe.setOnClickListener{
            binding.btnCafe.isSelected=!binding.btnCafe.isSelected
            binding.btnRestaurant.isSelected=false
        }

        binding.btnRestaurant.setOnClickListener{
            binding.btnRestaurant.isSelected=!binding.btnRestaurant.isSelected
            binding.btnCafe.isSelected=false
        }
    }

    private fun clickRegisterButton(type: String?) {
        binding.btnRegister.setOnClickListener{
            if(type=="new"){
                val intent=Intent(this, ReviewRegisterActivity::class.java)
                intent.putExtra("registerActivity","storeRegisterActivity")
                intent.putExtra("fromReview", true)
                startActivity(intent)
            }else{
                Toast.makeText(
                    this,
                    getString(R.string.store_register_ban_complete),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun finish() {
        super.finish()
        mapView.onDestroy()
    }
}