package jp.sugasato.opengles_wrapperkt

class BasicPolygon {

    private var vaoId = 0
    private var numIndex = 0
    private var dp = DrawParameter()
    private val thetaMatX = FloatArray(16)
    private val thetaMatY = FloatArray(16)
    private val thetaMatZ = FloatArray(16)
    private val thetaMatZY = FloatArray(16)
    private val thetaMatZYX = FloatArray(16)
    private val mov = FloatArray(16)
    private val scale = FloatArray(16)
    private val scro = FloatArray(16)
    private val world = FloatArray(16)
    private var created = false

    fun create(
        Dp: DrawParameter,
        poshandle: Int,
        normalhandle: Int,
        uvhandle: Int,
        allVertices: FloatArray?,
        index: IntArray?,
        boneIndhandle: Int = -1,
        boneWeihandle: Int = -1
    ) {
        dp = Dp
        numIndex = index!!.size
        vaoId = Core.bindBufferObj(poshandle, normalhandle, uvhandle, allVertices, index, boneIndhandle, boneWeihandle)
        created = true
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
        scaz: Float = 1.0f,
        boneMatrix: FloatArray? = null
    ) {
        if (!created) return
        MatrixRotationX(thetaMatX, thex)
        MatrixRotationY(thetaMatY, they)
        MatrixRotationZ(thetaMatZ, thez)
        MatrixTranslation(mov, movx, movy, movz)
        MatrixScaling(scale, scax, scay, scaz)
        MatrixMultiply(thetaMatZY, thetaMatZ, thetaMatY)
        MatrixMultiply(thetaMatZYX, thetaMatZY, thetaMatX)
        MatrixMultiply(scro, thetaMatZYX, scale)
        MatrixMultiply(world, scro, mov)

        Core.draw(vaoId, numIndex, world, dp, boneMatrix)
    }
}