package oom;

/**
 * OMM 之 java.lang. StackOverflowError 栈空间溢出，栈管运行，每个方法就是一个栈帧，循环调用方法，会出现这种问题
 */
public class StackOverFlowDemo {
    public static void main(String[] args) {
        stackoverflowError();
    }

    private static void stackoverflowError() {
        stackoverflowError();
    }
}
