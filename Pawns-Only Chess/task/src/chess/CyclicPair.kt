package chess

/**
 * Utility class representing a pair over whose components we can iterate cyclically
 * starting on the first component of the pair
 * @param pair pair with the components that we want to iterate over
 */
class CyclicPair<T>(private val pair: Pair<T, T>) : Iterator<T> {
    /** Constructor from the components directly */
    constructor(first: T, second: T) : this(Pair(first, second))

    /** Counter to keep track of the iterator's current element */
    private var i = 0

    /** Checks if the iterator has a next element. Always true */
    override fun hasNext(): Boolean {
        return true
    }

    /** Advances the iterator to the next position and returns its element */
    override fun next(): T {
        i++
        return current()
    }

    /** Returns the component on the current position of the iterator */
    fun current(): T {
        return if (i % 2 == 0) pair.first
        else pair.second
    }

    /** Returns the component on the next position of the iterator without advancing it */
    fun peekNext(): T {
        return if (i % 2 == 0) pair.second
        else pair.first
    }

    /** Returns the component on the previous position of the iterator */
    fun last() = peekNext()

    /** Resets the iterator to the original position */
    fun reset() {
        i = 0
    }
}
