package com.ksv.discountcards.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ksv.discountcards.R
import com.ksv.discountcards.databinding.FragmentHomeBinding
import com.ksv.discountcards.entity.Card
import com.ksv.discountcards.entity.OuterImage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CardsViewModel by activityViewModels()
    private val adapter =
        CardRecyclerAdapter(
            { onOpenCard(it) },
            { onItemClickListener(it) },
            { onItemLongClickListener(it) })
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { galleryUri ->
            try {
                cardTitleDialog(galleryUri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dispatcher = requireActivity().onBackPressedDispatcher
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("ksvlog", "isSelectMode: ${adapter.isSelectMode}")
                if (adapter.isSelectMode) {
                    adapter.unSelectAll()
                } else {
                    onBackPressedCallback.isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        dispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter

        binding.fab.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        viewModel.cards.onEach {
            adapter.setDate(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun cardTitleDialog(imageUri: Uri?) {
        imageUri?.let {
            binding.fab.visibility = View.GONE
            val view = layoutInflater.inflate(R.layout.set_title_dialog, null)
            view.findViewById<ImageView>(R.id.image_in_dialog).setImageURI(imageUri)
            AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("OK") { _, _ ->
                    val title =
                        view.findViewById<EditText>(R.id.title_in_dialog).text.toString().trim()
                    if (title.isNotEmpty()) {
                        val outerImage = OuterImage(imageUri, title)
                        viewModel.saveOuterImage(outerImage)
                    } else {
                        Toast
                            .makeText(
                                requireContext(),
                                "Надо ввести название карты",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
                .setNegativeButton("Отмена") { _, _ -> }
                .setOnDismissListener {
                    binding.fab.visibility = View.VISIBLE
                }
                .show()
        }
    }

    private fun onOpenCard(card: Card) {
        viewModel.selectCard(card)
        findNavController().navigate(R.id.action_homeFragment_to_showCardFragment)
    }

    private fun onItemClickListener(position: Int) {
        //adapter.onItemClick(position)
    }

    private fun onItemLongClickListener(position: Int) {
        //adapter.onItemLongClick(position)
    }
}