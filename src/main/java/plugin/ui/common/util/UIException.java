package plugin.ui.common.util;

public class UIException extends RuntimeException {

    static final long serialVersionUID = 1L;

    public UIException(String message) {
        super(message);
    }

    public UIException(Throwable cause) {
        super(cause);
    }
}
