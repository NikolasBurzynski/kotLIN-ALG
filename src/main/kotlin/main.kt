import Matrix

fun main () {
    val A = Matrix.fromArray(arrayOf(1,2,3,4,5,6), Pair(3,2))
    A.swap(1,2)

    print(A)

}