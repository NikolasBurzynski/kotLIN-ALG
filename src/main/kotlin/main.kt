import Matrix

fun main () {
    val A = Matrix.fromArray(Array(1000000){it}, Pair(1000,1000))
    val B = Matrix.fromArray(Array(1000000){it}, Pair(1000,1000))

    print(Matrix.concat(A, B, Matrix.AXIS.COLUMN_WISE))
}