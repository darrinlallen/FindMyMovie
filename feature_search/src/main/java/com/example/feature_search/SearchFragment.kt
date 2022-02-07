package com.example.feature_search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.feature_search.adapter.MediaItemsAdapter
import com.example.feature_search.databinding.FragmentSearchBinding
import com.example.feature_search.util.ViewState
import com.example.feature_search.util.hideKeyboard
import com.example.feature_search.viewmodel.SearchViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()

    private val mediaItemsAdapter by lazy { MediaItemsAdapter() }

    /**
     * Listener for SearchView
     */
    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            // if query is NOT NULL, update query in SearchViewModel to be validate and possible start search
            if (query != null) viewModel.query = query
            hideKeyboard() // hide keyboard anytime search action happens
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            binding.tvNoQuery.isVisible = mediaItemsAdapter.itemCount < 1
            return true
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSearchBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() = with(binding) {
        svQuery.run { clearFocus();setOnQueryTextListener(queryTextListener) }
        rvResults.adapter = mediaItemsAdapter
    }

    private fun initObservers() = with(viewModel) {
        viewState.observe(viewLifecycleOwner) { state ->
            binding.loading.isVisible = state is ViewState.Loading
            if (state is ViewState.Success) mediaItemsAdapter.submitList(state.mediaItems)
            if (state is ViewState.Failure) handleError(state)
            binding.tvNoQuery.isVisible = mediaItemsAdapter.itemCount < 1
        }
    }

    private fun handleError(state: ViewState.Failure) {
        val errorMsg = when (state) {
            is ViewState.Failure.NoMediaItemsFound -> "NoMediaItemsFound"
            is ViewState.Failure.SomethingWentWrong -> "SomethingWentWrong"
            is ViewState.Failure.InvalidQuery -> "InvalidQuery"
            is ViewState.Failure.WhileFetching -> state.exception
        }
        Log.d("SearchFragment", "handleError: $errorMsg")
    }

}