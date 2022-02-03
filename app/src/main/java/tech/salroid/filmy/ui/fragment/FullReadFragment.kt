package tech.salroid.filmy.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tech.salroid.filmy.databinding.ReadFullLayoutBinding

class FullReadFragment : BottomSheetDialogFragment() {

    private var _binding: ReadFullLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ReadFullLayoutBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.cross.setOnClickListener {
            dismiss()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewTitle.text = arguments?.getString("title", " ")
        binding.textViewDesc.text = arguments?.getString("desc", " ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}