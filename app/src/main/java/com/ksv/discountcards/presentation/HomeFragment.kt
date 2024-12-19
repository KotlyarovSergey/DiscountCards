package com.ksv.discountcards.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
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
            { onCanBeEditItem(it) },
            { onCanBeDeleteItems(it) }
        )
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { galleryUri ->
            try {
                addCardTitleDialog(galleryUri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var mainMenu: Menu
    private var canBeDeleteCards: List<Card> = emptyList()
    private var canBeEditPositionItem: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dispatcher = requireActivity().onBackPressedDispatcher
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        addMenuProvider()

        binding.recycler.adapter = adapter

        binding.fab.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        viewModel.cards.onEach {
            adapter.submitList(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun addMenuProvider() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                mainMenu = menu
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        canBeEditPositionItem?.let{
                            val card = viewModel.cards.value[canBeEditPositionItem!!]
                            viewModel.selectCard(card)
                            adapter.unSelectAll()
                            findNavController().navigate(R.id.action_homeFragment_to_editCardFragment)
                        }
                        true
                    }
                    R.id.menu_delete -> {
                        val deleted = canBeDeleteCards
                        adapter.unSelectAll()
                        viewModel.deleteCards(deleted)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun addCardTitleDialog(imageUri: Uri?) {
        imageUri?.let {
            binding.fab.visibility = View.GONE
            val view = layoutInflater.inflate(R.layout.set_title_dialog, null)
            view.findViewById<ImageView>(R.id.image_in_dialog).setImageURI(imageUri)
            AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("OK") { _, _ ->
                    val title =
                        view.findViewById<EditText>(R.id.title_in_dialog).text.toString().trim()
                    val outerImage = OuterImage(imageUri, title)
                    viewModel.saveOuterImage(outerImage)
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

    private fun onCanBeDeleteItems(deletedList: List<Card>){
        canBeDeleteCards = deletedList
        mainMenu.findItem(R.id.menu_delete).isVisible = canBeDeleteCards.isNotEmpty()
    }

    private fun onCanBeEditItem(position: Int?){
        canBeEditPositionItem = position
        mainMenu.findItem(R.id.menu_edit).isVisible = (position != null)
    }
}