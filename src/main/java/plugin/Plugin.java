package plugin;

public interface Plugin {
    default void doBeforeFrameInit() {
    }

    default void doAfterFrameInit() {
    }

    default void doAfterReadingLang() {
    }

    default void doAfterLoading() {
    }

    default void checkUpdate() {
    }

}
