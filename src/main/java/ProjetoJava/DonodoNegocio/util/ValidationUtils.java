package ProjetoJava.DonodoNegocio.util;

import ProjetoJava.DonodoNegocio.config.AppConstants;

public final class ValidationUtils {

    private ValidationUtils() {
        // Private constructor
    }

    public static boolean isValidPath(String path) {
        return path != null && path.matches(AppConstants.REGEX_SAFE_PATH);
    }
}