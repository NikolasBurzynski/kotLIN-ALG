import Matrix

fun main () {
    val A = Matrix.fromArray(Array(100000){i->10}, Pair(1000,100))
    val B = Matrix.fromArray(Array(100000){i->10}, Pair(100,1000))
    val C = Matrix.matmul(A, B)

    print(C)

}