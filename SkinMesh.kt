package jp.sugasato.opengles_wrapperkt

import jp.sugasato.fbxloaderkt.FbxLoader
import android.content.Context
import java.util.*

class SkinMesh {

    private val fbx: FbxLoader = FbxLoader()
    private var bp: Array<BasicPolygon?>? = null
    private var dp: Array<DrawParameter?>? = null
    private var numMesh = 0
    private var numBone = 0

    fun create(con: Context, rawId: Int) {
        fbx.setFbxFile(con, rawId)
        numMesh = fbx.getNumFbxMeshNode()
        numBone = fbx.getFbxMeshNode(0)!!.GetNumDeformer()
        bp = Array<BasicPolygon?>(numMesh, { i -> BasicPolygon() })
        dp = Array<DrawParameter?>(numMesh, { i -> DrawParameter() })
        //各meshデータ読み込み
        for (i: Int in 0..numMesh - 1) {
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
            }
            dp!![i]!!.MVPMatrixHandle = GLShader.mvpMatrixHandle(0)
            dp!![i]!!.WorldMatrixHandle = GLShader.worldMatrixHandle(0)
            dp!![i]!!.DirLightHandle = GLShader.DirLightHandle(0)
            dp!![i]!!.DiffuseHandle = GLShader.DiffuseHandle(0)
            dp!![i]!!.AmbientHandle = GLShader.AmbientHandle(0)
            dp!![i]!!.Diffuse[0] = mesh.getDiffuseColor(0, 0).toFloat()
            dp!![i]!!.Diffuse[1] = mesh.getDiffuseColor(0, 1).toFloat()
            dp!![i]!!.Diffuse[2] = mesh.getDiffuseColor(0, 2).toFloat()
            dp!![i]!!.Diffuse[3] = 1.0f
            dp!![i]!!.Ambient[0] = mesh.getAmbientColor(0, 0).toFloat()
            dp!![i]!!.Ambient[1] = mesh.getAmbientColor(0, 1).toFloat()
            dp!![i]!!.Ambient[2] = mesh.getAmbientColor(0, 2).toFloat()
            dp!![i]!!.Ambient[3] = 0.0f

            val allVertices = FloatArray(Core.numVerNorUV * mesh.GetNumPolygonVertices())
            var nCnt = 0
            var uvCnt = 0
            var aCnt = 0
            for (i1 in 0..mesh.GetNumPolygonVertices() - 1) {
                allVertices[aCnt++] = ver!![index!![i1] * 3].toFloat()
                allVertices[aCnt++] = ver[index[i1] * 3 + 1].toFloat()
                allVertices[aCnt++] = ver[index[i1] * 3 + 2].toFloat()
                allVertices[aCnt++] = nor!![nCnt++].toFloat()
                allVertices[aCnt++] = nor[nCnt++].toFloat()
                allVertices[aCnt++] = nor[nCnt++].toFloat()
                allVertices[aCnt++] = uv!![uvCnt++].toFloat()
                allVertices[aCnt++] = 1.0f - uv[uvCnt++].toFloat()
            }
            //4頂点ポリゴン分割後のIndex数カウント
            var numNewIndex = 0
            for (i1 in 0..mesh.GetNumPolygon() - 1) {
                if (mesh.getPolygonSize(i1) == 3) {
                    numNewIndex += 3
                } else {
                    numNewIndex += 6
                }
            }
            //分割後のIndex生成, 順番を逆にする
            val newIndex = IntArray(numNewIndex)
            var nIcnt = 0
            var Icnt = 0
            for (i1 in 0..mesh.GetNumPolygon() - 1) {
                newIndex[nIcnt++] = Icnt
                newIndex[nIcnt++] = Icnt + 2
                newIndex[nIcnt++] = Icnt + 1
                Icnt += 3
                if (mesh.getPolygonSize(i1) == 4) {
                    newIndex[nIcnt++] = Icnt
                    newIndex[nIcnt++] = Icnt + 3
                    newIndex[nIcnt++] = Icnt + 2
                    Icnt += 3
                }
            }

            bp!![i]!!.create(
                dp!![i]!!,
                GLShader.positionHandle(0),
                GLShader.normalHandle(0),
                GLShader.uvHandle(0),
                allVertices,
                newIndex
            )

        }
        //各bone読み込み
        for (i: Int in 0..numBone - 1) {

        }
    }

    fun draw(
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
        Core.DepthTestOff()
        for (i: Int in 0..numMesh - 1) {
            bp!![i]!!.draw(movx, movy, movz, thex, they, thez, scax, scay, scaz)
        }
    }
}