package jp.sugasato.opengles_wrapperkt

import jp.sugasato.fbxloaderkt.FbxLoader
import android.content.Context
import java.util.*

class SkinMesh {

    private val fbx: FbxLoader = FbxLoader()
    private var bp: Array<BasicPolygon?>? = null
    private var diffTexId: IntArray? = null
    private var norTexId: IntArray? = null
    private var numMesh = 0
    private var numBone = 0

    fun create(con: Context, rawId: Int) {
        fbx.setFbxFile(con, rawId)
        numMesh = fbx.getNumFbxMeshNode()
        numBone = fbx.getFbxMeshNode(0)!!.GetNumDeformer()
        bp = Array<BasicPolygon?>(numMesh, { i -> BasicPolygon() })
        diffTexId = IntArray(numMesh, { -1 })
        norTexId = IntArray(numMesh, { -1 })
        //各meshデータ読み込み
        for (i: Int in 0..numMesh - 1) {
            val mesh = fbx.getFbxMeshNode(i)
            val index = mesh!!.GetPolygonVertices()
            val ver = mesh.GetVertices()
            val nor = mesh.getNormal(0)
            val uv = mesh.getAlignedUV(0)
            if (mesh.getNormalTextureName(0).getName() != null) {
                val norName = getNameFromPass(mesh.getNormalTextureName(0).getName())
               // norTexId!![i] = TextureManager.getTextureId(norName)!!
            }
            if (mesh.getDiffuseTextureName(0).getName() != null) {
                val diffName = getNameFromPass(mesh.getDiffuseTextureName(0).getName())
              //  diffTexId!![i] = TextureManager.getTextureId(diffName)!!
            }
        }
        //各bone読み込み
        for (i: Int in 0..numBone - 1) {

        }
    }
}