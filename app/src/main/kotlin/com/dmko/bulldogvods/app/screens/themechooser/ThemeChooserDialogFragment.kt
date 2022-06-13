package com.dmko.bulldogvods.app.screens.themechooser

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.BuildConfig
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.databinding.DialogFragmentThemeChooserBinding
import com.dmko.bulldogvods.features.theming.domain.entities.Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeChooserDialogFragment : AppCompatDialogFragment(R.layout.dialog_fragment_theme_chooser) {

    private val viewModel: ThemeChooserViewModel by viewModels()
    private val binding by viewBinding(DialogFragmentThemeChooserBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.layoutThemeDark.setOnClickListener { viewModel.onThemeClicked(Theme.DARK) }
        binding.layoutThemeLight.setOnClickListener { viewModel.onThemeClicked(Theme.LIGHT) }
        binding.layoutThemeSystemDefault.setOnClickListener { viewModel.onThemeClicked(Theme.SYSTEM_DEFAULT) }
        binding.appVersionText.text = getString(R.string.dialog_theme_chooser_app_version, BuildConfig.VERSION_NAME)

        viewModel.selectedThemeLiveData.observe(viewLifecycleOwner, ::showSelectedTheme)
    }

    private fun showSelectedTheme(theme: Theme) {
        binding.imageCheckThemeDark.isInvisible = theme != Theme.DARK
        binding.imageCheckThemeLight.isInvisible = theme != Theme.LIGHT
        binding.imageCheckThemeSystemDefault.isInvisible = theme != Theme.SYSTEM_DEFAULT
    }
}
