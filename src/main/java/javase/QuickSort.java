package javase;

/**
 * 快速排序：定义第一个数为比较值,一个从前面开始为i,一个从后面开始为j,左边碰到比比较值大的,和右边碰到比比较值小的交换
 */

public class QuickSort {
    public static int[] quickSort(int[] arr, int start, int end) {
        int value = arr[start];
        int i = start;
        int j = end;
        while (i < j) {
            while ((i<j)&&arr[j] > value) {
                j--;
            }
            while ((i<j)&&arr[i] < value) {
                i++;
            }

            if ((i<j)&&arr[j] == arr[i]) {
                i++;
            } else {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        if (i - 1 > start) arr = quickSort(arr, start, i - 1);
        if (j + 1 < end) arr = quickSort(arr, j + 1, end);
        return arr;
    }

    public static void main(String[] args) {
        //int arr[] = new int[]{5,3,7,6,4,1,0,2,9,10,8};
        int arr[] = new int[]{3,3,3,7,9,122344,4656,34,34,4656,5,6,7,8,9,343,57765,23,12321};
        int len = arr.length - 1;
        arr = quickSort(arr, 0, len);
        for (int i : arr) {
            System.out.print(i + "\t");
        }
    }
}
