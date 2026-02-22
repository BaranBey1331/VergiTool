package com.vergiai.inspector

class NativeEngine {
    companion object {
        init {
            // C++ kütüphanesini yükle
            System.loadLibrary("inspector-core")
        }
    }

    // Yerel fonksiyon tanımları
    external fun initialScan(pid: Int, targetValue: Int): Int
    external fun nextScan(pid: Int, targetValue: Int): Int
    external fun writeMemory(pid: Int, address: Long, value: Int): Boolean
    external fun patchInstruction(pid: Int, address: Long): Unit
}

