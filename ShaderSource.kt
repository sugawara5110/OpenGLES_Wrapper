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

    //フラグメントシェーダ3D
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
                    + "   vec4 light = (u_Diffuse * dirLight) + u_Ambient;\n"
                    + "   outColor = texture(texture0, v_Uv) * light;\n"
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