package com.example.weatherapp.domain.core

import com.example.weatherapp.domain.model.ApiException
import com.github.michaelbull.result.Err
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.catchInternal(action: suspend FlowCollector<T>.(Err<ApiException>) -> Unit): Flow<T> =
    catch { error ->
        action(Err(ApiException.Internal(error.cause, error.message)))
    }

fun <T> Flow<T>.catchErr(action: suspend FlowCollector<T>.(Err<ApiException>) -> Unit): Flow<T> =
    catch { error ->
        action(Err(ApiException.Internal(error.cause, error.message)))
    }

inline fun <T> Flow<T>.onLoading(crossinline action: suspend FlowCollector<T>.(isLoading: Boolean) -> Unit) =
    this.onStart { action(true) }.onCompletion { action(false) }
