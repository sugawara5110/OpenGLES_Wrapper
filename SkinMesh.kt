package jp.sugasato.opengles_wrapperkt

import jp.sugasato.fbxloaderkt.FbxLoader
import android.content.Context
import jp.sugasato.fbxloaderkt.Deformer

class SkinMesh {

    private val fbx: FbxLoader = FbxLoader()
    private var bp: Array<BasicPolygon?>? = null
    private var dp: Array<DrawParameter?>? = null
    private var numMesh = 0
    private var numBone = 0
    private var bindPose: FloatArray? = null
    private var newPose: FloatArray? = null
    private var bone: FloatArray? = null
    private var currentframe = 0.0f
    private var endframe = 0.0f
    private var TimeFRAMES30on = true
    private var AttitudeMode: IntArray? = null
    private var MeshMode = GLShader.SkinMesh
    private var addNortex: Array<String?>? = null

    fun addCreateNortextureArray(num: Int) {
        addNortex = Array<String?>(num, { null })
    }

    fun addNortexture(index: Int, nortexName: String) {
        addNortex!![index] = nortexName
    }

    fun initialAttitudeMode() {
        MeshMode = GLShader.Basic3D
    }

    fun skinMeshMode() {
        MeshMode = GLShader.SkinMesh
    }

    fun create(con: Context, rawId: Int, endFrame: Float, timeFRAMES: String = "30") {
        if (timeFRAMES != "30") TimeFRAMES30on = false
        endframe = endFrame
        fbx.setFbxFile(con, rawId)
        numMesh = fbx.getNumFbxMeshNode()
        AttitudeMode = IntArray(numMesh, { MeshMode })
        numBone = fbx.getFbxMeshNode(0)!!.GetNumDeformer()
        bindPose = FloatArray(numBone * 16)
        newPose = FloatArray(numBone * 16)
        bone = FloatArray(numBone * 16)
        bp = Array<BasicPolygon?>(numMesh, { i -> BasicPolygon() })
        dp = Array<DrawParameter?>(numMesh, { i -> DrawParameter() })
        //各meshデータ読み込み
        for (i: Int in 0 until numMesh) {
            val mesh = fbx.getFbxMeshNode(i)
            val index = mesh!!.GetPolygonVertices()
            val ver = mesh.GetVertices()
            val nor = mesh.getNormal(0)
            val uv = mesh.getAlignedUV(0)

            if (mesh.getDiffuseTextureName(0).getName() != null) {
                val diffName = getNameFromPass(mesh.getDiffuseTextureName(0).getName())
                dp!![i]!!.diffTexId = TextureManager.getTextureId(diffName)!!
            }
            if (mesh.getNormalTextureName(0).getName() != null) {
                val norName = getNameFromPass(mesh.getNormalTextureName(0).getName())
                dp!![i]!!.norTexId = TextureManager.getTextureId(norName)!!
                if (MeshMode == GLShader.Basic3D) AttitudeMode!![i] = GLShader.Basic3DNormal
                if (MeshMode == GLShader.SkinMesh) AttitudeMode!![i] = GLShader.SkinMeshNormal
                dp!![i]!!.Texture1Handle = GLShader.texture1Handle(AttitudeMode!![i])!!
            }
            if (addNortex != null && addNortex!![i] != null) {
                dp!![i]!!.norTexId = TextureManager.getTextureId(addNortex!![i]!!)!!
                if (MeshMode == GLShader.Basic3D) AttitudeMode!![i] = GLShader.Basic3DNormal
                if (MeshMode == GLShader.SkinMesh) AttitudeMode!![i] = GLShader.SkinMeshNormal
                dp!![i]!!.Texture1Handle = GLShader.texture1Handle(AttitudeMode!![i])!!
            }

            dp!![i]!!.MVPMatrixHandle = GLShader.mvpMatrixHandle(AttitudeMode!![i])!!
            dp!![i]!!.WorldMatrixHandle = GLShader.worldMatrixHandle(AttitudeMode!![i])!!
            if (MeshMode == GLShader.SkinMesh) dp!![i]!!.BoneMatrixHandle =
                GLShader.boneMatrixHandle(AttitudeMode!![i])!!
            dp!![i]!!.DirLightHandle = GLShader.DirLightHandle(AttitudeMode!![i])!!
            dp!![i]!!.DiffuseHandle = GLShader.DiffuseHandle(AttitudeMode!![i])!!
            dp!![i]!!.AmbientHandle = GLShader.AmbientHandle(AttitudeMode!![i])!!
            dp!![i]!!.Diffuse[0] = mesh.getDiffuseColor(0, 0).toFloat()
            dp!![i]!!.Diffuse[1] = mesh.getDiffuseColor(0, 1).toFloat()
            dp!![i]!!.Diffuse[2] = mesh.getDiffuseColor(0, 2).toFloat()
            dp!![i]!!.Diffuse[3] = 1.0f
            dp!![i]!!.Ambient[0] = mesh.getAmbientColor(0, 0).toFloat()
            dp!![i]!!.Ambient[1] = mesh.getAmbientColor(0, 1).toFloat()
            dp!![i]!!.Ambient[2] = mesh.getAmbientColor(0, 2).toFloat()
            dp!![i]!!.Ambient[3] = 0.0f

            //ボーン
            val numBoneWei = 4
            val boneWeightArr = FloatArray(numBoneWei * mesh.GetNumVertices(), { 0.0f })
            val boneWeightIndArr = IntArray(numBoneWei * mesh.GetNumVertices(), { 0 })
            for (i1 in 0 until mesh.GetNumDeformer()) {
                val defo = mesh.getDeformer(i1)//meshのDeformer(i1)
                val bNum = defo!!.getIndicesCnt()//このボーンに影響を受ける頂点インデックス数
                val bInd = defo.GetIndices()//このボーンに影響を受ける頂点のインデックス配列
                val bWei = defo.GetWeights()//このボーンに影響を受ける頂点のウエイト配列
                for (k in 0 until bNum) {
                    val bindex = bInd!![k]//影響を受ける頂点
                    val weight = bWei!![k]//ウエイト
                    for (m in 0..3) {
                        //各Bone毎に影響を受ける頂点のウエイトを一番大きい数値に更新していく
                        val ind = bindex * numBoneWei + m
                        if (weight > boneWeightArr[ind]) {//調べたウエイトの方が大きい
                            boneWeightIndArr[ind] = i1//Boneインデックス登録
                            boneWeightArr[ind] = weight.toFloat()//ウエイト登録
                            break
                        }
                    }
                }
            }

            //ウエイト正規化
            for (i1 in 0 until mesh.GetNumVertices()) {
                var we = 0.0f
                for (m in 0..3) {
                    val ind = i1 * numBoneWei + m
                    we += boneWeightArr[ind]
                }
                val we1 = 1.0f / we
                for (m in 0..3) {
                    val ind = i1 * numBoneWei + m
                    boneWeightArr[ind] *= we1
                }
            }

            var verOneSIze = Core.numVerNorUV
            if (MeshMode == GLShader.SkinMesh) {
                verOneSIze = Core.numVerNorUVBoneIndWei
            }
            val allVertices = FloatArray(verOneSIze * mesh.GetNumPolygonVertices())
            var nCnt = 0
            var uvCnt = 0
            var aCnt = 0
            var vCnt = 0
            for (i1 in 0 until mesh.GetNumPolygonVertices()) {
                allVertices[aCnt++] = ver!![index!![i1] * 3].toFloat()
                allVertices[aCnt++] = ver[index[i1] * 3 + 1].toFloat()
                allVertices[aCnt++] = ver[index[i1] * 3 + 2].toFloat()
                allVertices[aCnt++] = nor!![nCnt++].toFloat()
                allVertices[aCnt++] = nor[nCnt++].toFloat()
                allVertices[aCnt++] = nor[nCnt++].toFloat()
                allVertices[aCnt++] = uv!![uvCnt++].toFloat()
                allVertices[aCnt++] = 1.0f - uv[uvCnt++].toFloat()
                if (MeshMode == GLShader.SkinMesh) {
                    for (i2 in 0..3) {
                        allVertices[aCnt++] = boneWeightIndArr[index[i1] * 4 + i2].toFloat()
                    }
                    for (i2 in 0..3) {
                        allVertices[aCnt++] = boneWeightArr[index[i1] * 4 + i2]
                    }
                }
            }

            //4頂点ポリゴン分割後のIndex数カウント
            var numNewIndex = 0
            for (i1 in 0 until mesh.GetNumPolygon()) {
                if (mesh.getPolygonSize(i1) == 3) {
                    numNewIndex += 3
                }
                if (mesh.getPolygonSize(i1) == 4) {
                    numNewIndex += 6
                }
            }
            //分割後のIndex生成, 順番を逆にする
            val newIndex = IntArray(numNewIndex)
            var nIcnt = 0
            var Icnt = 0
            for (i1 in 0 until mesh.GetNumPolygon()) {
                if (mesh.getPolygonSize(i1) == 3) {
                    newIndex[nIcnt++] = Icnt
                    newIndex[nIcnt++] = Icnt + 2
                    newIndex[nIcnt++] = Icnt + 1
                    Icnt += 3
                }
                if (mesh.getPolygonSize(i1) == 4) {
                    newIndex[nIcnt++] = Icnt
                    newIndex[nIcnt++] = Icnt + 2
                    newIndex[nIcnt++] = Icnt + 1
                    newIndex[nIcnt++] = Icnt
                    newIndex[nIcnt++] = Icnt + 3
                    newIndex[nIcnt++] = Icnt + 2
                    Icnt += 4
                }
            }

            var boneIndH = 0
            var boneWeiH = 0
            if (MeshMode == GLShader.SkinMesh) {
                boneIndH = GLShader.boneIndHandle(AttitudeMode!![i])!!
                boneWeiH = GLShader.boneWeiHandle(AttitudeMode!![i])!!
            }
            bp!![i]!!.create(
                dp!![i]!!,
                GLShader.positionHandle(AttitudeMode!![i])!!,
                GLShader.normalHandle(AttitudeMode!![i])!!,
                GLShader.uvHandle(AttitudeMode!![i])!!,
                allVertices,
                newIndex,
                boneIndH,
                boneWeiH
            )
        }
        val mesh = fbx.getFbxMeshNode(0)
        //初期姿勢行列読み込み
        for (i: Int in 0 until numBone) {
            val defo = mesh!!.getDeformer(i)
            for (y in 0..3) {
                for (x in 0..3) {
                    bindPose!![i * 16 + y * 4 + x] = defo!!.getTransformLinkMatrix(y, x).toFloat()
                }
            }
        }
    }

    private fun setNewPoseMatrices(time: Float): Boolean {
        currentframe += time
        if (endframe < currentframe) {
            currentframe = 0.0f
            return false
        }
        val frame: Int = currentframe.toInt()
        val de = Deformer()
        var ti: Long = 0
        if (TimeFRAMES30on) {
            ti = de.getTimeFRAMES30(frame)
        } else {
            ti = de.getTimeFRAMES60(frame)
        }
        //次のポーズ行列
        val mesh = fbx.getFbxMeshNode(0)
        for (i: Int in 0 until numBone) {
            val defo = mesh!!.getDeformer(i)
            defo!!.EvaluateGlobalTransform(ti)
            for (y in 0..3) {
                for (x in 0..3) {
                    newPose!![i * 16 + y * 4 + x] = defo.getEvaluateGlobalTransform(y, x).toFloat()
                }
            }
        }
        return true
    }

    private fun getCurrentPoseMatrix() {
        for (i in 0 until numBone) {
            val stInd = i * 16
            val endInd = stInd + 16
            val elBindPose = bindPose!!.copyOfRange(stInd, endInd)
            val elNewPose = newPose!!.copyOfRange(stInd, endInd)
            val inv = FloatArray(16)
            MatrixInverse(inv, elBindPose)
            val ret = FloatArray(16)
            MatrixMultiply(ret, inv, elNewPose)
            var retInd = 0
            for (i1 in stInd until endInd) {
                bone!![i1] = ret[retInd++]
            }
        }
    }

    fun addALPHA(al: Float) {
        for (i: Int in 0 until numMesh) {
            dp!![i]!!.Ambient[3] = al
        }
    }

    fun draw(
        ti: Float,
        movx: Float = 0.0f,
        movy: Float = 0.0f,
        movz: Float = 0.0f,
        thex: Float = 0.0f,
        they: Float = 0.0f,
        thez: Float = 0.0f,
        scax: Float = 1.0f,
        scay: Float = 1.0f,
        scaz: Float = 1.0f
    ) {
        setNewPoseMatrices(ti)
        getCurrentPoseMatrix()
        for (i: Int in 0 until numMesh) {
            GLShader.startProgram(AttitudeMode!![i])
            bp!![i]!!.draw(movx, movy, movz, thex, they, thez, scax, scay, scaz, bone)
        }
    }
}