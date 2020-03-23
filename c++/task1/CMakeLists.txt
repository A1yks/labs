cmake_minimum_required(VERSION 3.14)
project(task1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set(CMAKE_EXE_LINKER_FLAGS "-static-libgcc -static-libstdc++")
set(SOURCE_FILES main.cpp)
add_executable(task1 ${SOURCE_FILES})
target_link_libraries(task1 ${CMAKE_EXE_LINKER_FLAGS})