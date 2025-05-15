package com.bravepeople.onggiyonggi.presentation.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.databinding.FragmentHomeBinding
import com.bravepeople.onggiyonggi.extension.SearchState
import com.bravepeople.onggiyonggi.presentation.main.home.store_register.StoreRegisterActivity
import com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewFragment
import com.bravepeople.onggiyonggi.presentation.main.home.search.SearchRecentAdapter
import com.bravepeople.onggiyonggi.presentation.main.home.search.SearchResultAdapter
import com.bravepeople.onggiyonggi.presentation.main.home.search.SearchViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding) { "homefragment is null" }

    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var searchRecentAdapter: SearchRecentAdapter

    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null
    private var currentPosition: LatLng? = null
    private var isAutoMoveEnabled = true
    private var isFirstCameraMove = true

    private var newMarker: Marker? = null
    private var isUserTyping = true
    private var clickSearch = false
    private var markerClick: Boolean = false
    private var selectedMarker: Marker? = null

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    private var backPressedTime: Long = 0L
    private val backPressInterval = 2000L // 2초

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

                if (isFirstCameraMove) {
                    Timber.d("위치 수신 → 최초 자동 이동 실행")
                    map.moveCamera(CameraUpdate.scrollTo(position))
                    isFirstCameraMove = false

                    map.addOnCameraIdleListener {   // 이후엔 버튼으로만 이동
                        isAutoMoveEnabled = false
                        Timber.d("사용자 조작 감지 → 자동 이동 비활성화")
                    }
                }
            }
        }
    }

    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,   // 앱 사용중에만 허용
        Manifest.permission.ACCESS_COARSE_LOCATION
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
        ViewCompat.setOnApplyWindowInsetsListener(binding.cvSearch) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = topInset + 16.dpToPx()  // 원래 marginTop이 16dp였다면 이렇게 추가
            }
            insets
        }

        permissionCheck()
        getResultList()

        clickCurrentBtn()
        clickSearchBar()
        clickEditText()
        clickAddButton()
        clickSystemBackButton()
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
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
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val position = LatLng(location.latitude, location.longitude)
                    currentPosition = position

                    naverMap?.let { map ->
                        map.moveCamera(CameraUpdate.scrollTo(position))
                        map.locationOverlay.position = position
                        map.locationOverlay.isVisible = true
                    }
                    Timber.d("lastLocation으로 즉시 이동")
                } else {
                    val locationRequest = LocationRequest.create().apply {
                        priority = Priority.PRIORITY_HIGH_ACCURACY
                        interval = 5000L
                    }

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        mLocationCallback,
                        Looper.getMainLooper()
                    )

                    Timber.d("실시간 위치 요청 시작")
                }
            }

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
        marker.position = LatLng(37.300536, 127.044169)
        marker.setIconPerspectiveEnabled(true)
        marker.icon = MarkerIcons.BLACK
        marker.iconTintColor = requireContext().getColor(R.color.home_bronze_green)
        marker.map = naverMap

       /* val markerBan = Marker()
        markerBan.position = LatLng(37.300956, 127.045275)
        markerBan.setIconPerspectiveEnabled(true)
        markerBan.tag = true
        markerBan.icon = MarkerIcons.BLACK
        markerBan.iconTintColor = requireContext().getColor(R.color.red)
        markerBan.map = naverMap*/

        val marker2 = Marker()
        marker2.position = LatLng(37.299176, 127.045354)
        marker2.setIconPerspectiveEnabled(true)
        marker2.tag = false
        marker2.icon = MarkerIcons.BLACK
        marker2.iconTintColor = requireContext().getColor(R.color.home_silver_green)
        marker2.map = naverMap

        val marker3 = Marker()
        marker3.position = LatLng(37.298992, 127.043875)
        marker3.setIconPerspectiveEnabled(true)
        marker3.tag = false
        marker3.icon = MarkerIcons.BLACK
        marker3.iconTintColor = requireContext().getColor(R.color.home_gold_green)
        marker3.map = naverMap

        val marker4 = Marker()
        marker4.position = LatLng(37.297731, 127.042160)
        marker4.setIconPerspectiveEnabled(true)
        marker4.tag = false
        marker4.icon = MarkerIcons.BLACK
        marker4.iconTintColor = requireContext().getColor(R.color.home_rookie_yellow)
        marker4.map = naverMap

        clickMarker(marker)
        //clickMarker(markerBan)

        /* naverMap.addOnCameraIdleListener {
             if (isFirstCameraMove) {
                 isFirstCameraMove = false
                 Timber.d("최초 자동 이동 → 무시")
             } else {
                 isAutoMoveEnabled = false
                 Timber.d("사용자 조작 감지 → 자동 이동 비활성화")
             }
         }*/
    }


    private fun clickMarker(marker: Marker) {
        marker.setOnClickListener {
            selectedMarker = marker
            val isBan = marker.tag as? Boolean ?: false
            showReviewFragment(
                Search(
                    R.drawable.img_review1,
                    requireContext().getString(R.string.store_name),
                    requireContext().getString(R.string.store_address),
                    marker.position,
                    isBan
                ), true
            )
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
                    showReviewFragment(search, false)
                },
                clickDelete = { search ->
                    removeRecentList(search)
                })
            binding.rvSearch.adapter = searchRecentAdapter

            val reviewFragment = parentFragmentManager.findFragmentByTag("ReviewFragment")
            reviewFragment?.let {
                parentFragmentManager.beginTransaction()
                    .remove(it)
                    .commit()
            }

            markerClick = false
            clickSearch = true
            selectedMarker?.setCaptionText("")
            newMarker?.map = null

            requestPermission()
            getSearchRecentList()
            clickMicButton()
            clickBackButton()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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
            clickSearch = true

            searchRecentAdapter.getRecentSearchList(searchViewModel.getRecentSearchList())
            with(binding) {
                rvSearch.adapter = searchRecentAdapter
                rvSearch.visibility = View.VISIBLE
                tvRecentSearches.visibility = View.VISIBLE
                tvDeleteAll.visibility = View.VISIBLE
                fcvReview.layoutParams.height = 0
                fcvReview.requestLayout()
            }

            newMarker?.map = null

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
                fabMain.visibility = View.GONE
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

                fabMain.visibility = View.VISIBLE
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
                                    ), true
                                )

                                Timber.d(
                                    "좌표: ${
                                        LatLng(
                                            store.mapy.toDouble() / 1e7, store.mapx.toDouble() / 1e7
                                        )
                                    }"
                                )

                                showReviewFragment(data, false)
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

    private fun showReviewFragment(data: Search, click: Boolean) {
        hideKeyboard(binding.root)
        val fragmentManager = parentFragmentManager

        val existingFragment = fragmentManager.findFragmentByTag("ReviewFragment")
        Timber.d("fragment: ${existingFragment}")
        if (existingFragment != null) {
            fragmentManager.beginTransaction()
                .remove(existingFragment)
                .commitNow() // 확실하게 제거 완료 후 진행
        }

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

        fragmentManager.beginTransaction()
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
        moveToMarker(data, click)
    }


    private fun hideKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun moveToMarker(search: Search, click: Boolean) {
        with(binding) {
            ivTextsBackground.visibility = View.INVISIBLE
            tvRecentSearches.visibility = View.INVISIBLE
            tvDeleteAll.visibility = View.INVISIBLE
            rvSearch.visibility = View.INVISIBLE
        }

        if (markerClick) {
            newMarker?.setCaptionText("")
            markerClick = false
        }
        markerClick = click

        naverMap?.let { map ->
            val marker = Marker()
            marker.position = search.address
            marker.map = naverMap
            marker.setIconPerspectiveEnabled(true)
            marker.setCaptionText(search.name)

            map.moveCamera(CameraUpdate.scrollTo(search.address))

            newMarker = marker
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
                        fabBan.visibility = View.INVISIBLE
                        fabBan.startAnimation(fabClose)
                    }, 80)
                }
                isFabOpen = false
            } else {
                with(binding) {
                    fabMain.animate().rotation(45f).setDuration(200).start()
                    fabBan.visibility = View.VISIBLE
                    fabBan.startAnimation(fabOpen)

                    fabBan.postDelayed({
                        fabNew.visibility = View.VISIBLE
                        fabNew.startAnimation(fabOpen)
                    }, 80)

                    fabNew.setOnClickListener {
                        val intent = Intent(requireContext(), StoreRegisterActivity::class.java)
                        intent.putExtra("type", "new")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.stay_still
                        )
                    }
                    fabBan.setOnClickListener {
                        val intent = Intent(requireContext(), StoreRegisterActivity::class.java)
                        intent.putExtra("type", "bav")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.stay_still
                        )
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
                selectedMarker!!.setCaptionText("")
                newMarker?.map = null
            } else {
                setVisibility(false)
                clickSearch = false
            }
        }

        clickSystemBackButton()
    }

    private fun clickSystemBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val reviewFragment = parentFragmentManager.findFragmentByTag("ReviewFragment")

            Timber.d("markerClick: ${markerClick}")

            when {
                // 1. ReviewFragment가 있으면 제거
                reviewFragment != null -> {
                    Timber.d("back: reviewfragment is not null")
                    parentFragmentManager.beginTransaction()
                        .remove(reviewFragment)
                        .commitNow()

                    markerClick = false
                    newMarker?.map = null

                    if (clickSearch) setVisibility(true)
                }

                // 2. 검색창이 열려 있으면 닫기
                isSearchUIVisible() -> {
                    Timber.d("back: search ui is visible")
                    setVisibility(false)
                    clickSearch = false
                }

                // 3. 초기 화면이면 종료 물어보기
                else -> {
                    Timber.d("back: none is visible")
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - backPressedTime < backPressInterval) {
                        requireActivity().finish()
                    } else {
                        backPressedTime = currentTime
                        Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            /*if(!markerClick) {
                // 검색창에서 결과 또는 최근 리스트를 클릭한 후 화면이라면
                if (reviewFragment != null) {
                    Timber.d("ReviewFragment 제거")
                    parentFragmentManager.beginTransaction()
                        .remove(reviewFragment)
                        .commit()

                    if(clickSearch){
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
                            newMarker?.map = null
                            fcvReview.layoutParams.height = 0
                            fcvReview.requestLayout()
                        }

                        clickSearch=false
                    }

                } else {    // 최근 리스트가 보이는 화면이라면
                    if(binding.ivTextsBackground.visibility==View.VISIBLE){
                        setVisibility(false)
                    }else{  // 초기 화면이라면
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - backPressedTime < backPressInterval) {
                            requireActivity().finish()
                        } else {
                            backPressedTime = currentTime
                            Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{  // 핀 마커 누른 후 화면이라면
                Timber.d("ReviewFragment 제거")
                if (reviewFragment != null) {
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(0, R.anim.slide_out_bottom) // enter, exit
                        .remove(reviewFragment)
                        .commit()
                    markerClick=false
                    newMarker?.setCaptionText("")
                }
            }
*/
        }
    }

    private fun isSearchUIVisible(): Boolean {
        return binding.rvSearch.visibility == View.VISIBLE || binding.etSearch.visibility == View.VISIBLE
    }

    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }
    }

    // 말하기 버튼 클릭
    private fun clickMicButton() {
        binding.btnMic.setOnClickListener{
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(recognitionListener)
                // RecognizerIntent 생성
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_CALLING_PACKAGE,
                        requireActivity().packageName
                    ) //여분의 키
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                }
                // 여기에 startListening 호출 추가
                startListening(intent)
            }
        }
    }

    // 음성 듣는 listener
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            Toast.makeText(context, "이제 말씀하세요!", Toast.LENGTH_SHORT).show()
            //binding.tvState.text = "이제 말씀하세요!"
        }

        override fun onBeginningOfSpeech() {
            //binding.tvState.text = "잘 듣고 있어요."
        }

        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}

        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}

        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            //binding.tvState.text = "끝!"
            CoroutineScope(Dispatchers.Main).launch {
                /*delay(500)
                addChatItem(
                    requireContext().getString(R.string.ai_explain),
                    MessageType.AI_CHAT
                )*/

                //binding.tvState.text = "상태체크"
            }
        }

        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            Timber.e("$message")
        }

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0] // 첫 번째 인식 결과를 사용
                // text를 adapter에 넣으면 됨
                isUserTyping = true
                binding.etSearch.setText(text.toString())

                Timber.d( "인식된 메시지: $text")
            }
        }

        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}

        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    fun openReviewFragment() {
        resetFabState()
        val reviewFragment = ReviewFragment()
        parentFragmentManager.beginTransaction()
            .add(R.id.fcv_review, reviewFragment, "ReviewFragment")
            .commit()
    }

    fun refreshData(){
        setting()
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

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
                interval = 5000L
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback,
                Looper.getMainLooper()
            )

            mapView.getMapAsync(this) // 이미 초기화돼 있어도 괜찮음
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
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
            } else {
                Timber.e("권한 없음: 위치 요청 불가")
            }
        } else {
            Toast.makeText(requireContext(), "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }


}