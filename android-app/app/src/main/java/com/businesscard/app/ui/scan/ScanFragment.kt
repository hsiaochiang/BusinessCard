package com.businesscard.app.ui.scan

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.businesscard.app.BusinessCardApp
import com.businesscard.app.R
import com.businesscard.app.databinding.FragmentScanBinding
import com.businesscard.app.domain.create.CreateContactUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScanViewModel by viewModels {
        val app = requireActivity().application as BusinessCardApp
        val repo = app.appContainer.contactRepository
        val workManager = WorkManager.getInstance(requireContext())
        val useCase = CreateContactUseCase(repo, workManager)
        ScanViewModelFactory(requireActivity().application, useCase)
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                captureLauncher.launch(null)
            } else {
                binding.statusText.text = "需要相機權限以掃描名片"
            }
        }

    private val captureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            viewModel.onImageCaptured(bitmap)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scanButton.setOnClickListener {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        binding.retryButton.setOnClickListener {
            viewModel.onUploadTriggered()
        }
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is ScanUiState.Idle -> binding.statusText.text = getString(R.string.scan_hint)
                    is ScanUiState.Processing -> binding.statusText.text = "處理影像中…"
                    is ScanUiState.Uploading -> binding.statusText.text = "上傳排程中…"
                    is ScanUiState.Uploaded -> binding.statusText.text = "上傳已排入，待背景同步"
                    is ScanUiState.Error -> binding.statusText.text = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
