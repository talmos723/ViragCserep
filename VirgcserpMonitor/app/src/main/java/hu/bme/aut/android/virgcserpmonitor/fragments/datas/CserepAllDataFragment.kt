package hu.bme.aut.android.virgcserpmonitor.fragments.datas

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.virgcserpmonitor.adapter.StateAdapter
import hu.bme.aut.android.virgcserpmonitor.data.DataAccess
import hu.bme.aut.android.virgcserpmonitor.data.StatesDataHolder
import hu.bme.aut.android.virgcserpmonitor.databinding.FragmentCserepAllDataBinding
import hu.bme.aut.android.virgcserpmonitor.network.WebSocketManager
import kotlin.concurrent.withLock


class CserepAllDataFragment : Fragment() {

    private lateinit var binding: FragmentCserepAllDataBinding
    private lateinit var adapter: StateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCserepAllDataBinding.inflate(inflater, container, false)

        binding.pullToRefresh.setOnRefreshListener {
            DataAccess.refresh()
            binding.pullToRefresh.isRefreshing = false
        }
        initRecyclerView()

        binding.fab.setOnClickListener {
            binding.mainRecyclerView.scrollToPosition(0)
            //binding.mainRecyclerView.smoothScrollToPosition(0)
        }

        binding.mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    Handler().postDelayed(
                        { binding.fab.setVisibility(View.GONE) },
                        2000
                    )
                } else if (dy < 0) {
                    binding.fab.setVisibility(View.VISIBLE)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Handler().postDelayed(
                        { binding.fab.setVisibility(View.GONE) },
                        2000
                    )
                }
            }
        })

        return binding.root
    }

    private fun initRecyclerView() {
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = StateAdapter()

        //DataAccess.refresh()
        if (StatesDataHolder.getSize() == 0)
            DataAccess.loadStateData()
        else {
            for (i in 0 until StatesDataHolder.states.size)
                adapter.notifyItemInserted(i)
        }

        binding.mainRecyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        WebSocketManager.close()
    }

}
