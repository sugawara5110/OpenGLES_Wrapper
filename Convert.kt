package jp.sugasato.opengles_wrapperkt

import kotlin.math.cos
import kotlin.math.sin

fun MatrixScaling(mat: FloatArray, sx: Float, sy: Float, sz: Float) {
    mat[0] = sx; mat[1] = 0.0f; mat[2] = 0.0f; mat[3] = 0.0f
    mat[4] = 0.0f; mat[5] = sy; mat[6] = 0.0f; mat[7] = 0.0f
    mat[8] = 0.0f; mat[9] = 0.0f; mat[10] = sz; mat[11] = 0.0f
    mat[12] = 0.0f; mat[13] = 0.0f; mat[14] = 0.0f; mat[15] = 1.0f
}

fun MatrixRotationX(mat: FloatArray, theta: Float) {
    val the = theta * 3.14f / 180.0f
    mat[0] = 1.0f; mat[1] = 0.0f; mat[2] = 0.0f; mat[3] = 0.0f
    mat[4] = 0.0f; mat[5] = cos(the); mat[6] = sin(the); mat[7] = 0.0f
    mat[8] = 0.0f; mat[9] = -sin(the); mat[10] = cos(the); mat[11] = 0.0f
    mat[12] = 0.0f; mat[13] = 0.0f; mat[14] = 0.0f; mat[15] = 1.0f
}

fun MatrixRotationY(mat: FloatArray, theta: Float) {
    val the = theta * 3.14f / 180.0f
    mat[0] = cos(the); mat[1] = 0.0f; mat[2] = -sin(the); mat[3] = 0.0f
    mat[4] = 0.0f; mat[5] = 1.0f; mat[6] = 0.0f; mat[7] = 0.0f
    mat[8] = sin(the); mat[9] = 0.0f; mat[10] = cos(the); mat[11] = 0.0f
    mat[12] = 0.0f; mat[13] = 0.0f; mat[14] = 0.0f; mat[15] = 1.0f
}

fun MatrixRotationZ(mat: FloatArray, theta: Float) {
    val the = theta * 3.14f / 180.0f
    mat[0] = cos(the); mat[1] = sin(the); mat[2] = 0.0f; mat[3] = 0.0f
    mat[4] = -sin(the); mat[5] = cos(the); mat[6] = 0.0f; mat[7] = 0.0f
    mat[8] = 0.0f; mat[9] = 0.0f; mat[10] = 1.0f; mat[11] = 0.0f
    mat[12] = 0.0f; mat[13] = 0.0f; mat[14] = 0.0f; mat[15] = 1.0f
}

fun MatrixTranslation(mat: FloatArray, movx: Float, movy: Float, movz: Float) {
    mat[0] = 1.0f; mat[1] = 0.0f; mat[2] = 0.0f; mat[3] = 0.0f
    mat[4] = 0.0f; mat[5] = 1.0f; mat[6] = 0.0f; mat[7] = 0.0f
    mat[8] = 0.0f; mat[9] = 0.0f; mat[10] = 1.0f; mat[11] = 0.0f
    mat[12] = movx; mat[13] = movy; mat[14] = movz; mat[15] = 1.0f
}

fun MatrixMultiply(outmat: FloatArray, mat1: FloatArray, mat2: FloatArray) {
    outmat[0] = mat1[0] * mat2[0] + mat1[1] * mat2[4] + mat1[2] * mat2[8] + mat1[3] * mat2[12]
    outmat[1] = mat1[0] * mat2[1] + mat1[1] * mat2[5] + mat1[2] * mat2[9] + mat1[3] * mat2[13]
    outmat[2] = mat1[0] * mat2[2] + mat1[1] * mat2[6] + mat1[2] * mat2[10] + mat1[3] * mat2[14]
    outmat[3] = mat1[0] * mat2[3] + mat1[1] * mat2[7] + mat1[2] * mat2[11] + mat1[3] * mat2[15]

    outmat[4] = mat1[4] * mat2[0] + mat1[5] * mat2[4] + mat1[6] * mat2[8] + mat1[7] * mat2[12]
    outmat[5] = mat1[4] * mat2[1] + mat1[5] * mat2[5] + mat1[6] * mat2[9] + mat1[7] * mat2[13]
    outmat[6] = mat1[4] * mat2[2] + mat1[5] * mat2[6] + mat1[6] * mat2[10] + mat1[7] * mat2[14]
    outmat[7] = mat1[4] * mat2[3] + mat1[5] * mat2[7] + mat1[6] * mat2[11] + mat1[7] * mat2[15]

    outmat[8] = mat1[8] * mat2[0] + mat1[9] * mat2[4] + mat1[10] * mat2[8] + mat1[11] * mat2[12]
    outmat[9] = mat1[8] * mat2[1] + mat1[9] * mat2[5] + mat1[10] * mat2[9] + mat1[11] * mat2[13]
    outmat[10] = mat1[8] * mat2[2] + mat1[9] * mat2[6] + mat1[10] * mat2[10] + mat1[11] * mat2[14]
    outmat[11] = mat1[8] * mat2[3] + mat1[9] * mat2[7] + mat1[10] * mat2[11] + mat1[11] * mat2[15]

    outmat[12] = mat1[12] * mat2[0] + mat1[13] * mat2[4] + mat1[14] * mat2[8] + mat1[15] * mat2[12]
    outmat[13] = mat1[12] * mat2[1] + mat1[13] * mat2[5] + mat1[14] * mat2[9] + mat1[15] * mat2[13]
    outmat[14] = mat1[12] * mat2[2] + mat1[13] * mat2[6] + mat1[14] * mat2[10] + mat1[15] * mat2[14]
    outmat[15] = mat1[12] * mat2[3] + mat1[13] * mat2[7] + mat1[14] * mat2[11] + mat1[15] * mat2[15]
}

fun CalDetMat4x4(mat: FloatArray): Float {
    return (mat[0] * mat[5] * mat[10] * mat[15] + mat[0] * mat[6] * mat[11] * mat[13] + mat[0] * mat[7] * mat[9] * mat[14]
            + mat[1] * mat[4] * mat[11] * mat[14] + mat[1] * mat[6] * mat[8] * mat[15] + mat[1] * mat[7] * mat[10] * mat[12]
            + mat[2] * mat[4] * mat[9] * mat[15] + mat[2] * mat[5] * mat[11] * mat[12] + mat[2] * mat[7] * mat[8] * mat[13]
            + mat[3] * mat[4] * mat[10] * mat[13] + mat[3] * mat[5] * mat[8] * mat[14] + mat[3] * mat[6] * mat[9] * mat[12]
            - mat[0] * mat[5] * mat[11] * mat[14] - mat[0] * mat[6] * mat[9] * mat[15] - mat[0] * mat[7] * mat[10] * mat[13]
            - mat[1] * mat[4] * mat[10] * mat[15] - mat[1] * mat[6] * mat[11] * mat[12] - mat[1] * mat[7] * mat[8] * mat[14]
            - mat[2] * mat[4] * mat[11] * mat[13] - mat[2] * mat[5] * mat[8] * mat[15] - mat[2] * mat[7] * mat[9] * mat[12]
            - mat[3] * mat[4] * mat[9] * mat[14] - mat[3] * mat[5] * mat[10] * mat[12] - mat[3] * mat[6] * mat[8] * mat[13])
}

fun MatrixInverse(outmat: FloatArray, mat: FloatArray) {
    val det = CalDetMat4x4(mat)
    val inv_det = (1.0f / det)

    outmat[0] = inv_det *
            (mat[5] * mat[10] * mat[15] + mat[6] * mat[11] * mat[13] + mat[7] * mat[9] * mat[14] - mat[5] * mat[11] * mat[14] - mat[6] * mat[9] * mat[15] - mat[7] * mat[10] * mat[13])
    outmat[1] = inv_det *
            (mat[1] * mat[11] * mat[14] + mat[2] * mat[9] * mat[15] + mat[3] * mat[10] * mat[13] - mat[1] * mat[10] * mat[15] - mat[2] * mat[11] * mat[13] - mat[3] * mat[9] * mat[14])
    outmat[2] = inv_det *
            (mat[1] * mat[6] * mat[15] + mat[2] * mat[7] * mat[13] + mat[3] * mat[5] * mat[14] - mat[1] * mat[7] * mat[14] - mat[2] * mat[5] * mat[15] - mat[3] * mat[6] * mat[13])
    outmat[3] = inv_det *
            (mat[1] * mat[7] * mat[10] + mat[2] * mat[5] * mat[11] + mat[3] * mat[6] * mat[9] - mat[1] * mat[6] * mat[11] - mat[2] * mat[7] * mat[9] - mat[3] * mat[5] * mat[10])

    outmat[4] = inv_det *
            (mat[4] * mat[11] * mat[14] + mat[6] * mat[8] * mat[15] + mat[7] * mat[10] * mat[12] - mat[4] * mat[10] * mat[15] - mat[6] * mat[11] * mat[12] - mat[7] * mat[8] * mat[14])
    outmat[5] = inv_det *
            (mat[0] * mat[10] * mat[15] + mat[2] * mat[11] * mat[12] + mat[3] * mat[8] * mat[14] - mat[0] * mat[11] * mat[14] - mat[2] * mat[8] * mat[15] - mat[3] * mat[10] * mat[12])
    outmat[6] = inv_det *
            (mat[0] * mat[7] * mat[14] + mat[2] * mat[4] * mat[15] + mat[3] * mat[6] * mat[12] - mat[0] * mat[6] * mat[15] - mat[2] * mat[7] * mat[12] - mat[3] * mat[4] * mat[14])
    outmat[7] = inv_det *
            (mat[0] * mat[6] * mat[11] + mat[2] * mat[7] * mat[8] + mat[3] * mat[4] * mat[10] - mat[0] * mat[7] * mat[10] - mat[2] * mat[4] * mat[11] - mat[3] * mat[6] * mat[8])

    outmat[8] = inv_det *
            (mat[4] * mat[9] * mat[15] + mat[5] * mat[11] * mat[12] + mat[7] * mat[8] * mat[13] - mat[4] * mat[11] * mat[13] - mat[5] * mat[8] * mat[15] - mat[7] * mat[9] * mat[12])
    outmat[9] = inv_det *
            (mat[0] * mat[11] * mat[13] + mat[1] * mat[8] * mat[15] + mat[3] * mat[9] * mat[12] - mat[0] * mat[9] * mat[15] - mat[1] * mat[11] * mat[12] - mat[3] * mat[8] * mat[13])
    outmat[10] = inv_det *
            (mat[0] * mat[5] * mat[15] + mat[1] * mat[7] * mat[12] + mat[3] * mat[4] * mat[13] - mat[0] * mat[7] * mat[13] - mat[1] * mat[4] * mat[15] - mat[3] * mat[5] * mat[12])
    outmat[11] = inv_det *
            (mat[0] * mat[7] * mat[9] + mat[1] * mat[4] * mat[11] + mat[3] * mat[5] * mat[8] - mat[0] * mat[5] * mat[11] - mat[1] * mat[7] * mat[8] - mat[3] * mat[4] * mat[9])

    outmat[12] = inv_det *
            (mat[4] * mat[10] * mat[13] + mat[5] * mat[8] * mat[14] + mat[6] * mat[9] * mat[12] - mat[4] * mat[9] * mat[14] - mat[5] * mat[10] * mat[12] - mat[6] * mat[8] * mat[13])
    outmat[13] = inv_det *
            (mat[0] * mat[9] * mat[14] + mat[1] * mat[10] * mat[12] + mat[2] * mat[8] * mat[13] - mat[0] * mat[10] * mat[13] - mat[1] * mat[8] * mat[14] - mat[2] * mat[9] * mat[12])
    outmat[14] = inv_det *
            (mat[0] * mat[6] * mat[13] + mat[1] * mat[4] * mat[14] + mat[2] * mat[5] * mat[12] - mat[0] * mat[5] * mat[14] - mat[1] * mat[6] * mat[12] - mat[2] * mat[4] * mat[13])
    outmat[15] = inv_det *
            (mat[0] * mat[5] * mat[10] + mat[1] * mat[6] * mat[8] + mat[2] * mat[4] * mat[9] - mat[0] * mat[6] * mat[9] - mat[1] * mat[4] * mat[10] - mat[2] * mat[5] * mat[8])
}

fun MatrixIdentity(m: FloatArray) {
    m[0] = 1.0f
    m[1] = 0.0f
    m[2] = 0.0f
    m[3] = 0.0f

    m[4] = 0.0f
    m[5] = 1.0f
    m[6] = 0.0f
    m[7] = 0.0f

    m[8] = 0.0f
    m[9] = 0.0f
    m[10] = 1.0f
    m[11] = 0.0f

    m[12] = 0.0f
    m[13] = 0.0f
    m[14] = 0.0f
    m[15] = 1.0f
}

private fun Normalize(x: Float, y: Float, z: Float, w: Float): FloatArray {
    val nor = Math.sqrt((x * x + y * y + z * z + w * w).toDouble()).toFloat()
    val out = FloatArray(4)
    if (nor != 0.0f) {
        out[0] = x / nor
        out[1] = y / nor
        out[2] = z / nor
        out[3] = w / nor
    } else {
        out[0] = 0.0f
        out[1] = 0.0f
        out[2] = 0.0f
        out[3] = 0.0f
    }
    return out
}

fun MatrixNormalize(m:FloatArray) {
    var mat = Normalize(m[0], m[4], m[8], 0.0f)
    m[0] = mat[0]
    m[4] = mat[1]
    m[8] = mat[2]
    mat = Normalize(m[1], m[5], m[9], 0.0f)
    m[1] = mat[0]
    m[5] = mat[1]
    m[9] = mat[2]
    mat = Normalize(m[2], m[6], m[10], 0.0f)
    m[2] = mat[0]
    m[6] = mat[1]
    m[10] = mat[2]
}

fun MatrixLookAtLH(look:FloatArray, x1: Float, y1: Float, z1: Float,
                   x2: Float, y2: Float, z2: Float, x3: Float, y3: Float, z3: Float
) {
    //z軸
    val zx = x2 - x1
    val zy = y2 - y1
    val zz = z2 - z1
    MatrixLookAt(look, x1, y1, z1, x3, y3, z3, zx, zy, zz)
}

fun MatrixLookAtRH(look:FloatArray, x1: Float, y1: Float, z1: Float,
                   x2: Float, y2: Float, z2: Float, x3: Float, y3: Float, z3: Float
) {
    //z軸
    val zx = x1 - x2
    val zy = y1 - y2
    val zz = z1 - z2
    MatrixLookAt(look, x1, y1, z1, x3, y3, z3, zx, zy, zz)
}

private fun MatrixLookAt(look:FloatArray, x1: Float, y1: Float, z1: Float,
                         x3: Float, y3: Float, z3: Float, zx: Float, zy: Float, zz: Float) {
    var zx = zx
    var zy = zy
    var zz = zz
    //正規化
    val nor = Normalize(zx, zy, zz, 0.0f)
    zx = nor[0]
    zy = nor[1]
    zz = nor[2]

    //x軸(外積)
    var xx = y3 * zz - z3 * zy
    var xy = z3 * zx - x3 * zz
    var xz = x3 * zy - y3 * zx
    //正規化
    val nor1 = Normalize(xx, xy, xz, 0.0f)
    xx = nor1[0]
    xy = nor1[1]
    xz = nor1[2]

    //y軸(外積)
    val yx = zy * xz - zz * xy
    val yy = zz * xx - zx * xz
    val yz = zx * xy - zy * xx

    //平行移動(内積)
    val mx = -(x1 * xx + y1 * xy + z1 * xz)
    val my = -(x1 * yx + y1 * yy + z1 * yz)
    val mz = -(x1 * zx + y1 * zy + z1 * zz)

    look[0] = xx
    look[1] = yx
    look[2] = zx
    look[3] = 0.0f

    look[4] = xy
    look[5] = yy
    look[6] = zy
    look[7] = 0.0f

    look[8] = xz
    look[9] = yz
    look[10] = zz
    look[11] = 0.0f

    look[12] = mx
    look[13] = my
    look[14] = mz
    look[15] = 1.0f
}

fun MatrixPerspectiveFovLH(per:FloatArray,theta: Float, aspect: Float, Near: Float, Far: Float) {
    MatrixPerspectiveFov(per, theta, aspect, Near, Far, 1.0f, Far - Near)
}

fun MatrixPerspectiveFovRH(per:FloatArray,theta: Float, aspect: Float, Near: Float, Far: Float) {
    MatrixPerspectiveFov(per, theta, aspect, Near, Far, -1.0f, Near - Far)
}

private fun MatrixPerspectiveFov(per:FloatArray,theta: Float, aspect: Float, Near: Float, Far: Float, m11: Float, fn: Float) {
    val the = theta * Math.PI.toFloat() / 180.0f
    //透視変換後y方向スケーリング
    val sy = 1.0f / Math.tan((the / 2.0f).toDouble()).toFloat()
    //x方向スケーリング
    val sx = sy / aspect
    //z方向スケーリング
    val sz = Far / fn

    per[0] = sx
    per[1] = 0.0f
    per[2] = 0.0f
    per[3] = 0.0f

    per[4] = 0.0f
    per[5] = sy
    per[6] = 0.0f
    per[7] = 0.0f

    per[8] = 0.0f
    per[9] = 0.0f
    per[10] = sz
    per[11] = m11

    per[12] = 0.0f
    per[13] = 0.0f
    per[14] = -(sz * Near)
    per[15] = 0.0f
}

fun MatrixViewPort(view:FloatArray,width: Int, height: Int) {
    view[0] = (width / 2).toFloat()
    view[1] = 0.0f
    view[2] = 0.0f
    view[3] = 0.0f

    view[4] = 0.0f
    view[5] = (-height / 2).toFloat()
    view[6] = 0.0f
    view[7] = 0.0f

    view[8] = 0.0f
    view[9] = 0.0f
    view[10] = 1.0f
    view[11] = 0.0f

    view[12] = (width / 2).toFloat()
    view[13] = (height / 2).toFloat()
    view[14] = 0.0f
    view[15] = 1.0f
}

fun MatrixTranspose(mat:FloatArray) {
    var temp: Float
    temp = mat[1]
    mat[1] = mat[4]
    mat[4] = temp
    temp = mat[2]
    mat[2] = mat[8]
    mat[8] = temp
    temp = mat[3]
    mat[3] = mat[12]
    mat[12] = temp
    temp = mat[6]
    mat[6] = mat[9]
    mat[9] = temp
    temp = mat[7]
    mat[7] = mat[13]
    mat[13] = temp
    temp = mat[11]
    mat[11] = mat[14]
    mat[14] = temp
}

fun getNameFromPass(pass:CharArray?):CharArray {
    var stInd = 0
    var endInd = 0

    for (i in pass!!.size - 1 downTo 0) {
        if (pass[i] == '.') {
            endInd = i - 1
        }
        if (pass[i] == '\\' || pass[i] == '/') {
            stInd = i + 1
            break
        }
    }

    val ret = pass.copyOfRange(stInd, endInd + 1)
    return ret
}

fun nameComparison(name1: CharArray?, name2: CharArray?): Boolean {
    var stInd1 = 0
    var stInd2 = 0
    for (i in name1!!.size - 1 downTo 0) {
        if (name1[i] == ' ') {
            stInd1 = i + 1
            break
        }
    }
    for (i in name2!!.size - 1 downTo 0) {
        if (name2[i] == ' ') {
            stInd2 = i + 1
            break
        }
    }
    val n1 = name1.copyOfRange(stInd1, name1.size)
    val n2 = name2.copyOfRange(stInd2, name2.size)
    if (n1.size != n2.size) return false
    for (i in 0 until n1.size) {
        if (n1[i] != n2[i]) return false
    }
    return true
}

fun nameComparison(name1: Array<Char?>, name2: String): Boolean {
    val ch1 = CharArray(name1.size)
    val ch2 = CharArray(name2.length)
    for (i in 0..name1.size - 1) ch1[i] = name1[i]!!.toChar()
    for (i in 0..name2.length - 1) ch2[i] = name2[i].toChar()
    return nameComparison(ch1, ch2)
}

fun nameComparison(name1: CharArray?, name2: String): Boolean {
    val ch1 = name1!!.copyOf()
    val ch2 = CharArray(name2.length)
    for (i in 0..name2.length - 1) ch2[i] = name2[i].toChar()
    return nameComparison(ch1, ch2)
}
