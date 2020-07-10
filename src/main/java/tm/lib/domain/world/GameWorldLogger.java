package tm.lib.domain.world;

public interface GameWorldLogger {

    void print(String str);

    void println();

    void println(String string);

    void println(String formatString, Object... args);

    GameWorldLogger NoopLogger = new GameWorldLogger() {
        @Override
        public void print(String str) {
        }

        @Override
        public void println() {
        }

        @Override
        public void println(String string) {
        }

        @Override
        public void println(String formatString, Object... args) {
        }
    };
}
