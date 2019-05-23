class Globals {
    private static long TEXT_LENGTH;

    static long getTextLength() {
        return TEXT_LENGTH;
    }

    static void setTextLength(long textLength) {
        TEXT_LENGTH = textLength;
    }

    static void resetTextLength() {
        TEXT_LENGTH = 0;
    }

    static void incrementTextLength() {
        TEXT_LENGTH += 1;
    }
}
