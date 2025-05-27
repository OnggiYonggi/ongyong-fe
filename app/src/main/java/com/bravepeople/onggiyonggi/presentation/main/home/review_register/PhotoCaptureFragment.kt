package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentPhotoBinding
import com.bravepeople.onggiyonggi.extension.home.register.DeleteState
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class PhotoCaptureFragment : Fragment() {
    private var _binding: FragmentPhotoBinding? = null
    private val binding: FragmentPhotoBinding
        get() = requireNotNull(_binding) { "receipt fragment is null" }

    private lateinit var imageCapture: ImageCapture
    private val reviewRegisterViewModel: ReviewRegisterViewModel by activityViewModels()
    private val args: PhotoCaptureFragmentArgs by navArgs()
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentType: PhotoType

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("갤러리 권한 허용됨 → 카메라 권한 요청")
                checkCameraPermissionAndLoad()  // 갤러리 허용되면 바로 카메라 요청
            } else {
                Timber.e("갤러리 권한 거부됨")
                Toast.makeText(requireContext(), "갤러리 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(currentType)
            } else {
                Timber.e("카메라 권한 거부됨")
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // galleryLauncher 초기화
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageUri = result.data?.data
                    selectedImageUri?.let { uri ->
                        Timber.d("갤러리에서 선택된 URI: $uri")

                        if (currentType == PhotoType.RECEIPT) {
                            startCrop(uri)  // or startCrop(selectedImageUri)
                        } else {
                            //reviewRegisterViewModel.saveReceipt(uri)
                            navigateToLoading(uri)
                        }

                        /*val action =
                            PhotoCaptureFragmentDirections.actionPhotoToLoading(currentType)
                        findNavController().navigate(action)*/
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        currentType = args.photoType

        checkGalleryPermissionAndThenCamera()

        deleteStore()
        clickCancel()
    }

    // 갤러리 권한 설정
    private fun checkGalleryPermissionAndThenCamera() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(permission), GALLERY_PERMISSION_CODE)
        } else {
            checkCameraPermissionAndLoad()
        }
    }

    // 카메라 권한 설정
    private fun checkCameraPermissionAndLoad() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera(currentType)
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun startCamera(currentType: PhotoType) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.pvReceipt.surfaceProvider)
            }

            val displayMetrics = Resources.getSystem().displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(screenWidth, screenHeight))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            when (currentType) {
                PhotoType.RECEIPT -> binding.tvTitle.setText(R.string.receipt_photo_title)
                else -> binding.tvTitle.setText(R.string.food_photo_title)
            }

            binding.btnCapture.setOnClickListener {
                Timber.d("btnCapture 클릭됨")
                takePhoto(currentType)
            }

            loadLatestGalleryImage(currentType)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto(currentType: PhotoType) {
        val photoFile = File(
            requireContext().cacheDir,
            "photo_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Timber.d("사진 저장됨: $savedUri")
                    savePicture(savedUri, currentType)
                }

                override fun onError(exception: ImageCaptureException) {
                    Timber.e(exception, "사진 촬영 실패")
                }
            }
        )
    }

    private fun savePicture(savedUri: Uri, currentType: PhotoType) {
        Timber.d("savePicture() called with uri: $savedUri")

        if (currentType == PhotoType.RECEIPT) {
            startCrop(savedUri)  // or startCrop(selectedImageUri)
        } else {
            //reviewRegisterViewModel.saveReceipt(savedUri)
            navigateToLoading(savedUri)
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(
            File(
                requireContext().cacheDir,
                "cropped_${System.currentTimeMillis()}.jpg"
            )
        )

        val options = UCrop.Options().apply {
            //setCompressionQuality(80)
            setShowCropFrame(true)      // crop 프레임 보여주기
            setHideBottomControls(true) // 비율 버튼
            setFreeStyleCropEnabled(true)  // 자유로운 비율
            setCircleDimmedLayer(false)     // 원형 크롭 아닌 사각형 유지
            setToolbarTitle("영수증 자르기")
        }

        UCrop.of(uri, destinationUri)
            .withOptions(options)
            .start(requireContext(), this)
    }

    private fun navigateToLoading(uri: Uri) {
        try {
            Timber.d("photo-phototype: $currentType")

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.photoFragment, true)
                .build()

            val action = PhotoCaptureFragmentDirections.actionPhotoToLoading(currentType,
                uri.toString()
            )
            findNavController().navigate(action, navOptions)
        } catch (e: Exception) {
            Timber.e(e, "예외 발생: photoType 처리 중 오류")
        }
    }

    private fun loadLatestGalleryImage(currentType: PhotoType) {
        val cursor = context?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Timber.d("불러온 이미지 URI: $uri")

                if (canOpenUri(requireContext(), uri)) {
                    Timber.d("정상적인 URI: $uri")
                    binding.ivGalary.load(uri) {
                        placeholder(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.img_onggiyonggi
                            )
                        )
                        error(ContextCompat.getDrawable(requireContext(), R.drawable.ic_ban_red_48))
                    }

                    binding.ivGalary.setOnClickListener {
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.type = "image/*"
                        galleryLauncher.launch(intent)
                    }
                } else {
                    Timber.e("잘못된 URI라서 로드 불가: $uri")
                }
            }
        }
    }

    private fun canOpenUri(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun deleteStore() {
        lifecycleScope.launch {
            reviewRegisterViewModel.deleteState.collect { state ->
                when (state) {
                    is DeleteState.Success -> {
                        requireActivity().finish()
                    }

                    is DeleteState.Loading -> {}
                    is DeleteState.Error -> {
                        Timber.e("delete state error!")
                    }
                }
            }
        }

    }

    private fun clickCancel() {
        binding.btnCancel.setOnClickListener {
            Timber.e("storeId: ${reviewRegisterViewModel.storeId.value}")
            if (reviewRegisterViewModel.newStore.value == true) reviewRegisterViewModel.delete()
            else requireActivity().finish()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Timber.e("storeId: ${reviewRegisterViewModel.storeId.value}")
            if (reviewRegisterViewModel.newStore.value == true) reviewRegisterViewModel.delete()
            else requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val GALLERY_REQUEST_CODE = 1001
        private const val GALLERY_PERMISSION_CODE = 101
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                Timber.d("크롭된 URI: $it")
                //reviewRegisterViewModel.saveReceipt(it)
                navigateToLoading(it)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Timber.e(cropError, "사진 자르기 실패")
            Toast.makeText(requireContext(), "사진 자르기에 실패했어요", Toast.LENGTH_SHORT).show()
        }
    }

}