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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoadableValue<*>

        if (status != other.status) return false
        if (value != other.value) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + message.hashCode()
        return result
    }
}
