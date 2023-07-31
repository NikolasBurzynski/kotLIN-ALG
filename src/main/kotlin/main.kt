fun main () {
    val A = Matrix.fromArray(arrayOf(4f, -1f, -6f, 10f, -7f, -2f), Pair(2, 3))

    val pair = Matrix.split(A, 3, Matrix.AXIS.COLUMN_WISE)
    print(pair.first)
    print(pair.second)

}