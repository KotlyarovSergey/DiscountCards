package com.ksv.discountcards.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ksv.discountcards.databinding.FragmentShowCardBinding

class ShowCardFragment : Fragment() {
    private var _binding: FragmentShowCardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CardsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowCardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.cardImage.setImageURI(Uri.parse("content://com.android.providers.media.documents/document/image%3A32"))
        binding.cardImage.setImageURI(viewModel.selectedCard)
        binding.status.text = viewModel.selectedCard.toString()
    }
}