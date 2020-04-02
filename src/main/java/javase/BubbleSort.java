package javase;

public class BubbleSort {
    public static void main(String[] args) {
        int[] arr = new int[]{3,3,3,7,9,122344,4656,34,34,4656,5,6,7,8,9,343,57765,23,12321};
        for (int i = 0; i <arr.length-1  ; i++) {
            for (int j = 0; j <arr.length-1-i ; j++) {
                if (arr[j]>arr[j+1]){
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }

            }
        }
        for (int i : arr) {
            System.out.print(i + "\t");
        }
    }

}
