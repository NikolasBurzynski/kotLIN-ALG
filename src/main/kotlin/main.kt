fun main () {
    val A = Matrix.fromArray(arrayOf(1f,2f,3f,4f,5f,6f,10f,8f,9f), Pair(3, 3))
    val AP = Matrix.getInverse(A)
    print(A)
    print(AP)
}