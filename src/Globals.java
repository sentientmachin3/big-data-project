class Globals {
    private static Globals instance = new Globals();
    private static long TEXT_LENGTH;

    private Globals() {
        TEXT_LENGTH = 0;
    }

    public static Globals getInstance() {
        return instance;
    }

    long getTextLength() {
        return TEXT_LENGTH;
    }


    void incrementTextLength() {
        TEXT_LENGTH++;
    }
}
