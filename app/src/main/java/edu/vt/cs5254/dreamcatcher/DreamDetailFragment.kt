package edu.vt.cs5254.dreamcatcher

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.dreamcatcher.databinding.FragmentDreamDetailBinding
import kotlinx.coroutines.launch
import java.io.File

class DreamDetailFragment : Fragment() {

    private val args: DreamDetailFragmentArgs by navArgs()
    private val vm: DreamDetailViewModel by viewModels {
        DreamDetailViewModelFactory(args.dreamId)
    }
    private var _binding: FragmentDreamDetailBinding? = null
    private val binding get() = checkNotNull(_binding) { "FragmentDreamDetailBinding is null" }
    private val formattedDate = "'Last updated' yyyy-MM-dd 'at' hh:mm:ss A"

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){ didTakePhoto ->
        if(didTakePhoto) {
            binding.dreamPhoto.tag = null
            vm.dream.value?.let { updatePhoto(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDreamDetailBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_dream_detail, menu)
                val intentTakePhoto = takePhoto.contract.createIntent(
                    requireContext(),
                    Uri.EMPTY
                )
                menu.findItem(R.id.take_photo_menu).isVisible = canResolveIntent(intentTakePhoto)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.take_photo_menu -> {
                        vm.dream.value?.let{ dream ->
                            val photoFile = File(
                                requireActivity().applicationContext.filesDir,
                                dream.photoFileName
                            )

                            val photoUri = FileProvider.getUriForFile(
                                requireContext(),
                                "edu.vt.cs5254.dreamcatcher.fileprovider",
                                photoFile
                            )
                            takePhoto.launch(photoUri)
                        }
                        true
                    }

                    R.id.share_dream_menu -> {
                        vm.dream.value?.let { shareDream(it) }
                        true
                    }

                    R.id.delete_dream_menu -> {
                        vm.dream.value?.let {
                            vm.deleteDream(it)
                            val photo = File(requireContext().applicationContext.filesDir, it.photoFileName)
                            if(photo.exists()){
                                photo.delete()
                            }
                            findNavController().navigateUp()
                        }
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getItemTouchHelper().attachToRecyclerView(binding.dreamEntryRecycler)
        binding.dreamEntryRecycler.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.dream.collect { dreamMain ->
                    dreamMain?.let { dream ->
                        binding.dreamPhoto.setOnClickListener {
                            findNavController()
                                .navigate(DreamDetailFragmentDirections.showPhotoDetail(dream.photoFileName))
                        }
                        updateView(dreamMain)
                        binding.dreamEntryRecycler.adapter = DreamEntryAdapter(dreamMain.entries)
                    }
                }
            }
        }

        binding.fulfilledCheckbox.setOnClickListener{
            vm.updateDream {oldDream ->
                oldDream.copy().apply { entries =
                    if(oldDream.isFulfilled){
                        oldDream.entries.filter { it.kind != DreamEntryKind.FULFILLED }
                    } else{
                        oldDream.entries + DreamEntry ( kind = DreamEntryKind.FULFILLED, dreamId = oldDream.id)
                    }
                }
            }
        }

        binding.deferredCheckbox.setOnClickListener {
            vm.updateDream {oldDream ->
                oldDream.copy().apply { entries =
                    if(oldDream.isDeferred){
                        oldDream.entries.filter { it.kind != DreamEntryKind.DEFERRED }
                    } else{
                        oldDream.entries + DreamEntry ( kind = DreamEntryKind.DEFERRED, dreamId = oldDream.id)
                    }
                }
            }
        }

        binding.titleText.doOnTextChanged { text, _, _, _ ->
            vm.updateDream { oldDream ->
                oldDream.copy(title = text.toString()).apply { entries = oldDream.entries }
            }
        }

        binding.addReflectionButton.setOnClickListener {
            findNavController().navigate(DreamDetailFragmentDirections.addReflection())
        }

        setFragmentResultListener(
            ReflectionDialogFragment.REQUEST_KEY_REFLECTION
        ) { _, bundle ->
            val newReflection = bundle.getString(ReflectionDialogFragment.BUNDLE_KEY_REFLECTION)
            newReflection?.let {
                vm.updateDream { oldDream ->
                    oldDream.copy().apply { entries = oldDream.entries + DreamEntry(text = newReflection, kind = DreamEntryKind.REFLECTION, dreamId = id) }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateView(dream: Dream){
        if (binding.titleText.text.toString() != dream.title){
            binding.titleText.setText(dream.title)
        }

        binding.lastUpdatedText.text = DateFormat.format(formattedDate, dream.lastUpdated)

        binding.fulfilledCheckbox.isChecked = dream.isFulfilled
        binding.fulfilledCheckbox.isEnabled = !dream.isDeferred

        binding.deferredCheckbox.isChecked = dream.isDeferred
        binding.deferredCheckbox.isEnabled = !dream.isFulfilled

        if (dream.isFulfilled) {
            binding.addReflectionButton.hide()
        } else {
            binding.addReflectionButton.show()
        }

        updatePhoto(dream)
    }

    private fun updatePhoto(dream: Dream){
        with (binding.dreamPhoto) {
            if (tag != dream.photoFileName) {
                val photoFile =
                    File(requireContext().applicationContext.filesDir, dream.photoFileName)
                if (photoFile.exists()) {
                    isEnabled = true
                    doOnLayout { imageView ->
                        val scaledBitmap = getScaledBitmap(
                            photoFile.path,
                            imageView.width,
                            imageView.height
                        )
                        setImageBitmap(scaledBitmap)
                        tag = dream.photoFileName
                    }
                } else {
                    isEnabled = false
                    setImageBitmap(null)
                    tag = null
                }
            }
        }
    }

    private fun Button.configureForEntry(entry: DreamEntry){
        text = entry.kind.toString()
        visibility = View.VISIBLE
        when(entry.kind){
            DreamEntryKind.REFLECTION -> {
                text = entry.text
                isAllCaps = false
                setBackgroundWithContrastingText("navy")
            }
            DreamEntryKind.DEFERRED -> {
                setBackgroundWithContrastingText("lightgrey")
            }
            DreamEntryKind.FULFILLED -> {
                setBackgroundWithContrastingText("purple")
            }
            DreamEntryKind.CONCEIVED -> {
                setBackgroundWithContrastingText("aqua")
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean{
        return requireActivity().packageManager
            .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null
    }

    private fun shareDream(dream: Dream) {
        val reportIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getDreamReport(dream))
            putExtra(Intent.EXTRA_SUBJECT, dream.title)
        }

        val sendIntent = Intent.createChooser(reportIntent, dream.title)
        startActivity(sendIntent)
    }

    private fun getDreamReport(dream: Dream): String {
        val formattedDate = "yyyy-MM-dd 'at' hh:mm:ss a"
        val dateString = DateFormat.format(formattedDate, dream.lastUpdated)

        val dreamReportBuilder = StringBuilder().apply {
            append(dream.title).append("\n").append(getString(R.string.last_updated_date, dateString))
        }

        val reflectionsList = dream.entries
            .filter { it.kind == DreamEntryKind.REFLECTION }
            .joinToString(separator = "\n") { entry ->
                " * ${entry.text}"
            }

        if (reflectionsList.isNotEmpty()) {
            dreamReportBuilder.append("\n\n").append(getString(R.string.reflection_text)).append("\n")
            dreamReportBuilder.append(reflectionsList).append("\n")
        }

        if (dream.isFulfilled) {
            dreamReportBuilder.append("\n").append(getString(R.string.fulfilled_text))
        }

        if (dream.isDeferred) {
            dreamReportBuilder.append("\n").append(getString(R.string.deferred_text))
        }

        return dreamReportBuilder.toString()
    }

    private fun getItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(object: ItemTouchHelper
        .SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false;
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val dreamHolder = viewHolder as DreamEntryHolder
                val swipedEntry = dreamHolder.boundEntry
                vm.updateDream { oldDream ->
                    oldDream.copy().apply {
                        entries = oldDream.entries.filter { it != swipedEntry }
                    }
                }
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dreamHolder = viewHolder as DreamEntryHolder
                val swipedEntry = dreamHolder.boundEntry
                return if (swipedEntry.kind == DreamEntryKind.REFLECTION) {
                    ItemTouchHelper.LEFT
                } else {
                    0
                }
            }

        })
    }
}