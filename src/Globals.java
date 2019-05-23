public class Globals {
    private static long TEXT_LENGTH;

    public static long getTextLength() {
        return TEXT_LENGTH;
    }

    public static void setTextLength(long textLength) {
        TEXT_LENGTH = textLength;
    }

    public static void incrementTextLength(int value) {
        TEXT_LENGTH += value;
    }
}
