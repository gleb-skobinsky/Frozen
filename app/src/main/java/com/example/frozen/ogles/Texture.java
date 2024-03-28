package com.example.frozen.ogles;

public interface Texture {
    int getTexName();
    int getWidth();
    int getHeight();
    void setup();
    void release();
}