package util;

import io.quarkus.qute.TemplateExtension;

/**
 * Add your custom Qute extension methods here.
 */
@TemplateExtension
public class TemplateExtensions {
    /**
     * This registers the String.capitalise extension method
     */
    public static String capitalise(String string) {
        StringBuilder sb = new StringBuilder();
        for (String part : string.split("\\s+")) {
            if(!sb.isEmpty()) {
                sb.append(" ");
            }
            if(!part.isEmpty()) {
            sb.append(part.substring(0, 1).toUpperCase());
            sb.append(part.substring(1));
            }
        }
        return sb.toString();
    }
}
