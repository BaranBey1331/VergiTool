cmake_minimum_required(VERSION 3.22.1)

project("inspector-core")

add_library(
    inspector-core
    SHARED
    native-lib.cpp
    scanner.cpp
)

find_library(log-lib log)

target_link_libraries(
    inspector-core
    ${log-lib}
)

