#include <jni.h>
#include <string>
#include <vector>
#include <sys/uio.h>
#include <unistd.h>
#include <fcntl.h>

// Baran hocam, bellek adreslerini ve değerlerini tutan yapı
struct ScanResult {
    uintptr_t address;
    int value;
};

std::vector<uintptr_t> candidate_addresses;

// Bellek okuma fonksiyonu (Root gerektirir)
bool read_mem(pid_t pid, uintptr_t addr, void* buffer, size_t size) {
    struct iovec local[1];
    struct iovec remote[1];
    local[0].iov_base = buffer;
    local[0].iov_len = size;
    remote[0].iov_base = (void*)addr;
    remote[0].iov_len = size;
    return process_vm_readv(pid, local, 1, remote, 1, 0) == size;
}

// Bellek yazma fonksiyonu
bool write_mem(pid_t pid, uintptr_t addr, void* buffer, size_t size) {
    struct iovec local[1];
    struct iovec remote[1];
    local[0].iov_base = buffer;
    local[0].iov_len = size;
    remote[0].iov_base = (void*)addr;
    remote[0].iov_len = size;
    return process_vm_writev(pid, local, 1, remote, 1, 0) == size;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_vergiai_inspector_NativeEngine_initialScan(JNIEnv* env, jobject thiz, jint pid, jint target_value) {
    candidate_addresses.clear();
    // Örnek: 0x7000000000 ile 0x8000000000 arasını tara (Normalde /proc/pid/maps okunmalı)
    // Hocam burası basitleştirilmiştir, tam sürümde haritayı parse ederiz.
    for (uintptr_t addr = 0x7000000000; addr < 0x7100000000; addr += 4) {
        int val = 0;
        if (read_mem(pid, addr, &val, sizeof(int)) && val == target_value) {
            candidate_addresses.push_back(addr);
        }
    }
    return candidate_addresses.size();
}

extern "C" JNIEXPORT void JNICALL
Java_com_vergiai_inspector_NativeEngine_patchInstruction(JNIEnv* env, jobject thiz, jint pid, jlong addr) {
    // Reklam kaldırma veya Pass Buyer için: Fonksiyonun başına RET (0xC3) veya NOP (0x90) yazar
    unsigned char nop_code = 0x90; 
    write_mem(pid, (uintptr_t)addr, &nop_code, 1);
}

