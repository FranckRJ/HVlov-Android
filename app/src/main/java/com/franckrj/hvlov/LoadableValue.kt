package com.franckrj.hvlov

class LoadableValue<out T> private constructor(val status: Status, val value: T, val message: String) {
    companion object {
        fun <T> error(newValue: T, newMessage: String = ""): LoadableValue<T> =
            LoadableValue(Status.ERROR, newValue, newMessage)

        fun <T> loading(newValue: T, newMessage: String = ""): LoadableValue<T> =
            LoadableValue(Status.LOADING, newValue, newMessage)

        fun <T> loaded(newValue: T, newMessage: String = ""): LoadableValue<T> =
            LoadableValue(Status.LOADED, newValue, newMessage)
    }

    enum class Status {
        ERROR,
        LOADING,
        LOADED
    }
}
