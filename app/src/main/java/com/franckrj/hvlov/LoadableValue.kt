package com.franckrj.hvlov

/**
 * A container for a value with its state (LOADING, ERROR or LOADED) and an optional status message.
 *
 * @param T The type of the value to store.
 * @property status The status of the value (LOADING, ERROR or LOADED).
 * @property value The value to store.
 * @property message A message containing information about the status (error message, loading progress, etc).
 */
class LoadableValue<out T> private constructor(val status: Status, val value: T, val message: String) {
    companion object {
        /**
         * Helper function for creating an error [LoadableValue].
         *
         * @param T The type of the value to store.
         * @param newValue The value to store.
         * @param newMessage A message containing information about the status (an error message, for example).
         * @return The created LoadableValue.
         */
        fun <T> error(newValue: T, newMessage: String = ""): LoadableValue<T> =
            LoadableValue(Status.ERROR, newValue, newMessage)

        /**
         * Helper function for creating a loading [LoadableValue].
         *
         * @param T The type of the value to store.
         * @param newValue The value to store.
         * @param newMessage A message containing information about the status (loading progress, for example).
         * @return The created LoadableValue.
         */
        fun <T> loading(newValue: T, newMessage: String = ""): LoadableValue<T> =
            LoadableValue(Status.LOADING, newValue, newMessage)

        /**
         * Helper function for creating a loaded [LoadableValue].
         *
         * @param T The type of the value to store.
         * @param newValue The value to store.
         * @param newMessage A message containing information about the status.
         * @return The created LoadableValue.
         */
        fun <T> loaded(newValue: T, newMessage: String = ""): LoadableValue<T> =
            LoadableValue(Status.LOADED, newValue, newMessage)
    }

    enum class Status {
        ERROR,
        LOADING,
        LOADED
    }
}
