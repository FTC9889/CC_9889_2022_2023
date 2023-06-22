package com.team9889.ftc2021.auto;

/**
 * Created by edm11 on 6/19/2023.
 */
public class AutoSettings {
    public int wait = 0;

    // 0 = Center High, 1 = Center High Defensive, 2 = Medium, 3 = Safe High
    public int preloaded = 0;

    // 0 = Center High, 1 = Center High Defensive, 2 = Medium, 3 = Safe High
    public int[] cycles = new int[] {0, 0, 0, 0, 0};

    public int waitAfterScore = 200;

    public boolean left = false;
}
