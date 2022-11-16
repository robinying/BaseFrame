#include <android/log.h>
#include <jni.h>
#include <string>

#define TAG "HelloJNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_com_robin_baseframe_test_DemoJni_sayHi(JNIEnv *env, jobject thiz) {
    LOGD("%s", "Java 调用 Native 方法 sayHi：Hello Jni!");
}