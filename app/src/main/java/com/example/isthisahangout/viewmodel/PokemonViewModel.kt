package com.example.isthisahangout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.isthisahangout.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    pokemonRepository: PokemonRepository
) : ViewModel() {
    var refreshInProgress = false
    val pokemon = pokemonRepository.getPokemonPaginated().cachedIn(viewModelScope)
    val currentPokemon = MutableStateFlow("")
    val pokemonDetailsResult = currentPokemon.flatMapLatest { pokemonName ->
        if(pokemonName.isNotBlank()){
            pokemonRepository.getPokemonDetails(pokemonName)
        } else emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.Lazily,null)

    fun setPokemonName(name: String){
        currentPokemon.value = name
    }
}