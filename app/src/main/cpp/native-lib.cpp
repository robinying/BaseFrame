#include <android/log.h>
#include <jni.h>
#include <string>
#include <vector>

#define TAG "HelloJNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_com_robin_baseframe_test_DemoJni_sayHi(JNIEnv *env, jobject thiz) {
    LOGD("%s", "Java 调用 Native 方法 sayHi：Hello Jni!");
}
extern "C"
JNIEXPORT void JNICALL
Java_com_robin_baseframe_test_DemoJni_accessField(JNIEnv *env, jobject thiz) {
    // 找到类
   jclass clz = env->GetObjectClass(thiz);
    jfieldID sFieldId = env->GetStaticFieldID(clz, "sName", "Ljava/lang/String;");
    // 访问静态字段
    if (sFieldId) {
        // Java 方法的返回值 String 映射为 jstring
        jstring jStr = static_cast<jstring>(env->GetStaticObjectField(clz, sFieldId));
        // 将 jstring 转换为 C/C++ 字符串
        const char *sStr = env->GetStringUTFChars(jStr, JNI_FALSE);
        LOGD("静态字段：%s", sStr);
        // 释放资源
        env->ReleaseStringUTFChars(jStr, sStr);
    }
    //找到方法
    jmethodID mid = env->GetMethodID(clz, "callFromJava", "()V");

    // 调用 Java 方法
    env->CallVoidMethod(thiz, mid);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_robin_baseframe_test_DemoJni_test(JNIEnv *env, jobject thiz) {
    std::vector<int> nums = {1, 2, 3, 4, 5};
    std::string result = "Vector size: " + std::to_string(nums.size());
    LOGD("test",result.c_str());
}