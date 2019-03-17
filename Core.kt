package jp.sugasato.opengles_wrapperkt

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import android.opengl.GLES30

class DrawParameter {
    var diffTexId: Int = -1
    var norTexId: Int = -1
    var MVPMatrixHandle: Int = -1
    var WorldMatrixHandle: Int = -1
    var Texture1Handle: Int = -1
    var BoneMatrixHandle: Int = -1
    var DiffuseHandle: Int = -1
    var AmbientHandle: Int = -1
    var DirLightHandle: Int = -1
    var Diffuse = FloatArray(4, { 1.0f })
    var Ambient = FloatArray(4, { 0.3f })
}

object Core {

    const val floatSize = 4
    const val intSize = 4
    const val numVertex = 3
    const val numNormal = 3
    const val numUV = 2
    const val numVerNor = 6
    const val numVerNorUV = 8
    const val VerSize = numVertex * floatSize
    const val VerNorSize = numVerNor * floatSize
    const val VerNorUVSize = numVerNorUV * floatSize
    const val numBoneWei = 4
    const val boneIndSize = numBoneWei * intSize
    const val boneWeiSize = numBoneWei * floatSize
    const val VerNorUVBoneIndSize = VerNorUVSize + boneIndSize
    const val numVerNorUVBoneIndWei = numVerNorUV + numBoneWei * 2
    const val VerNorUVBoneIndWeiSize = VerNorUVSize + boneWeiSize + boneIndSize

    private var width = 0
    private var height = 0
    private var DirLight = floatArrayOf(0.0f, -1.0f, -0.2f)
    private var fovy = 45.0f//画角
    private var zNear = 1.0f
    private var zFar = 170.0f
    private var eyeX = 0.0f
    private var eyeY = 0.0f
    private var eyeZ = 100.0f//視点
    private var centerX = 0.0f
    private var centerY = 0.0f
    private var centerZ = 0.0f//注視点
    private var upX = 0.0f
    private var upY = 1.0f
    private var upZ = 0.0f//上方向
    private val Look = FloatArray(16)//視点
    private val Per = FloatArray(16)
    private val worLookMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    fun clear(r: Float = 0.0f, g: Float = 0.0f, b: Float = 0.0f) {
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT) //バッファのクリア
        GLES30.glClearColor(r, g, b, 1.0f)
    }

    fun surfaceChanged(wid: Int, hei: Int) {
        width = wid
        height = hei
        //スクリーンが変わり画角を変更する場合、射影行列を作り直す
        GLES30.glViewport(0, 0, width, height)
        //カリングの有効化
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        // 裏面を描画しない
        GLES30.glFrontFace(GLES30.GL_CCW)
        GLES30.glCullFace(GLES30.GL_BACK)
        MatrixLookAtLH(Look, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
        MatrixPerspectiveFovLH(Per, fovy, width.toFloat() / height.toFloat(), zNear, zFar)
    }

    fun setPerspective(fov: Float, znear: Float, zfar: Float) {
        fovy = fov
        zNear = znear
        zFar = zfar
        MatrixPerspectiveFovLH(Per, fovy, width.toFloat() / height.toFloat(), zNear, zFar)
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
        eyeX = ex
        eyeY = ey
        eyeZ = ez
        centerX = cx
        centerY = cy
        centerZ = cz
        upX = upx
        upY = upy
        upZ = upz
        MatrixLookAtLH(Look, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }

    fun setDirLight(x: Float, y: Float, z: Float) {
        DirLight[0] = x
        DirLight[1] = y
        DirLight[2] = z
    }

    fun bindBufferObj(
        poshandle: Int,
        norhandle: Int,
        uvhandle: Int,
        allVertices: FloatArray?,
        index: IntArray?,
        boneIndhandle: Int = -1,
        boneWeihandle: Int = -1
    ): Int {
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
        if (boneIndhandle != -1) {
            GLES30.glEnableVertexAttribArray(boneIndhandle)
            GLES30.glEnableVertexAttribArray(boneWeihandle)
        }
        if (boneIndhandle == -1) {
            GLES30.glVertexAttribPointer(poshandle, numVertex, GLES30.GL_FLOAT, false, VerNorUVSize, 0)
            GLES30.glVertexAttribPointer(norhandle, numNormal, GLES30.GL_FLOAT, false, VerNorUVSize, VerSize)
            GLES30.glVertexAttribPointer(uvhandle, numUV, GLES30.GL_FLOAT, false, VerNorUVSize, VerNorSize)
        } else {
            GLES30.glVertexAttribPointer(poshandle, numVertex, GLES30.GL_FLOAT, false, VerNorUVBoneIndWeiSize, 0)
            GLES30.glVertexAttribPointer(norhandle, numNormal, GLES30.GL_FLOAT, false, VerNorUVBoneIndWeiSize, VerSize)
            GLES30.glVertexAttribPointer(uvhandle, numUV, GLES30.GL_FLOAT, false, VerNorUVBoneIndWeiSize, VerNorSize)
            GLES30.glVertexAttribPointer(
                boneIndhandle,
                numBoneWei,
                GLES30.GL_FLOAT,
                false,
                VerNorUVBoneIndWeiSize,
                VerNorUVSize
            )
            GLES30.glVertexAttribPointer(
                boneWeihandle,
                numBoneWei,
                GLES30.GL_FLOAT,
                false,
                VerNorUVBoneIndWeiSize,
                VerNorUVBoneIndSize
            )
        }
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

    fun ALPHAlBlendOn() {
        //アルファブレンド有効
        GLES30.glEnable(GLES30.GL_BLEND)
        //合成アルゴリズム, これから描画する画像係数, すでに描画した画像係数
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
    }

    fun ALPHAlBlendOff() {
        //アルファブレンド無効
        GLES30.glDisable(GLES30.GL_BLEND)
    }

    fun DepthTestOn() {
        //DepthTest有効
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glDepthFunc(GLES30.GL_LEQUAL)
    }

    fun DepthTestOff() {
        //DepthTest無効
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
    }

    fun draw(vaoId: Int, numIndex: Int, worldMatrix: FloatArray, dp: DrawParameter, boneMatrix: FloatArray? = null) {
        updateMatrix(worldMatrix)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)//テクスチャ0有効化
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dp.diffTexId)//テクスチャオブジェクトの指定
        if (dp.Texture1Handle != -1) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE1)//テクスチャ1有効化
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dp.norTexId)
        }
        GLES30.glBindVertexArray(vaoId)
        GLES30.glUniformMatrix4fv(dp.MVPMatrixHandle, 1, false, mMVPMatrix, 0)
        GLES30.glUniformMatrix4fv(dp.WorldMatrixHandle, 1, false, worldMatrix, 0)
        if (dp.BoneMatrixHandle != -1) {
            val numMat = boneMatrix!!.size / 16
            GLES30.glUniformMatrix4fv(dp.BoneMatrixHandle, numMat, false, boneMatrix, 0)
        }
        if (dp.Texture1Handle != -1) {
            GLES30.glUniform1i(dp.Texture1Handle, 1)//シェーダーの1番テクスチャ
        }
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