#include <jni.h>
#include <string>
#include "memory.h"

extern "C" JNIEXPORT jint JNICALL
Java_com_vergiai_inspector_NativeEngine_nextScan(JNIEnv* env, jobject thiz, jint pid, jint target_value) {
    // Next Scan mantığı buraya gelecek, şimdilik placeholder
    return 0; 
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_vergiai_inspector_NativeEngine_writeMemory(JNIEnv* env, jobject thiz, jint pid, jlong address, jint value) {
    return write_mem(pid, (uintptr_t)address, &value, sizeof(int));
}

extern "C" JNIEXPORT void JNICALL
Java_com_vergiai_inspector_NativeEngine_patchInstruction(JNIEnv* env, jobject thiz, jint pid, jlong addr) {
    unsigned char nop_code = 0x90; 
    write_mem(pid, (uintptr_t)addr, &nop_code, 1);
}
