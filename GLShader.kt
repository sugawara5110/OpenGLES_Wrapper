package jp.sugasato.opengles_wrapperkt

import android.opengl.GLES30

object GLShader {

    const val Basic3D = "Basic3D"
    const val SkinMesh = "SkinMesh"
    const val B2D = "2D"
    private val mProgramHandle = HashMap<String?, Int>()
    private val mMVPMatrixHandle = HashMap<String?, Int>()//u_MVPMatrixのハンドル
    private val mWorldMatrixHandle = HashMap<String?, Int>()//u_WorldMatrixのハンドル
    private val mDiffuseHandle = HashMap<String?, Int>()//u_Diffuseのハンドル
    private val mAmbientHandle = HashMap<String?, Int>()//u_Ambientのハンドル
    private val mDirLightHandle = HashMap<String?, Int>()//u_DirLightのハンドル
    private val mPositionHandle = HashMap<String?, Int>()//a_Positionのハンドル
    private val mNormalHandle = HashMap<String?, Int>()//a_Normalのハンドル
    private val mUvHandle = HashMap<String?, Int>()
    //SkinMesh
    private var mBoneMatrixHandle: Int = 0//u_BoneMatrixのハンドル
    private var mBoneIndHandle: Int = 0//a_BoneIndのハンドル
    private var mBoneWeiHandle: Int = 0//a_BoneWeiのハンドル
    //2D用
    private var transformHandle: Int = 0
    private var uvwhHandle: Int = 0
    private var ind: String? = null

    fun programHandle(ind: String): Int? {
        return mProgramHandle[ind]
    }

    fun mvpMatrixHandle(ind: String): Int? {
        return mMVPMatrixHandle[ind]
    }

    fun worldMatrixHandle(ind: String): Int? {
        return mWorldMatrixHandle[ind]
    }

    fun DiffuseHandle(ind: String): Int? {
        return mDiffuseHandle[ind]
    }

    fun AmbientHandle(ind: String): Int? {
        return mAmbientHandle[ind]
    }

    fun DirLightHandle(ind: String): Int? {
        return mDirLightHandle[ind]
    }

    fun positionHandle(ind: String): Int? {
        return mPositionHandle[ind]
    }

    fun normalHandle(ind: String): Int? {
        return mNormalHandle[ind]
    }

    fun uvHandle(ind: String): Int? {
        return mUvHandle[ind]
    }

    fun boneMatrixHandle(): Int {
        return mBoneMatrixHandle
    }

    fun boneIndHandle(): Int {
        return mBoneIndHandle
    }

    fun boneWeiHandle(): Int {
        return mBoneWeiHandle
    }

    fun createShader() {
        ind = Basic3D
        create3DShader(ShaderSource.vertexShader3D)
        ind = SkinMesh
        create3DShader(ShaderSource.vertexShaderSkinMesh)
        ind = B2D
        create2DShader()
    }

    private fun create3DShader(vs: String) {
        createShader(vs, ShaderSource.fragmentShader3D)
        mMVPMatrixHandle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_MVPMatrix")
        mWorldMatrixHandle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_WorldMatrix")
        if (ind!!.compareTo(SkinMesh) == 0) {
            mBoneMatrixHandle = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_BoneMatrix")
        }
        mDiffuseHandle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_Diffuse")
        mAmbientHandle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_Ambient")
        mDirLightHandle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_DirLight")
    }

    private fun create2DShader() {
        createShader(ShaderSource.vertexShader2D, ShaderSource.fragmentShader2D)
        transformHandle = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_Transform")
        uvwhHandle = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_UvWH")
    }

    fun startProgram(Ind: String) {
        //シェーダプログラム適用
        ind = Ind
        GLES30.glUseProgram(mProgramHandle[ind]!!)
    }

    fun stopProgram() {
        //シェーダー停止
        GLES30.glUseProgram(0)
    }

    fun releaseProgram() {
        //プログラム解放
        GLES30.glDeleteProgram(mProgramHandle[Basic3D]!!)
        GLES30.glDeleteProgram(mProgramHandle[SkinMesh]!!)
        GLES30.glDeleteProgram(mProgramHandle[B2D]!!)
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
        mPositionHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind]!!, "a_Position")
        mNormalHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind]!!, "a_Normal")
        mUvHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind]!!, "a_Uv")
        if (ind!!.compareTo(SkinMesh) == 0) {
            mBoneIndHandle = GLES30.glGetAttribLocation(mProgramHandle[ind]!!, "a_BoneInd")
            mBoneWeiHandle = GLES30.glGetAttribLocation(mProgramHandle[ind]!!, "a_BoneWei")
        }
    }

    private fun AttachShader(vsHandle: Int, fsHandle: Int): Int {
        //シェーダプログラムをリンク
        var progHandle = GLES30.glCreateProgram()
        if (progHandle != 0) {
            GLES30.glAttachShader(progHandle, vsHandle) //バーテックスシェーダをアタッチ
            GLES30.glAttachShader(progHandle, fsHandle) //フラグメントシェーダをアタッチ
            GLES30.glBindAttribLocation(progHandle, 0, "a_Position") //attributeのindexを設定
            GLES30.glBindAttribLocation(progHandle, 1, "a_Normal")
            GLES30.glBindAttribLocation(progHandle, 2, "a_Uv")
            if (ind!!.compareTo(SkinMesh) == 0) {
                GLES30.glBindAttribLocation(progHandle, 3, "a_BoneInd")
                GLES30.glBindAttribLocation(progHandle, 4, "a_BoneWei")
            }
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