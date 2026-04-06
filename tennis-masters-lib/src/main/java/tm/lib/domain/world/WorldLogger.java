package tm.lib.domain.world;

public interface WorldLogger {

    void print(String str);

    void println();

    void println(String string);

    void println(String formatString, Object... args);

    WorldLogger NoopLogger = new WorldLogger() {
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
