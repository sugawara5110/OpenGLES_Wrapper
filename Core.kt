package jp.sugasato.opengles_wrapperkt

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import android.opengl.GLES30

class DrawParameter {
    var textureId: Int = 0
    var MVPMatrixHandle: Int = 0
    var WorldMatrixHandle: Int = 0
    var DiffuseHandle: Int = 0
    var AmbientHandle: Int = 0
    var DirLightHandle: Int = 0
    var Diffuse = FloatArray(4, { 1.0f })
    var Ambient = FloatArray(4, { 0.3f })
}

object Core {

    private const val floatSize = 4
    private const val intSize = 4
    private const val numVertex = 3
    private const val numNormal = 3
    private const val numUV = 2
    private const val numVerNor = 6
    private const val numVerNorUV = 8
    private const val VerSize = numVertex * floatSize
    private const val VerNorSize = numVerNor * floatSize
    private const val VerNorUVSize = numVerNorUV * floatSize

    private var width = 0
    private var height = 0
    private var DirLight = floatArrayOf(0.0f, -1.0f, -0.2f)
    private val fovy = 45.0f//画角
    private val zNear = 1.0f
    private val zFar = 170.0f
    private val eyeX = 0.0f
    private val eyeY = 0.0f
    private val eyeZ = 100.0f//視点
    private val centerX = 0.0f
    private val centerY = 0.0f
    private val centerZ = 0.0f//注視点
    private val upX = 0.0f
    private val upY = 1.0f
    private var upZ = 0.0f//上方向
    private val Look = FloatArray(16)//視点
    private val Per = FloatArray(16)
    private val worLookMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    fun clear() {
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT) //バッファのクリア
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    fun surfaceChanged(wid: Int, hei: Int) {
        width = wid
        height = hei
        //DepthTest有効
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glDepthFunc(GLES30.GL_LEQUAL)
        //スクリーンが変わり画角を変更する場合、射影行列を作り直す
        GLES30.glViewport(0, 0, width, height)
        //アルファブレンド有効
        GLES30.glEnable(GLES30.GL_BLEND)
        //合成アルゴリズム, これから描画する画像係数, すでに描画した画像係数
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
        //カリングの有効化
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        // 裏面を描画しない
        GLES30.glFrontFace(GLES30.GL_CCW)
        GLES30.glCullFace(GLES30.GL_BACK)
        MatrixLookAtLH(Look, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
        MatrixPerspectiveFovLH(Per, fovy, width.toFloat() / height.toFloat(), zNear, zFar)
    }

    fun setPerspective(fov: Float, znear: Float, zfar: Float) {
        MatrixPerspectiveFovLH(Per, fov, width.toFloat() / height.toFloat(), znear, zfar)
    }

    fun setCamera(
        ex: Float,
        ey: Float,
        ez: Float,
        cx: Float,
        cy: Float,
        cz: Float,
        upx: Float = 0.0f,
        upy: Float = 1.0f,
        upz: Float = 0.0f
    ) {
        MatrixLookAtLH(Look, ex, ey, ez, cx, cy, cz, upx, upy, upz)
    }

    fun setDirLight(x: Float, y: Float, z: Float) {
        DirLight[0] = x
        DirLight[1] = y
        DirLight[2] = z
    }

    fun bindBufferObj(poshandle: Int, norhandle: Int, uvhandle: Int, allVertices: FloatArray?, index: IntArray?): Int {
        val vboId = IntArray(2)
        val vaoId = IntArray(1)
        GLES30.glGenBuffers(2, vboId, 0)
        GLES30.glGenVertexArrays(1, vaoId, 0)

        val ver = makeFloatBuffer(allVertices)
        val indexBf = makeIntBuffer(index)
        //VBO, IBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId[0])
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, allVertices!!.size * floatSize, ver, GLES30.GL_STATIC_DRAW)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vboId[1])
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, index!!.size * intSize, indexBf, GLES30.GL_STATIC_DRAW)
        //VAO
        GLES30.glBindVertexArray(vaoId[0])
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId[0])
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vboId[1])
        GLES30.glEnableVertexAttribArray(poshandle)
        GLES30.glEnableVertexAttribArray(norhandle)
        GLES30.glEnableVertexAttribArray(uvhandle)
        GLES30.glVertexAttribPointer(poshandle, numVertex, GLES30.GL_FLOAT, false, VerNorUVSize, 0)
        GLES30.glVertexAttribPointer(norhandle, numNormal, GLES30.GL_FLOAT, false, VerNorUVSize, VerSize)
        GLES30.glVertexAttribPointer(uvhandle, numUV, GLES30.GL_FLOAT, false, VerNorUVSize, VerNorSize)
        GLES30.glBindVertexArray(0)
        return vaoId[0]
    }

    private fun updateMatrix(world: FloatArray) {
        MatrixMultiply(worLookMatrix, world, Look)
        MatrixMultiply(mMVPMatrix, worLookMatrix, Per)
    }

    fun draw2D(
        vaoID: Int, textureId: Int, TransformHandle: Int, trans: FloatArray, UvwhHandle: Int, uvwh: FloatArray
    ) {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glBindVertexArray(vaoID)
        GLES30.glUniform2f(TransformHandle, trans[0], trans[1])
        GLES30.glUniform4f(UvwhHandle, uvwh[0], uvwh[1], uvwh[2], uvwh[3])
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, (4 * 1.5).toInt(), GLES30.GL_UNSIGNED_INT, 0)//後で直す
        GLES30.glBindVertexArray(0)
        GLES30.glFinish()
    }

    fun draw(vaoId: Int, numIndex: Int, world: FloatArray, dp: DrawParameter) {
        updateMatrix(world)
        //テクスチャ有効化
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        //テクスチャオブジェクトの指定
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dp.textureId)
        GLES30.glBindVertexArray(vaoId)
        GLES30.glUniformMatrix4fv(dp.MVPMatrixHandle, 1, false, mMVPMatrix, 0)
        GLES30.glUniformMatrix4fv(dp.WorldMatrixHandle, 1, false, world, 0)
        GLES30.glUniform4f(dp.DiffuseHandle, dp.Diffuse[0], dp.Diffuse[1], dp.Diffuse[2], dp.Diffuse[3])
        GLES30.glUniform4f(dp.AmbientHandle, dp.Ambient[0], dp.Ambient[1], dp.Ambient[2], dp.Ambient[3])
        GLES30.glUniform3f(dp.DirLightHandle, DirLight[0], DirLight[1], DirLight[2])
        //インデックス有り描画
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, numIndex, GLES30.GL_UNSIGNED_INT, 0)
        GLES30.glBindVertexArray(0)
        GLES30.glFinish()
    }

    private fun makeIntBuffer(arr: IntArray?): IntBuffer {
        //システムメモリ領域確保
        val bb = ByteBuffer.allocateDirect(arr!!.size * intSize)
        bb.order(ByteOrder.nativeOrder())
        val ib = bb.asIntBuffer()
        ib.put(arr)//配列転送
        ib.position(0)
        return ib
    }

    private fun makeFloatBuffer(arr: FloatArray?): FloatBuffer {
        //システムメモリ領域確保
        val bb = ByteBuffer.allocateDirect(arr!!.size * floatSize)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(arr)//配列転送
        fb.position(0)
        return fb
    }

    private fun makeByteBuffer(array: ByteArray): ByteBuffer {
        val bb = ByteBuffer.allocateDirect(array.size).order(ByteOrder.nativeOrder())
        bb.put(array).position(0)
        return bb
    }
}