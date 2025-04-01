package com.bravepeople.onggiyonggi.presentation.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.databinding.FragmentHomeBinding
import com.bravepeople.onggiyonggi.extension.SearchState
import com.bravepeople.onggiyonggi.presentation.home.register.StoreRegisterActivity
import com.bravepeople.onggiyonggi.presentation.home.search.SearchRecentAdapter
import com.bravepeople.onggiyonggi.presentation.home.search.SearchResultAdapter
import com.bravepeople.onggiyonggi.presentation.home.search.SearchViewModel
import com.bravepeople.onggiyonggi.presentation.review.ReviewFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
class HomeFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding) { "homefragment is null" }

    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var searchRecentAdapter: SearchRecentAdapter
    private lateinit var searchResultAdapter: SearchResultAdapter

    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null
    private var currentPosition: LatLng? = null
    private var isAutoMoveEnabled = true
    private var isFirstCameraMove = true

    private lateinit var newMarker: Marker
    private var isUserTyping = true

    private var isFabOpen = false

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation

    // 위치 요청 받을 변수
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            val position = LatLng(location.latitude, location.longitude)
            currentPosition = position

            naverMap?.let { map ->
                map.locationOverlay.position = position
                if (isAutoMoveEnabled) {
                    map.moveCamera(CameraUpdate.scrollTo(position))
                }
            }
        }
    }

    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,   // 앱 사용중에만 허용
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("viewcreated")

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.btnBack.visibility == View.VISIBLE) {
                binding.btnBack.performClick()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        mapReset(savedInstanceState)
        setting()
    }

    private fun mapReset(savedInstanceState: Bundle?) {
        //맵 초기화
        mapView = binding.mvMap
        mapView.onCreate(savedInstanceState)
    }

    private fun setting() {
        permissionCheck()
        getResultList()

        clickCurrentBtn()
        clickSearchBar()
        clickEditText()
        clickAddButton()
    }

    private fun permissionCheck() {
        // 권한 확인 및 지도 초기화
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        Timber.d("권한 상태: $permissionCheck") // 0이면 권한 허용, -1이면 거부

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
                interval = 5000L    // 5초마다 위치정보 받아옴
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback,
                Looper.getMainLooper()
            )

            mapView.getMapAsync(this)
        } else {
            Timber.d("위치 권한 거부됨 → 권한 요청 실행")
            requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun clickCurrentBtn() {
        binding.btnCurrent.setOnClickListener {
            currentPosition?.let { position ->
                isAutoMoveEnabled = true
                isFirstCameraMove = true
                naverMap?.moveCamera(CameraUpdate.scrollTo(position))
                Timber.d("current button click!")
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationOverlay.isVisible = true

        val marker = Marker()
        marker.position = LatLng(37.30025715360071, 127.04301805436064)
        marker.map = naverMap
        marker.setIconPerspectiveEnabled(true);

        clickMarker(marker)

        naverMap.addOnCameraIdleListener {
            if (isFirstCameraMove) {
                isFirstCameraMove = false
                Timber.d("최초 자동 이동 → 무시")
            } else {
                isAutoMoveEnabled = false
                Timber.d("사용자 조작 감지 → 자동 이동 비활성화")
            }
        }
    }


    private fun clickMarker(marker: Marker) {
        marker.setOnClickListener {
            val fragment = ReviewFragment.newInstance(
                Search(
                    R.drawable.img_review1,
                    requireContext().getString(R.string.store_name),
                    requireContext().getString(R.string.store_address),
                    LatLng(37.30025715360071, 127.04301805436064)
                )
            )

            parentFragmentManager.beginTransaction()
                .add(R.id.fcv_review, fragment)
                .addToBackStack(null)
                .commit()

            binding.fcvReview.post {
                val newHeight = (resources.displayMetrics.heightPixels * 0.3).toInt()
                binding.fcvReview.layoutParams.height = newHeight
                binding.fcvReview.requestLayout()
            }


            true
        }
    }

    private fun clickSearchBar() {
        binding.cvSearch.setOnClickListener {
            setVisibility(true)
            getEditText()
            with(binding) {
                etSearch.text.clear()
            }
            searchRecentAdapter = SearchRecentAdapter(requireContext(),
                clickStore = { search ->
                    showReviewFragment(search)
                },
                clickDelete = { search ->
                    removeRecentList(search)
                })
            binding.rvSearch.adapter = searchRecentAdapter
            getSearchRecentList()
            clickBackButton()
        }
    }

    private fun clickEditText() {
        binding.etSearch.setOnTouchListener { v, event ->
            Timber.d("etSearch 터치됨 → review fragment 제거 & 검색창 모드 전환")

            val reviewFragment = parentFragmentManager.findFragmentByTag("ReviewFragment")
            reviewFragment?.let {
                parentFragmentManager.beginTransaction()
                    .remove(it)
                    .commit()
            }

            setVisibility(true)

            searchRecentAdapter.getRecentSearchList(searchViewModel.getRecentSearchList())
            with(binding) {
                rvSearch.adapter = searchRecentAdapter
                rvSearch.visibility = View.VISIBLE
                tvRecentSearches.visibility = View.VISIBLE
                tvDeleteAll.visibility = View.VISIBLE
                fcvReview.layoutParams.height = 0
                fcvReview.requestLayout()
            }

            if (this::newMarker.isInitialized) {
                newMarker.map = null
            }

            false
        }
    }

    private fun setVisibility(search: Boolean) {
        Timber.d("visibility: $search")
        // 검색창 열기
        if (search) {
            with(binding) {
                btnBack.visibility = View.VISIBLE
                btnMic.visibility = View.VISIBLE
                vLine.visibility = View.VISIBLE
                tvSearch.visibility = View.VISIBLE
                tvRecentSearches.visibility = View.VISIBLE
                tvDeleteAll.visibility = View.VISIBLE
                rvSearch.visibility = View.VISIBLE
                ivSearchBackground.visibility = View.VISIBLE
                etSearch.visibility = View.VISIBLE
                ivTextsBackground.visibility = View.VISIBLE

                resetFabState()
                fabMain.visibility=View.GONE
                cvSearch.visibility = View.INVISIBLE
                btnCurrent.visibility = View.INVISIBLE
                tvSearch.visibility = View.INVISIBLE
            }
        } else {// 검색창 닫기
            with(binding) {
                btnBack.visibility = View.INVISIBLE
                btnMic.visibility = View.INVISIBLE
                vLine.visibility = View.INVISIBLE
                tvSearch.visibility = View.INVISIBLE
                tvRecentSearches.visibility = View.INVISIBLE
                tvDeleteAll.visibility = View.INVISIBLE
                rvSearch.visibility = View.GONE
                ivSearchBackground.visibility = View.INVISIBLE
                etSearch.visibility = View.INVISIBLE
                ivTextsBackground.visibility = View.INVISIBLE
                rvResult.visibility = View.GONE

                fabMain.visibility=View.VISIBLE
                cvSearch.visibility = View.VISIBLE
                btnCurrent.visibility = View.VISIBLE
                tvSearch.visibility = View.VISIBLE

            }
        }
    }

    private fun getEditText() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s?.toString() ?: ""
                Timber.d("text: ${inputText}")

                if (inputText.isEmpty()) {
                    with(binding) {
                        tvRecentSearches.visibility = View.VISIBLE
                        tvDeleteAll.visibility = View.VISIBLE
                        rvSearch.visibility = View.VISIBLE
                        rvResult.visibility = View.GONE
                        ivTextsBackground.visibility = View.VISIBLE
                    }
                } else {
                    // 사용자가 직접 입력한 경우에만 리스트 노출
                    if (isUserTyping) {
                        with(binding) {
                            tvRecentSearches.visibility = View.INVISIBLE
                            tvDeleteAll.visibility = View.INVISIBLE
                            rvSearch.visibility = View.GONE
                            rvResult.visibility = View.VISIBLE
                            ivTextsBackground.visibility = View.INVISIBLE
                        }

                        searchViewModel.searchQueryInfo(inputText)
                    } else {
                        Timber.d("text change skipped: not user typing")
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun getResultList() {
        lifecycleScope.launch {
            searchViewModel.searchState.collect { searchState ->
                when (searchState) {
                    is SearchState.Success -> {
                        val searchResultAdapter = SearchResultAdapter(
                            clickStore = { store ->
                                val title = HtmlCompat.fromHtml(
                                    store.title,
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                ).toString()
                                val address = HtmlCompat.fromHtml(
                                    store.roadAddress,
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                ).toString()
                                val data = Search(
                                    R.drawable.ic_pin_green, title, address, LatLng(
                                        store.mapy.toDouble() / 1e7, store.mapx.toDouble() / 1e7
                                    )
                                )

                                showReviewFragment(data)
                            }
                        )
                        searchResultAdapter.getList(searchState.searchDto.items)
                        binding.rvResult.adapter = searchResultAdapter
                    }

                    is SearchState.Loading -> {}
                    is SearchState.Error -> {
                        Timber.e("get search state error!")
                    }
                }
            }
        }
    }

    private fun removeRecentList(item: Search) {
        searchRecentAdapter.removeRecentSearchItem(item)
    }

    private fun getSearchRecentList() {
        searchRecentAdapter.getRecentSearchList(searchViewModel.getRecentSearchList())
    }

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            val fragment = parentFragmentManager.findFragmentByTag("ReviewFragment")
            Timber.d("fragment: ${fragment}")
            if (fragment != null) {
                parentFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
                with(binding) {
                    ivTextsBackground.visibility = View.VISIBLE
                    tvRecentSearches.visibility = View.VISIBLE
                    tvDeleteAll.visibility = View.VISIBLE
                    rvSearch.visibility = View.VISIBLE
                    isUserTyping = false
                    etSearch.text.clear()

                    etSearch.post {
                        isUserTyping = true
                    }
                }
                newMarker.map = null
            } else {
                setVisibility(false)
            }
        }
    }

    private fun showReviewFragment(data: Search) {
        hideKeyboard(binding.root)
        val fragment = ReviewFragment.newInstance(data)

        with(binding.rvResult) {
            adapter = null
            visibility = View.GONE
            layoutParams.height = 0
            requestLayout()
        }

        with(binding) {
            etSearch.clearFocus()
            tvRecentSearches.visibility = View.INVISIBLE
            tvDeleteAll.visibility = View.INVISIBLE
        }

        parentFragmentManager.beginTransaction()
            .add(R.id.fcv_review, fragment, "ReviewFragment")
            .commit()

        binding.fcvReview.post {
            val newHeight = (resources.displayMetrics.heightPixels * 0.3).toInt()
            binding.fcvReview.layoutParams.height = newHeight
            binding.fcvReview.requestLayout()
        }

        isUserTyping = false
        binding.etSearch.setText(data.name)
        binding.etSearch.post {
            isUserTyping = true
        }
        moveToMarker(data)
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun moveToMarker(search: Search) {
        with(binding) {
            ivTextsBackground.visibility = View.INVISIBLE
            tvRecentSearches.visibility = View.INVISIBLE
            tvDeleteAll.visibility = View.INVISIBLE
            rvSearch.visibility = View.INVISIBLE
        }

        naverMap?.let { map ->
            newMarker = Marker()
            newMarker.position = search.address
            newMarker.map = naverMap
            newMarker.setIconPerspectiveEnabled(true)
            newMarker.setCaptionText(search.name)

            map.moveCamera(CameraUpdate.scrollTo(search.address))
        }
    }

    private fun clickAddButton() {

        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)

        binding.fabMain.setOnClickListener {
            if (isFabOpen) {
                with(binding) {
                    fabMain.animate().rotation(0f).setDuration(200).start()
                    fabNew.visibility = View.INVISIBLE
                    fabNew.startAnimation(fabClose)

                    fabNew.postDelayed({
                        fabBan.visibility=View.INVISIBLE
                        fabBan.startAnimation(fabClose)
                    }, 80)
                }
                isFabOpen = false
            } else {
                with(binding){
                    fabMain.animate().rotation(45f).setDuration(200).start()
                    fabBan.visibility = View.VISIBLE
                    fabBan.startAnimation(fabOpen)

                    fabBan.postDelayed({
                        fabNew.visibility = View.VISIBLE
                        fabNew.startAnimation(fabOpen)
                    }, 80)

                    fabNew.setOnClickListener{
                        val intent=Intent(requireContext(), StoreRegisterActivity::class.java)
                        intent.putExtra("type", "new")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
                    }
                    fabBan.setOnClickListener{
                        val intent=Intent(requireContext(), StoreRegisterActivity::class.java)
                        intent.putExtra("type", "bav")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
                    }
                }
                isFabOpen = true


            }
        }
    }

    private fun resetFabState() {
        binding.fabMain.rotation = 0f

        binding.fabNew.clearAnimation()
        binding.fabNew.visibility = View.GONE

        binding.fabBan.clearAnimation()
        binding.fabBan.visibility = View.GONE

        isFabOpen = false
    }


    fun openReviewFragment() {
        resetFabState()
        val reviewFragment = ReviewFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fcv_review, reviewFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()

        resetFabState()
        mapView.onResume()
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionCheck == PackageManager.PERMISSION_GRANTED && !this::mapView.isInitialized) {
            Timber.d("설정에서 돌아온 뒤 위치 권한 허용 확인됨 → mapreset() 호출")
            val locationRequest = LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
                interval = 5000L
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback,
                Looper.getMainLooper()
            )

            mapView.getMapAsync(this)
        }
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

        fusedLocationClient.removeLocationUpdates(mLocationCallback)
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}