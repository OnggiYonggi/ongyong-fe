package com.bravepeople.onggiyonggi.presentation.main.home.store_register

import android.content.Intent
import android.content.res.Resources
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
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseNaverAddressDto
import com.bravepeople.onggiyonggi.databinding.ActivityStoreRegisterBinding
import com.bravepeople.onggiyonggi.extension.GetStoreTimeState
import com.bravepeople.onggiyonggi.extension.SearchState
import com.bravepeople.onggiyonggi.extension.home.register.RegisterStoreState
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
class StoreRegisterActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityStoreRegisterBinding

    private val storeRegisterViewModel: StoreRegisterViewModel by viewModels()

    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null
    private lateinit var newMarker: Marker

    private var pendingLatLng: LatLng? = null
    private var pendingTitle: String? = null

    private val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setting(savedInstanceState)
    }

    private fun init() {
        binding = ActivityStoreRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }

        val defaultPaddingStart = binding.root.paddingStart
        val defaultPaddingEnd = binding.root.paddingEnd
        val defaultPaddingBottom = binding.root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            // 상단 여백 적용
            binding.root.setPadding(
                defaultPaddingStart,
                statusBarHeight,
                defaultPaddingEnd,
                defaultPaddingBottom
            )

            // 하단 버튼 위 여백 적용
            binding.btnRegister.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navBarHeight + 10.dp  // 16dp 정도 추가 마진 권장
            }

            insets
        }

        mapReset(savedInstanceState)
        searchStore()
        clickTypeButton()
        clickBackButton()
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun mapReset(savedInstanceState: Bundle?) {
        mapView = binding.mvMap
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    private fun searchStore() {
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    binding.rvResult.visibility = View.VISIBLE
                    storeRegisterViewModel.searchQueryInfo(newText)
                } else {
                    binding.rvResult.visibility = View.GONE
                }
                return false
            }
        })
    }

    private fun getResultList(storeType: String) {
        lifecycleScope.launch {
            storeRegisterViewModel.searchState.collect { searchState ->
                when (searchState) {
                    is SearchState.Success -> {
                        val storeRegisterAdapter = StoreRegisterAdapter(
                            clickStore = { store ->
                                binding.mvMap.visibility = View.VISIBLE
                                binding.rvResult.visibility = View.GONE
                                checkVisibleRegister()

                                val imm =
                                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(binding.svSearch.windowToken, 0)
                                binding.svSearch.clearFocus()

                                val position =
                                    LatLng(store.mapy.toDouble() / 1e7, store.mapx.toDouble() / 1e7)

                                if (naverMap != null) {
                                    addMarkerAndMoveCamera(position, store.title)
                                } else {
                                    pendingLatLng = position
                                    pendingTitle = store.title
                                }

                                getTime(storeType, store)
                            }
                        )
                        binding.rvResult.adapter = storeRegisterAdapter
                        storeRegisterAdapter.getList(searchState.searchDto.items)
                    }

                    is SearchState.Loading -> {}
                    is SearchState.Error -> Timber.e("get search state error!")
                }
            }
        }
    }

    private fun getTime(storeType: String, store: ResponseNaverAddressDto.Item){
        val dayMap = mapOf(
            "Monday" to "월요일",
            "Tuesday" to "화요일",
            "Wednesday" to "수요일",
            "Thursday" to "목요일",
            "Friday" to "금요일",
            "Saturday" to "토요일",
            "Sunday" to "일요일"
        )

        lifecycleScope.launch {
            storeRegisterViewModel.getStoreTimeState.collect{state->
                when(state){
                    is GetStoreTimeState.Success->{
                        val time = state.searchDto.places
                            .firstOrNull { it.regularOpeningHours != null }
                            ?.regularOpeningHours?.weekdayDescriptions

                        val translatedTime = time?.map { line ->
                            var newLine = line
                            dayMap.forEach { (eng, kor) ->
                                newLine = newLine.replace(eng, kor)
                            }
                            newLine
                        }
                        clickRegisterButton(setText(), storeType, store, translatedTime?.joinToString("\n"))
                    }
                    is GetStoreTimeState.Loading->{}
                    is GetStoreTimeState.Error->{
                        Timber.e("get store time state error!")
                    }
                }
            }
        }

        storeRegisterViewModel.searchStoreTime(store.title)
    }

    private fun clickRegisterButton(
        type: String,
        storeType: String,
        store: ResponseNaverAddressDto.Item,
        time:String?
    ) {
        val token = intent.getStringExtra("accessToken")

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                storeRegisterViewModel.registerState.collect { state ->
                    when (state) {
                        is RegisterStoreState.Success -> {
                            if (type == "ROOKIE") {
                                val intent = Intent(
                                    this@StoreRegisterActivity,
                                    ReviewRegisterActivity::class.java
                                )
                                intent.putExtra("registerActivity", "storeRegisterActivity")
                                intent.putExtra("fromNewRegister", true)
                                intent.putExtra("storeId", state.registerDto.data)
                                intent.putExtra("accessToken", token)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@StoreRegisterActivity,
                                    getString(R.string.store_register_ban_complete),
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                                //overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
                            }
                        }

                        is RegisterStoreState.Loading -> {}
                        is RegisterStoreState.Error -> {
                            Timber.e("register state error!")
                        }
                    }
                }
            }

            token?.let {
                storeRegisterViewModel.register(
                    token,
                    type,
                    storeType,
                    store.mapy.toDouble() / 1e7,
                    store.mapx.toDouble() / 1e7,
                    store.roadAddress,
                    store.title,
                    time?:getString(R.string.store_register_time_null)
                )
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

    private fun setText(): String {
        val type = intent.getStringExtra("type")
        if (type == "ROOKIE") {
            binding.tvTitle.text = getString(R.string.store_register_new)
        } else binding.tvTitle.text = getString(R.string.store_register_ban)

        return type!!
    }

    private fun checkVisibleRegister() {
        with(binding) {
            if ((btnCafe.isSelected || btnRestaurant.isSelected) && mvMap.visibility == View.VISIBLE)
                btnRegister.visibility = View.VISIBLE
        }
    }

    private fun clickTypeButton() {
        with(binding) {
            btnCafe.setOnClickListener {
                btnCafe.isSelected = !btnCafe.isSelected
                btnRestaurant.isSelected = false

                if (btnCafe.isSelected && !btnRestaurant.isSelected) {
                    setVisibleSearch(true)
                } else if (!btnCafe.isSelected && !btnRestaurant.isSelected) {
                    setVisibleSearch(false)
                }

                checkVisibleRegister()
                getResultList("CAFE")
            }

            btnRestaurant.setOnClickListener {
                btnRestaurant.isSelected = !btnRestaurant.isSelected
                btnCafe.isSelected = false

                if (!btnCafe.isSelected && btnRestaurant.isSelected) {
                    setVisibleSearch(true)
                } else if (!btnCafe.isSelected && !btnRestaurant.isSelected) {
                    setVisibleSearch(false)
                }

                checkVisibleRegister()
                getResultList("FOOD")
            }
        }
    }

    private fun setVisibleSearch(isVisible:Boolean){
        with(binding){
            if(isVisible){
                tvWhere.visibility = View.VISIBLE
                svSearch.visibility = View.VISIBLE
            }else{
                svSearch.setQuery("", false)
                tvWhere.visibility = View.GONE
                svSearch.visibility = View.GONE
                mvMap.visibility=View.GONE
                btnRegister.visibility=View.GONE
            }
        }

    }

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }
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
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}