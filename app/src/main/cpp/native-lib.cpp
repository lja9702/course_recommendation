#include <jni.h>
#include <string>
#include <math.h>
#include <opencv2/opencv.hpp>
using namespace cv;

extern "C" JNIEXPORT jstring JNICALL
Java_com_donsamo_babple_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_donsamo_babple_MainActivity_ConvertRGBtoGray(JNIEnv *env, jobject instance,
                                                      jlong matAddrInput, jlong matAddrResult) {

    // TODO
    Mat &matInput = *(Mat *)matAddrInput;

    Mat &matResult = *(Mat *)matAddrResult;


    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);

}