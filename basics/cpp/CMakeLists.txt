cmake_minimum_required(VERSION 3.12)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17 -O3")

set(CMAKE_INSTALL_PREFIX ${PROJECT_SOURCE_DIR})

add_subdirectory(src)
add_subdirectory(test)