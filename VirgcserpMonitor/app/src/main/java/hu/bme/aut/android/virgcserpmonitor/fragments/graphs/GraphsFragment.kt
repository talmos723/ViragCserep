package hu.bme.aut.android.virgcserpmonitor.fragments.graphs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.virgcserpmonitor.R
import hu.bme.aut.android.virgcserpmonitor.databinding.FragmentGraphsBinding


class GraphsFragment : Fragment() {
    private lateinit var binding: FragmentGraphsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGraphsBinding.inflate(inflater, container, false)

        binding.vpGraphs.adapter = GraphsPageAdapter(this)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_graphsFragment_to_mainFragment)
        }

        return binding.root
    }
}