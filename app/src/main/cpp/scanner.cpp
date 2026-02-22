#include <jni.h>
#include <iostream>
#include <fstream>
#include <string>
#include <vector>

struct MemoryRegion {
    uintptr_t start;
    uintptr_t end;
};

// Baran hocam, bu fonksiyon sadece geçerli bellek bölgelerini bulur
std::vector<MemoryRegion> get_writable_regions(pid_t pid) {
    std::vector<MemoryRegion> regions;
    std::string maps_path = "/proc/" + std::to_string(pid) + "/maps";
    std::ifstream maps_file(maps_path);
    std::string line;

    while (std::getline(maps_file, line)) {
        // Sadece 'rw' (Read-Write) izni olan bölgeleri tara
        if (line.find("rw-p") != std::string::npos) {
            uintptr_t start, end;
            sscanf(line.c_str(), "%lx-%lx", &start, &end);
            regions.push_back({start, end});
        }
    }
    return regions;
}

// Global aday listesi (PointerScan temeli)
std::vector<uintptr_t> g_candidates;

extern "C" JNIEXPORT jint JNICALL
Java_com_vergiai_inspector_NativeEngine_initialScan(JNIEnv* env, jobject thiz, jint pid, jint target_value) {
    g_candidates.clear();
    auto regions = get_writable_regions(pid);

    for (const auto& region : regions) {
        for (uintptr_t addr = region.start; addr < region.end - sizeof(int); addr += 4) {
            int val = 0;
            // read_mem fonksiyonunu bir önceki mesajda vermiştik hocam
            if (read_mem(pid, addr, &val, sizeof(int)) && val == target_value) {
                g_candidates.push_back(addr);
            }
        }
    }
    return g_candidates.size();
}

