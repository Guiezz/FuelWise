package com.example.exemplosimplesdecompose.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostoPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("postos_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "postos_list"

    fun salvarPosto(posto: Posto) {
        val listaAtual = getPostos().toMutableList()
        listaAtual.add(posto)
        prefs.edit().putString(key, gson.toJson(listaAtual)).apply()
    }

    private fun salvarPostos(lista: List<Posto>) {
        prefs.edit().putString(key, gson.toJson(lista)).apply()
    }


    fun getPostos(): List<Posto> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<Posto>>() {}.type
        return gson.fromJson(json, type)
    }

    fun deletarPosto(index: Int) {
        val lista = getPostos().toMutableList()
        if (index in lista.indices) {
            lista.removeAt(index)
            prefs.edit().putString(key, gson.toJson(lista)).apply()
        }
    }

    fun atualizarPosto(index: Int, novoPosto: Posto) {
        val lista = getPostos().toMutableList()
        if (index in lista.indices) {
            lista[index] = novoPosto
            salvarPostos(lista)
        }
    }

    fun limparTodos() {
        prefs.edit().remove(key).apply()
    }
}
