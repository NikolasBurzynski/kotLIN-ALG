class Matrix (private val rows: Int, private val cols: Int) {
    private val data = Array(rows * cols){0}
    val size = Pair(rows, cols)
    private val length = rows * cols


    private fun indx(i:Int, j:Int): Int {
        require(i * cols + j < length) {"Index out of bounds for this Matrix"}
        return i*cols+j
    }

    private fun unsafe_indx(i:Int, j:Int): Int { //this is used when there is no need to check for out of bounds exception
        return i*cols+j
    }

    operator fun get(i: Int, j: Int): Int {
        return data[indx(i,j)]
    }

    operator fun set(i:Int, j:Int, b:Int) {
        data[indx(i,j)] = b
    }

    operator fun plus(B: Matrix): Matrix {
        return Matrix.sum(this, B)
    }

    operator fun plusAssign(B: Matrix): Unit {
        this.sum(B)
    }

    operator fun minus(B: Matrix): Matrix {
        return Matrix.sub(this, B)
    }

    operator fun minusAssign(B: Matrix): Unit {
        this.sub(B)
    }

    override fun toString(): String {
        var builder = StringBuilder()
        builder.append("[\n")
        for (row in 0 until rows) {
            builder.append("\t")
            for (col in 0 until cols) {
                builder.append("${data[unsafe_indx(row, col)]}, ")
            }
            builder.append("\n")
        }
        builder.append("]")
        return builder.toString()
    }

    fun sum(B: Matrix) {
        require(this.size == B.size){"Matrix operand's sizes do not match"}
        for(i in 0 until this.length){
            this.data[i] += B.data[i]
        }
    }

    fun sub(B: Matrix) {
        require(this.size == B.size){"Matrix operand's sizes do not match"}
        for(i in 0 until this.length){
            this.data[i] -= B.data[i]
        }
    }



    companion object {
        fun sum(A: Matrix, B: Matrix): Matrix {
            require(A.size == B.size){"Matrix operand's sizes do not match"}
            val res = Matrix(A.rows, A.cols)
            for(i in 0 until A.length){
                res.data[i] = A.data[i] + B.data[i]
            }
            return res;
        }
        fun sub(A: Matrix, B: Matrix): Matrix {
            require(A.size == B.size){"Matrix operand's sizes do not match"}
            val res = Matrix(A.rows, A.cols)
            for(i in 0 until A.length){
                res.data[i] = A.data[i] - B.data[i]
            }
            return res;
        }

        private fun dot(A: List<Int>, B: List<Int>): Int {
            assert(A.size == B.size)
            var acc = 0
            for(i in A.indices) {
                acc += A[i] * B[i];
            }
            return acc
        }

        fun fromArray(input: Array<Int>, pair: Pair<Int, Int>): Matrix {
            require(input.size == pair.first * pair.second) {"Cannot make a ${pair.first}x${pair.second} matrix with ${input.size} elements"}
            val res = Matrix(pair.first, pair.second);
            for(i in input.indices) {
                res.data[i] = input[i]
            }
            return res

        }

        fun matmul(A: Matrix, B: Matrix): Matrix {
            require(A.cols == B.rows){"The column size of Operand 1 must equal the row size of Operand 2 to multiply matrices"}
            val res = Matrix(A.rows, B.cols)
            val col_map = HashMap<Int, List<Int>>()
            for(i in 0 until A.rows) {
                val row = A.data.slice(i*A.cols until i*A.cols + A.cols)
                for(j in 0 until B.cols) {
                    var col : List<Int>
                    if(j in col_map.keys) col = col_map[j]!!
                    else {
                        col = B.data.filterIndexed { index, _ -> index % B.cols == j }
                        col_map[j] = col
                    }
                    res[i, j] = dot(row, col)
                }
            }
            return res
        }
    }

}