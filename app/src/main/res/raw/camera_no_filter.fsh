#extension GL_OES_EGL_image_external : require

precision mediump float;

//采样点的坐标
varying vec2 aCoord;

//采样器
uniform samplerExternalOES vTexture;

void main() {

    /// 正常
        gl_FragColor = texture2D(vTexture, aCoord);
        vec4 rgba = texture2D(vTexture, aCoord);
}

