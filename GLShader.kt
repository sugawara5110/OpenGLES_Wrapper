package jp.sugasato.opengles_wrapperkt

import android.opengl.GLES30

object GLShader {

    private val mProgramHandle = IntArray(2)
    private val mMVPMatrixHandle = IntArray(2)//u_MVPMatrixのハンドル
    private val mWorldMatrixHandle = IntArray(2)//u_WorldMatrixのハンドル
    private val mDiffuseHandle = IntArray(2)//u_Diffuseのハンドル
    private val mAmbientHandle = IntArray(2)//u_Ambientのハンドル
    private val mDirLightHandle = IntArray(2)//u_DirLightのハンドル
    private val mPositionHandle = IntArray(2)//a_Positionのハンドル
    private val mNormalHandle = IntArray(2)//a_Normalのハンドル
    private val mUvHandle = IntArray(2)
    private var transformHandle: Int = 0
    private var uvwhHandle: Int = 0
    private var ind = 0

    fun programHandle(ind: Int): Int {
        return mProgramHandle[ind]
    }

    fun mvpMatrixHandle(ind: Int): Int {
        return mMVPMatrixHandle[ind]
    }

    fun worldMatrixHandle(ind: Int): Int {
        return mWorldMatrixHandle[ind]
    }

    fun DiffuseHandle(ind: Int): Int {
        return mDiffuseHandle[ind]
    }

    fun AmbientHandle(ind: Int): Int {
        return mAmbientHandle[ind]
    }

    fun DirLightHandle(ind: Int): Int {
        return mDirLightHandle[ind]
    }

    fun positionHandle(ind: Int): Int {
        return mPositionHandle[ind]
    }

    fun normalHandle(ind: Int): Int {
        return mNormalHandle[ind]
    }

    fun uvHandle(ind: Int): Int {
        return mUvHandle[ind]
    }

    fun createShader() {
        create3DShader()
        create2DShader()
    }

    private fun create3DShader() {
        ind = 0
        createShader(ShaderSource.vertexShader3D, ShaderSource.fragmentShader3D)
        mMVPMatrixHandle[0] = GLES30.glGetUniformLocation(mProgramHandle[0], "u_MVPMatrix")
        mWorldMatrixHandle[0] = GLES30.glGetUniformLocation(mProgramHandle[0], "u_WorldMatrix")
        mDiffuseHandle[0] = GLES30.glGetUniformLocation(mProgramHandle[0], "u_Diffuse")
        mAmbientHandle[0] = GLES30.glGetUniformLocation(mProgramHandle[0], "u_Ambient")
        mDirLightHandle[0] = GLES30.glGetUniformLocation(mProgramHandle[0], "u_DirLight")
    }

    private fun create2DShader() {
        ind = 1
        createShader(ShaderSource.vertexShader2D, ShaderSource.fragmentShader2D)
        transformHandle = GLES30.glGetUniformLocation(mProgramHandle[1], "u_Transform")
        uvwhHandle = GLES30.glGetUniformLocation(mProgramHandle[1], "u_UvWH")
    }

    fun startProgram(Ind: Int) {
        //シェーダプログラム適用
        ind = Ind
        GLES30.glUseProgram(mProgramHandle[ind])
    }

    fun stopProgram() {
        //シェーダー停止
        GLES30.glUseProgram(0)
    }

    fun releaseProgram() {
        //プログラム解放
        GLES30.glDeleteProgram(mProgramHandle[0])
        GLES30.glDeleteProgram(mProgramHandle[1])
    }

    private fun createShader(vs: String, fs: String) {
        //バーテックスシェーダをコンパイル
        val vertexShaderHandle = compileShader(GLES30.GL_VERTEX_SHADER, vs)
        //フラグメントシェーダをコンパイル
        val fragmentShaderHandle = compileShader(GLES30.GL_FRAGMENT_SHADER, fs)
        //シェーダプログラムをリンク
        mProgramHandle[ind] = AttachShader(vertexShaderHandle, fragmentShaderHandle)

        //シェーダーリンク完了したらシェーダーオブジェクト解放
        GLES30.glDeleteShader(vertexShaderHandle)
        GLES30.glDeleteShader(fragmentShaderHandle)

        // ハンドル(ポインタ)の取得
        mPositionHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind], "a_Position")
        mNormalHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind], "a_Normal")
        mUvHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind], "a_Uv")
    }

    private fun AttachShader(vsHandle: Int, fsHandle: Int): Int {
        //シェーダプログラムをリンク
        var progHandle = GLES30.glCreateProgram()
        if (progHandle != 0) {
            GLES30.glAttachShader(progHandle, vsHandle) // バーテックスシェーダをアタッチ
            GLES30.glAttachShader(progHandle, fsHandle) // フラグメントシェーダをアタッチ
            GLES30.glBindAttribLocation(progHandle, 0, "a_Position") // attributeのindexを設定
            GLES30.glBindAttribLocation(progHandle, 1, "a_Normal") // attributeのindexを設定
            GLES30.glBindAttribLocation(progHandle, 2, "a_Uv") // attributeのindexを設定
            GLES30.glLinkProgram(progHandle) // バーテックスシェーダとフラグメントシェーダをプログラムへリンク

            // リンク結果のチェック
            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(progHandle, GLES30.GL_LINK_STATUS, linkStatus, 0)

            if (linkStatus[0] == 0) {
                // リンク失敗
                GLES30.glDeleteProgram(progHandle)
                progHandle = 0
            }
        }
        if (progHandle == 0) {
            throw RuntimeException("Error creating program.")
        }
        return progHandle
    }

    private fun compileShader(shaderType: Int, src: String): Int {
        var shaderHandle = GLES30.glCreateShader(shaderType)
        if (shaderHandle != 0) {
            GLES30.glShaderSource(shaderHandle, src) // シェーダソースを送信し
            GLES30.glCompileShader(shaderHandle) // コンパイル

            // コンパイル結果のチェック
            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0)

            if (compileStatus[0] == 0) {
                // コンパイル失敗
                GLES30.glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }
        if (shaderHandle == 0) {
            throw RuntimeException("Error creating vertex shader.")
        }
        return shaderHandle
    }
}