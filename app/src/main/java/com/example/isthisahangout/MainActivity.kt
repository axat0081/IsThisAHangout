package com.example.isthisahangout

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.isthisahangout.databinding.ActivityMainBinding
import com.example.isthisahangout.databinding.NavHeaderBinding
import com.example.isthisahangout.utils.ConnectionLiveData
import com.example.isthisahangout.viewmodel.FirebaseAuthViewModel
import com.example.isthisahangout.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnLoadingStateChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private val viewModel by viewModels<FirebaseAuthViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()

    companion object {
        var userName: String = "abc"
        var userPfp: String = "123"
        var userId: String = "efrvwev"
        var userHeader: String = "d3ddwfewf"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this) {
            binding.bottomSheet.isVisible = !it
        }
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment2,
                R.id.chatsFragment2,
                R.id.videosFragment2,
                R.id.postsFragment2,
                R.id.songFragment,
                R.id.loginFragment
            ),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)
        binding.navView.setupWithNavController(navController)
        val headerView = binding.navView.getHeaderView(0)
        val headerBinding = NavHeaderBinding.bind(headerView)
        this.lifecycleScope.launchWhenStarted {
            viewModel.username.collectLatest { name ->
                headerBinding.navHeaderUsernameTextView.text = name
            }
        }
        this.lifecycleScope.launchWhenStarted {
            viewModel.userPfp.collectLatest { image ->
                if (image.isNotBlank()) {
                    Glide.with(applicationContext)
                        .load(image)
                        .into(headerBinding.navHeaderPfpImageview)
                }
            }
        }
        this.lifecycleScope.launchWhenStarted {
            viewModel.userId.collectLatest { id ->
                userId = id
            }
        }
        this.lifecycleScope.launchWhenStarted {
            viewModel.userPfp.collectLatest { pfp ->
                userPfp = pfp
            }
        }
        this.lifecycleScope.launchWhenStarted {
            viewModel.username.collectLatest { name ->
                userName = name
            }
        }
        this.lifecycleScope.launchWhenStarted {
            viewModel.userHeader.collectLatest { header ->
                userHeader = header
            }
        }
        navHostFragment.findNavController()
            .addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.homeFragment2 -> {
                        binding.bottomNav.isVisible = true
                    }
                    R.id.chatsFragment2 -> {
                        binding.bottomNav.isVisible = true
                    }
                    R.id.videosFragment2 -> {
                        binding.bottomNav.isVisible = true
                    }
                    R.id.postsFragment2 -> {
                        binding.bottomNav.isVisible = true
                    }
                    R.id.songFragment -> {
                        binding.bottomNav.isVisible = true
                    }
                    else -> {
                        binding.bottomNav.isVisible = false
                    }
                }
            }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.showProgressBar.collectLatest {
                    binding.mainProgressBar.isVisible = it
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun toggleLoadingState(state: Boolean) {
        mainViewModel.toggleProgressBarState(state)
    }
}

interface OnLoadingStateChangeListener {
    fun toggleLoadingState(state: Boolean)
}