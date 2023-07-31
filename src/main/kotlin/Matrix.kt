import kotlin.jvm.Throws
import kotlin.math.abs

class Matrix (private val rows: Int, private val cols: Int) {

    constructor(size : Pair<Int, Int>) : this(size.first, size.second)

    enum class AXIS {
        COLUMN_WISE, ROW_WISE, BOTH
    }

    private val data = Array(rows * cols){0f}
    val size = Pair(rows, cols)
    private val length = rows * cols


    private fun indx(i:Int, j:Int): Int {
        require(i * cols + j < length) {"Index out of bounds for this Matrix"}
        return i*cols+j
    }

    private fun unsafe_indx(i:Int, j:Int): Int { //this is used when there is no need to check for out of bounds exception
        return i*cols+j
    }

    operator fun get(i: Int, j: Int): Float {
        return data[indx(i,j)]
    }

    operator fun set(i:Int, j:Int, b:Float) {
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
        builder.append("]\n")
        return builder.toString()
    }

    fun arg_max(axis: AXIS, indx: Int): Int {
        return when (axis) {
            AXIS.ROW_WISE -> {
                val slice = data.slice(indx*cols until indx*cols + cols)
                val idx = data.indexOf(slice.max())
                idx
            }
            AXIS.COLUMN_WISE -> {
                val slice = data.filterIndexed { index, _ -> index % cols == indx }
                val idx = data.indexOf(slice.max())
                idx
            }
            AXIS.BOTH -> {
                val idx = data.indexOf(data.max())
                idx
            }
        }
    }

    private fun scale(row: Int, scalar: Float) {
        for(col in 0 until cols) {
            data[unsafe_indx(row, col)] *= scalar
        }
    }

    public fun matScale(scalar: Float) {
        for(i in data.indices) {
            data[i] *= scalar
        }
    }

    private fun op3(row1: Int, row2: Int, scalar: Float) {
        for(col in 0 until cols) {
            data[unsafe_indx(row2, col)] = scalar * data[unsafe_indx(row1, col)] + data[unsafe_indx(row2, col)]
        }
    }

//    fun findInverse()

    fun toRREF(): Matrix {
        var m = rows
        var n = cols
        var r = -1

        for(j in 0 until n){
            var i = r + 1
            while (i < m && data[unsafe_indx(i , j)] == 0f) {
                i += 1
            }
            if (i < m) {
                r += 1
                swap(i , r)
                scale(r, (1/data[unsafe_indx(r, j)]))
                for (k in 0 until m) {
                    if(k != r) {
                        op3(r, k, -1 * data[unsafe_indx(k,j)])
                    }
                }
            }
        }
        return this
    }

    fun toREF(): Unit{
        var h = 0 //Pivot row
        var k = 0 //Pivot col
        val m = rows
        val n = cols

        while (h < m && k < n) {
            // Find the Kth pivot
            var maxRow = 0
            var maxVal = 0f

            //ARGMAX from h to m
            for(i in h until m) {
                if (abs(data[unsafe_indx(i,k)]) > maxVal) {
                    maxVal = abs(data[unsafe_indx(i,k)])
                    maxRow = i
                }
            }


            if(data[unsafe_indx(maxRow, k)] == 0f) {
                //No Pivot in this column, pass to the next column
                k += 1
            } else {
                swap(h, maxRow)
                for (i in (h + 1) until m) {
                    val f: Float = data[unsafe_indx(i , k)] / data[unsafe_indx(h , k)]
                    println(f)
                    data[unsafe_indx(i,k)] = 0f
                    for (j in (k + 1) until n) {
                        data[unsafe_indx(i , j)] = data[unsafe_indx(i , j)] - (data[unsafe_indx(h , j)] * f)
                    }
                }
                h += 1
                k += 1
            }
        }
    }

    fun swap(row1:Int, row2:Int) {
        require(row1 in 0 until rows && row2 in 0 until rows) {"$row1 or $row2 out of range for matrix with $rows rows"}
        val row1Index = unsafe_indx(row1, 0)
        val row2Index = unsafe_indx(row2, 0)
        for (i in 0 until cols) {
            val temp = data[row1Index+i]
            data[row1Index + i] = data[row2Index + i]
            data[row2Index + i] = temp
        }
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

        private fun dot(A: List<Float>, B: List<Float>): Float {
            assert(A.size == B.size)
            var acc = 0f
            for(i in A.indices) {
                acc += A[i] * B[i];
            }
            return acc
        }

        fun fromArray(input: Array<Float>, pair: Pair<Int, Int>): Matrix {
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
            val col_map = HashMap<Int, List<Float>>()
            for(i in 0 until A.rows) {
                val row = A.data.slice(i*A.cols until i*A.cols + A.cols)
                for(j in 0 until B.cols) {
                    var col : List<Float>
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

        fun scale(A: Matrix, scalar: Float) : Matrix {
            val res  = Matrix(A.size)
            for (i in res.data.indices) {
                res.data[i] = A.data[i] * scalar
            }
            return res
        }

        @Throws
        fun concat(A: Matrix, B: Matrix, dim: AXIS): Matrix {
            return when(dim) {
                AXIS.COLUMN_WISE -> {
                    // When we concatenate row wise, we have the same number of rows but the columns number is the same
                    require(A.rows == B.rows) {"Cannot concatenate a matrix with ${A.rows} rows with a ${B.rows} rowed matrix column-wise"}
                    val res = Matrix(A.rows, A.cols + B.cols)
                    for(i in 0 until A.rows) {
                        for(j in 0 until A.cols) {
                            res.data[res.unsafe_indx(i,j)] = A.data[A.unsafe_indx(i,j)]
                        }
                        for(j in 0 until B.cols) {
                            res.data[res.unsafe_indx(i,A.cols + j)] = B.data[B.unsafe_indx(i,j)]
                        }
                    }
                    res
                }
                AXIS.ROW_WISE -> {
                    require(A.cols == B.cols) {"Cannot concatenate a matrix with ${A.cols} columns with a ${B.cols} columned matrix row-wise"}
                    val res = Matrix(A.rows + B.rows, A.cols)
                    for(i in 0 until A.data.size) {
                        res.data[i] = A.data[i]
                    }
                    for(i in 0 until B.data.size) {
                        res.data[A.data.size + i] = B.data[i]
                    }
                    res
                }
                else -> {
                    throw IllegalArgumentException("dim argument cannot be AXIS.Both for concatenation axis")
                }
            }
        }

        fun split(index : Int, axis:Matrix): Pair<Matrix, Matrix> {

        }

        fun getInverse(A: Matrix) : Matrix {
            require(A.cols == A.rows){"Haven't implemented left and right non equal inverses yet"}
            val res = Matrix.concat(A, Matrix.getIdentity(A.cols,A.cols), Matrix.AXIS.COLUMN_WISE).toRREF()

        }

        fun getIdentity(cols: Int, rows: Int) : Matrix {
            require(cols == rows) {"You cannot have a non-square identity matrix, make sure rows == cols"}
            val res = Matrix(cols, rows)
            for(i in 0 until cols) {
                res.data[res.unsafe_indx(i, i)] = 1f
            }
            return res
        }
    }

}