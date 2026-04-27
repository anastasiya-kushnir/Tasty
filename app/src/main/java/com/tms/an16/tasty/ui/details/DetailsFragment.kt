package com.tms.an16.tasty.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tms.an16.tasty.ui.theme.TastyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TastyTheme {
                    DetailsScreen(
                        viewModel = viewModel,
                        onBackClick = {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    )
                }
            }
        }
    }
}