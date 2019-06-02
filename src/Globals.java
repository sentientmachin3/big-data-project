class Globals {

    public Globals() {
    }

    private static long TEXT_LENGTH = 0;

    long getTextLength() {
        return TEXT_LENGTH;
    }


    void incrementTextLength() {
        TEXT_LENGTH += 1;
    }
}
