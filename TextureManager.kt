package jp.sugasato.opengles_wrapperkt

import android.opengl.GLES30
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import android.graphics.Bitmap.Config
import android.content.res.Resources

object TextureManager {

    private val options = BitmapFactory.Options()
    //テクスチャ名とテクスチャIdの組
    private val texNameId = HashMap<String, Int>()

    init {
        //リソース自動リサイズ無し
        options.inScaled = false
        //32bit画像として読み込む
        options.inPreferredConfig = Config.ARGB_8888
    }

    fun createTexture(resources: Resources, resId: Int, texName: String) {
        val textureId = IntArray(1)
        //Bitmap作成
        val bmp = BitmapFactory.decodeResource(resources, resId, options) ?: return
        //openGL用テクスチャ生成
        GLES30.glGenTextures(1, textureId, 0)//生成するテクスチャ数,テクスチャId配列,
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0])
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bmp, 0)
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR.toFloat())
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR.toFloat())
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        //openGLへ転送完了, VMメモリ上に作成したbitmap破棄
        bmp.recycle()
        //テクスチャ名とテクスチャIdを組で登録
        texNameId[texName] = textureId[0]
    }

    fun getTextureId(texName: CharArray): Int? {
        val keys = ArrayList(texNameId.keys)
        var hitString: String? = null
        for (key in keys) {
            if (nameComparison(texName, key)) {
                hitString = key
                break
            }
        }
        return texNameId[hitString]
    }

    fun getTextureId(texName: String): Int? {
        return texNameId[texName]
    }

    //テクスチャ削除
    fun deleteTexture(texName: String) {
        if (texNameId.containsKey(texName)) {
            val texId = IntArray(1)
            texId[0] = texNameId[texName]!!
            GLES30.glDeleteTextures(1, texId, 0)
            texNameId.remove(texName)
        }
    }

    //全テクスチャ削除
    fun deleteAll() {
        val keys = ArrayList(texNameId.keys)
        for (key in keys) {
            deleteTexture(key!!)
        }
    }
}