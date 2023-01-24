package tech.salroid.filmy.ui.full

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tech.salroid.filmy.databinding.ReadFullLayoutBinding

class FullReadFragment : BottomSheetDialogFragment() {

    private var _binding: ReadFullLayoutBinding? = null
    private val binding get() = _binding!!

    companion object{
        const val TITLE = "TITLE"
        const val DESCRIPTION = "DESCRIPTION"

        fun newInstance(title: String?, bio: String?): FullReadFragment {
            val args = Bundle()
            args.putString(TITLE, title)
            args.putString(DESCRIPTION, bio)
            val fragment = FullReadFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ReadFullLayoutBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.cross.setOnClickListener {
            binding.cross.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            dismiss()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewTitle.text = arguments?.getString(TITLE, " ")
        binding.textViewDesc.text = arguments?.getString(DESCRIPTION, " ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}