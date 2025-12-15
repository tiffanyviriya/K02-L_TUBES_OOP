package main.util;

public class InputBuffer {
    public boolean up, down, left, right, action, interact;

    public void reset() {
        up = false;
        down = false;
        left = false;
        right = false;
        action = false;
        interact = false;
    }
}