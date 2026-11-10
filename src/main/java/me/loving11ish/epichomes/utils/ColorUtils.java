package me.loving11ish.epichomes.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

public class ColorUtils {

    static public final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

    /**
     * @param text The string of text to apply colour/effects to
     * @return Returns a string of text with colour/effects applied
     */
    public static String translateColorCodes(String text) {
        // Optionally parse MiniMessage format
        if (containsMiniMessageTags(text)) {
            MiniMessage miniMessage = MiniMessage.miniMessage();
            Component component = miniMessage.deserialize(text);
            return LegacyComponentSerializer.legacySection().serialize(component);
        }

        // Fallback to legacy colour code handling with & and hex (#) codes
        String[] texts = text.split(String.format(WITH_DELIMITER, "&"));
        StringBuilder finalText = new StringBuilder();
        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equalsIgnoreCase("&")) {
                i++;
                if (texts[i].charAt(0) == '#') {
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)) + texts[i].substring(7));
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            } else {
                finalText.append(texts[i]);
            }
        }
        return finalText.toString();
    }

    /**
     * Checks if a string contains MiniMessage tags, based on typical tags like red, bold, etc.
     * @param text The text to check
     * @return True if MiniMessage tags are detected
     */
    private static boolean containsMiniMessageTags(String text) {
        // A basic check for some common MiniMessage tags like <colour>, <bold>, etc.
        return text.matches(".*<(red|green|blue|bold|italic|reset|underlined|hover|click|obfuscated).*>");
    }

    /**
     * Strips all chat colours, HEX colours, and MiniMessage colours from the provided string.
     * @param text The string to strip colours from.
     * @return The string without any colour codes.
     */
    public static String stripColorCodes(String text) {
        // Pattern to match legacy colour codes (& and §)
        String legacyColorPattern = "(?i)[&§][0-9A-FK-OR]";
        // Pattern to match HEX colour codes
        String hexColorPattern = "(?i)#[A-F0-9]{6}";
        // Pattern to match MiniMessage tags
        String miniMessagePattern = "<[^>]+>";

        // Combine all patterns
        String combinedPattern = String.format("%s|%s|%s", legacyColorPattern, hexColorPattern, miniMessagePattern);

        // Remove all matches
        return text.replaceAll(combinedPattern, "");
    }
}
