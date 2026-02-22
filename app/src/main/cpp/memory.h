#ifndef MEMORY_H
#define MEMORY_H

#include <sys/uio.h>
#include <unistd.h>
#include <vector>

// Bellek Okuma Yardımcısı
inline bool read_mem(pid_t pid, uintptr_t addr, void* buffer, size_t size) {
    struct iovec local[1];
    struct iovec remote[1];
    local[0].iov_base = buffer;
    local[0].iov_len = size;
    remote[0].iov_base = (void*)addr;
    remote[0].iov_len = size;
    return process_vm_readv(pid, local, 1, remote, 1, 0) == size;
}

// Bellek Yazma Yardımcısı
inline bool write_mem(pid_t pid, uintptr_t addr, void* buffer, size_t size) {
    struct iovec local[1];
    struct iovec remote[1];
    local[0].iov_base = buffer;
    local[0].iov_len = size;
    remote[0].iov_base = (void*)addr;
    remote[0].iov_len = size;
    return process_vm_writev(pid, local, 1, remote, 1, 0) == size;
}

#endif // MEMORY_H

