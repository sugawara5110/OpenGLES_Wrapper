package jp.sugasato.opengles_wrapperkt

object ShaderSource {

    //バーテックスシェーダ3D
    const val vertexShader3D = (
            "#version 300 es\n"
                    + "uniform mat4 u_MVPMatrix;\n"
                    + "uniform mat4 u_WorldMatrix;\n"
                    + "in vec4 a_Position;\n"
                    + "in vec4 a_Normal;\n"
                    + "in vec2 a_Uv;\n"

                    + "out vec3 v_Normal;\n"
                    + "out vec2 v_Uv;\n"

                    + "void main()\n"
                    + "{\n"
                    + "   v_Normal = normalize((u_WorldMatrix * a_Normal).xyz);\n"
                    + "   v_Uv = a_Uv;\n"
                    + "   gl_Position = u_MVPMatrix * a_Position;\n"
                    + "}\n")

    //バーテックスシェーダSkinMesh
    const val vertexShaderSkinMesh = (
            "#version 300 es\n"
                    + "uniform mat4 u_MVPMatrix;\n"
                    + "uniform mat4 u_WorldMatrix;\n"
                    + "uniform mat4 u_BoneMatrix[128];\n"
                    + "in vec4 a_Position;\n"
                    + "in vec4 a_Normal;\n"
                    + "in vec2 a_Uv;\n"
                    + "in vec4 a_BoneInd;\n"
                    + "in vec4 a_BoneWei;\n"

                    + "out vec3 v_Normal;\n"
                    + "out vec2 v_Uv;\n"

                    + "void main()\n"
                    + "{\n"
                    //スキニング
                    + "   vec4 sInPos = a_Position;\n"
                    + "   vec3 sInNor = a_Normal.xyz;\n"
                    + "   vec4 sOutPos = vec4(0.0, 0.0, 0.0, 1.0);\n"
                    + "   vec3 sOutNor = vec3(0.0, 0.0, 0.0);\n"
                    //Bone1
                    + "   int iBone = int(a_BoneInd.x);\n"//1個目のBoneIndex取り出し
                    + "   float fwei = a_BoneWei.x;\n"//1個目のBoneWeight取り出し
                    + "   mat4 m = u_BoneMatrix[iBone];\n"//姿勢行列配列からiBone番目行列取り出し
                    + "   sOutPos += (m * sInPos) * fwei;\n"//スキニング後頂点 = 姿勢行列 * 頂点 * 頂点ウエイト(DirectXとは逆)
                    + "   sOutNor += (mat3(m) * sInNor) * fwei;\n"//スキニング後法線 = 姿勢行列 * 法線 * 頂点ウエイト
                    //Bone2
                    + "   iBone = int(a_BoneInd.y);\n"
                    + "   fwei = a_BoneWei.y;\n"
                    + "   m = u_BoneMatrix[iBone];\n"
                    + "   sOutPos += (m * sInPos) * fwei;\n"
                    + "   sOutNor += (mat3(m) * sInNor) * fwei;\n"
                    //Bone3
                    + "   iBone = int(a_BoneInd.z);\n"
                    + "   fwei = a_BoneWei.z;\n"
                    + "   m = u_BoneMatrix[iBone];\n"
                    + "   sOutPos += (m * sInPos) * fwei;\n"
                    + "   sOutNor += (mat3(m) * sInNor) * fwei;\n"
                    //Bone4
                    + "   iBone = int(a_BoneInd.w);\n"
                    + "   fwei = a_BoneWei.w;\n"
                    + "   m = u_BoneMatrix[iBone];\n"
                    + "   sOutPos += (m * sInPos) * fwei;\n"
                    + "   sOutNor += (mat3(m) * sInNor) * fwei;\n"

                    + "   v_Normal = normalize(mat3(u_WorldMatrix) * sOutNor);\n"
                    + "   v_Uv = a_Uv;\n"
                    + "   gl_Position = u_MVPMatrix * sOutPos;\n"
                    + "}\n")

    //フラグメントシェーダ3D, NormalTexture無し
    const val fragmentShader3D = (
            "#version 300 es\n"
                    + "precision mediump float;\n"
                    + "uniform sampler2D texture0;\n"
                    + "uniform vec4 u_Diffuse;\n"
                    + "uniform vec4 u_Ambient;\n"
                    + "uniform vec3 u_DirLight;\n"
                    + "in vec3 v_Normal;\n"
                    + "in vec2 v_Uv;\n"

                    + "out vec4 outColor;\n"

                    + "void main()\n"
                    + "{\n"
                    + "   vec3 dir = normalize(u_DirLight);\n"
                    + "   float dirLight = max(dot(v_Normal, dir), 0.0);\n"
                    + "   vec4 light = (u_Diffuse * dirLight);\n"
                    + "   light.w = 1.0;\n"
                    + "   vec4 light1 = light + u_Ambient;\n"
                    + "   outColor = texture(texture0, v_Uv) * light1;\n"
                    + "}\n")

    //フラグメントシェーダ3D, NormalTexture有り
    const val fragmentShader3DNorTex = (
            "#version 300 es\n"
                    + "precision mediump float;\n"
                    + "uniform sampler2D texture0;\n"
                    + "uniform sampler2D texture1;\n"
                    + "uniform vec4 u_Diffuse;\n"
                    + "uniform vec4 u_Ambient;\n"
                    + "uniform vec3 u_DirLight;\n"
                    + "in vec3 v_Normal;\n"
                    + "in vec2 v_Uv;\n"

                    + "out vec4 outColor;\n"

                    + "void main()\n"
                    + "{\n"
                    + "   vec4 norT = texture(texture1, v_Uv);\n"
                    + "   vec3 NT = normalize(v_Normal * norT.xyz);\n"
                    + "   vec3 dir = normalize(u_DirLight);\n"
                    + "   float dirLight = max(dot(NT, dir), 0.0);\n"
                    + "   vec4 light = (u_Diffuse * dirLight);\n"
                    + "   light.w = 1.0;\n"
                    + "   vec4 light1 = light + u_Ambient;\n"
                    + "   outColor = texture(texture0, v_Uv) * light1;\n"
                    + "}\n")

    //バーテックスシェーダ2D
    const val vertexShader2D = (
            "#version 300 es\n"
                    + "uniform vec2 u_Transform;\n"
                    + "uniform vec4 u_UvWH; \n"

                    + "in vec2 a_Position;\n"
                    + "in vec2 a_Uv;\n"

                    + "out vec2 v_Uv;\n"

                    + "void main()\n"
                    + "{\n"
                    + "   v_Uv.x = a_Uv.x + u_UvWH.x;\n"
                    + "   v_Uv.y = a_Uv.y + u_UvWH.y;\n"
                    + "   gl_Position.x = (a_Position.x + u_Transform.x) / u_UvWH.z * 2.0 - 1.0;\n"
                    + "   gl_Position.y = (a_Position.y + u_Transform.y) / u_UvWH.w * 2.0 - 1.0;\n"
                    + "   gl_Position.z = 0.0;\n"
                    + "   gl_Position.w = 1.0;\n"
                    + "}\n")

    //フラグメントシェーダ2D
    const val fragmentShader2D = (
            "#version 300 es\n"
                    + "precision mediump float;\n"
                    + "uniform sampler2D texture0;\n"

                    + "in vec2 v_Uv;\n"

                    + "out vec4 outColor;\n"

                    + "void main()\n"
                    + "{\n"
                    + "   outColor = texture(texture0, v_Uv);\n"
                    + "}\n")
}