package com.zero.android.common.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Result<out T> {
	data class Success<T>(val data: T) : Result<T>
	data class Error(val exception: Throwable? = null) : Result<Nothing>
	object Loading : Result<Nothing>
	object None : Result<Nothing>
}

val Result<*>.isSuccess
	get() = this is Result.Success

val <T> Result<T>.data
	get() = if (this is Result.Success) this.data else null

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
	return this.map<T, Result<T>> { Result.Success(it) }
		.onStart { emit(Result.Loading) }
		.catch { emit(Result.Error(it)) }
}

suspend inline fun <T> Flow<Result<T>>.data() = firstOrNull()?.data
