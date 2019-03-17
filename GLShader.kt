package jp.sugasato.opengles_wrapperkt

import android.opengl.GLES30

object GLShader {

    const val Basic3D = 1
    const val Basic3DNormal = 2
    const val SkinMesh = 3
    const val SkinMeshNormal = 4
    const val B2D = 5
    private val mProgramHandle = HashMap<Int, Int>()
    private val mMVPMatrixHandle = HashMap<Int, Int>()//u_MVPMatrixのハンドル
    private val mWorldMatrixHandle = HashMap<Int, Int>()//u_WorldMatrixのハンドル
    private val mTexture1Handle = HashMap<Int, Int>()//texture1のハンドル
    private val mDiffuseHandle = HashMap<Int, Int>()//u_Diffuseのハンドル
    private val mAmbientHandle = HashMap<Int, Int>()//u_Ambientのハンドル
    private val mDirLightHandle = HashMap<Int, Int>()//u_DirLightのハンドル
    private val mPositionHandle = HashMap<Int, Int>()//a_Positionのハンドル
    private val mNormalHandle = HashMap<Int, Int>()//a_Normalのハンドル
    private val mUvHandle = HashMap<Int, Int>()
    //SkinMesh
    private var mBoneMatrixHandle = HashMap<Int, Int>()//u_BoneMatrixのハンドル
    private var mBoneIndHandle = HashMap<Int, Int>()//a_BoneIndのハンドル
    private var mBoneWeiHandle = HashMap<Int, Int>()//a_BoneWeiのハンドル
    //2D用
    private var transformHandle: Int = -1
    private var uvwhHandle: Int = -1
    private var ind: Int = -1

    fun programHandle(ind: Int): Int? {
        return mProgramHandle[ind]
    }

    fun mvpMatrixHandle(ind: Int): Int? {
        return mMVPMatrixHandle[ind]
    }

    fun worldMatrixHandle(ind: Int): Int? {
        return mWorldMatrixHandle[ind]
    }

    fun texture1Handle(ind: Int): Int? {
        return mTexture1Handle[ind]
    }

    fun DiffuseHandle(ind: Int): Int? {
        return mDiffuseHandle[ind]
    }

    fun AmbientHandle(ind: Int): Int? {
        return mAmbientHandle[ind]
    }

    fun DirLightHandle(ind: Int): Int? {
        return mDirLightHandle[ind]
    }

    fun positionHandle(ind: Int): Int? {
        return mPositionHandle[ind]
    }

    fun normalHandle(ind: Int): Int? {
        return mNormalHandle[ind]
    }

    fun uvHandle(ind: Int): Int? {
        return mUvHandle[ind]
    }

    fun boneMatrixHandle(ind: Int): Int? {
        return mBoneMatrixHandle[ind]
    }

    fun boneIndHandle(ind: Int): Int? {
        return mBoneIndHandle[ind]
    }

    fun boneWeiHandle(ind: Int): Int? {
        return mBoneWeiHandle[ind]
    }

    fun createShader() {
        ind = Basic3D
        create3DShader(ShaderSource.vertexShader3D, ShaderSource.fragmentShader3D)
        ind = Basic3DNormal
        create3DShader(ShaderSource.vertexShader3D, ShaderSource.fragmentShader3DNorTex)
        ind = SkinMesh
        create3DShader(ShaderSource.vertexShaderSkinMesh, ShaderSource.fragmentShader3D)
        ind = SkinMeshNormal
        create3DShader(ShaderSource.vertexShaderSkinMesh, ShaderSource.fragmentShader3DNorTex)
        ind = B2D
        create2DShader()
    }

    private fun create3DShader(vs: String, fs: String) {
        createShader(vs, fs)
        mMVPMatrixHandle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_MVPMatrix")
        mWorldMatrixHandle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_WorldMatrix")
        if (ind == SkinMesh || ind == SkinMeshNormal) {
            mBoneMatrixHandle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "u_BoneMatrix")
        }
        if (ind == Basic3DNormal || ind == SkinMeshNormal) {
            mTexture1Handle[ind] = GLES30.glGetUniformLocation(mProgramHandle[ind]!!, "texture1")
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

    fun startProgram(Ind: Int) {
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
        GLES30.glDeleteProgram(mProgramHandle[Basic3DNormal]!!)
        GLES30.glDeleteProgram(mProgramHandle[SkinMesh]!!)
        GLES30.glDeleteProgram(mProgramHandle[SkinMeshNormal]!!)
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
        if (ind == SkinMesh || ind == SkinMeshNormal) {
            mBoneIndHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind]!!, "a_BoneInd")
            mBoneWeiHandle[ind] = GLES30.glGetAttribLocation(mProgramHandle[ind]!!, "a_BoneWei")
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
            if (ind == SkinMesh || ind == SkinMeshNormal) {
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