package edu.nchu.mall.services.product;

import java.util.Arrays;

public class MainTest {
    public static void main(String[] args) {
        int[][] grid = new int[][]{
                {1, 1, 0, 0, 0},
                {1, 1, 2, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 0, 1, 1}
        };

        Integer cnt = Arrays.stream(grid).map(arr -> Arrays.stream(arr).filter(n -> n == 2).count()).reduce(0L, Long::sum).intValue();
    }
}
