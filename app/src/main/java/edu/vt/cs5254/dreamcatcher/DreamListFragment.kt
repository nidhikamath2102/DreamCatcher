package edu.vt.cs5254.dreamcatcher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.dreamcatcher.Constants.Companion.isDarkTheme
import edu.vt.cs5254.dreamcatcher.databinding.FragmentDreamListBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class DreamListFragment : Fragment() {

    private var _binding: FragmentDreamListBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "FragmentDreamListBinding is null!" }
    private val vm: DreamListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDreamListBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_dream_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_dream ->  {
                        showNewDream()
                        return true
                    }
                    R.id.theme_toggle -> {
                        toggleTheme()
                        return true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getItemTouchHelper().attachToRecyclerView(binding.dreamRecyclerView)

        binding.dreamRecyclerView.layoutManager = LinearLayoutManager(context)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                vm.dreams.collect{ dreams ->

                    if(dreams.isEmpty()) {
                        binding.noDreamText.visibility = View.VISIBLE
                        binding.noDreamAddButton.visibility = View.VISIBLE
                        binding.dreamRecyclerView.visibility = View.GONE
                    } else {
                        binding.noDreamText.visibility = View.GONE
                        binding.noDreamAddButton.visibility = View.GONE
                        binding.dreamRecyclerView.visibility = View.VISIBLE
                    }

                    binding.dreamRecyclerView.adapter = DreamListAdapter(dreams) { dreamId ->
                        findNavController().navigate(DreamListFragmentDirections.showDreamDetail(dreamId))
                    }
                }
            }
        }

        binding.noDreamAddButton.setOnClickListener {
            showNewDream()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNewDream(){
        lifecycleScope.launch(){
            val dream = Dream()
            vm.insertDream(dream)
            findNavController()
                .navigate(DreamListFragmentDirections.showDreamDetail(dream.id))
        }
    }

    private fun getItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val dreamHolder = viewHolder as DreamHolder
                val swipedDream = dreamHolder.boundDream
                val photo = File(requireContext().applicationContext.filesDir, swipedDream.photoFileName)
                if(photo.exists()){
                    photo.delete()
                }
                vm.deleteDream(swipedDream)
            }

        })
    }

    private fun toggleTheme() {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        isDarkTheme = !isDarkTheme
    }
}