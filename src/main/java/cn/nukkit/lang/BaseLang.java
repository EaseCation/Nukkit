package cn.nukkit.lang;

import cn.nukkit.Server;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class BaseLang {
    public static final String FALLBACK_LANGUAGE = "eng";

    protected static final Pattern I18N_PATTERN = Pattern.compile("%([a-zA-Z0-9._\\-:]+)");

    protected final String langName;

    protected final Map<String, String> lang = new Object2ObjectOpenHashMap<>();

    public BaseLang(String lang) {
        this(lang, null);
    }

    public BaseLang(String lang, String path) {
        this.langName = lang.toLowerCase();

        if (path == null) {
            this.loadLang(this.getClass().getClassLoader().getResourceAsStream("texts/en_US.lang"));
            this.loadLang(this.getClass().getClassLoader().getResourceAsStream("vanilla_texts/en_US.lang"));
            return;
        }

        this.loadLang(this.getClass().getClassLoader().getResourceAsStream(path));
    }

    public Map<String, String> getLangMap() {
        return lang;
    }

    public String getName() {
//        return this.get("nukkit.language.name");
        return "English (United States)";
    }

    public String getLang() {
        return langName;
    }

    protected void loadLang(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int equals = line.indexOf('=');
                if (equals == -1) {
                    continue;
                }
                int comment = line.indexOf('#');
                boolean hasComment = comment != -1;
                if (hasComment && comment < equals) {
                    continue;
                }
                this.lang.put(line.substring(0, equals), trimTab(line.substring(equals + 1, !hasComment ? line.length() : comment)));
            }
        } catch (IOException e) {
            Server.getInstance().getLogger().error("Unable to load localization", e);
        }
    }

    protected static String trimTab(String string) {
        int start = 0;
        int length = string.length();
        while (start < length && string.charAt(start) == '\t') {
            start++;
        }
        while (start < length && string.charAt(length - 1) == '\t') {
            length--;
        }
        return start > 0 || length < string.length() ? string.substring(start, length) : string;
    }

    public String translate(TextContainer c) {
        if (c instanceof TranslationContainer) {
            return this.translate(c.getText(), ((TranslationContainer) c).getParameters());
        }
        return this.translate(c.getText());
    }

    public String translate(String string, Object... objects) {
        if (string.isEmpty()) {
            return string;
        }

        String i18n;
        boolean percentBreak = false;
        Matcher matcher = I18N_PATTERN.matcher(string);

        if (matcher.find()) {
            i18n = matcher.group(1);
            percentBreak = true;
        } else {
            i18n = string;
        }

        String l10n = this.lang.get(i18n);
        if (l10n == null) {
            return string;
        }

        if (percentBreak) {
            i18n = "%" + i18n;
        }

        if (objects == null || objects.length == 0) {
            return string.replaceFirst(i18n, l10n);
        }

        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object instanceof LiteralContainer) {
                objects[i] = object.toString();
                continue;
            }
            Matcher argMatcher = I18N_PATTERN.matcher(String.valueOf(object));
            if (!argMatcher.matches()) {
                continue;
            }
            String arg = this.lang.get(argMatcher.group(1));
            if (arg == null) {
                continue;
            }
            objects[i] = arg;
        }

        try {
            return string.replaceFirst(i18n, String.format(l10n, objects));
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.throwing(e);
            }
            return string;
        }
    }

    @Nullable
    public String translateOnly(String prefix, String string, Object... objects) {
        if (string.isEmpty()) {
            return string;
        }

        String i18n;
        boolean percentBreak = false;
        Matcher matcher = I18N_PATTERN.matcher(string);

        if (matcher.find()) {
            i18n = matcher.group(1);
            percentBreak = true;
        } else {
            i18n = string;
        }

        if (!i18n.startsWith(prefix)) {
            return null;
        }

        String l10n = this.lang.get(i18n);
        if (l10n == null) {
            return null;
        }

        if (percentBreak) {
            i18n = "%" + i18n;
        }

        if (objects == null || objects.length == 0) {
            return string.replaceFirst(i18n, l10n);
        }

        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object instanceof LiteralContainer) {
                objects[i] = object.toString();
                continue;
            }
            Matcher argMatcher = I18N_PATTERN.matcher(String.valueOf(object));
            if (!argMatcher.matches()) {
                continue;
            }
            String match = argMatcher.group(1);
            if (!match.startsWith(prefix)) {
                continue;
            }
            String arg = this.lang.get(match);
            if (arg == null) {
                continue;
            }
            objects[i] = arg;
        }

        try {
            return string.replaceFirst(i18n, String.format(l10n, objects));
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.throwing(e);
            }
            return null;
        }
    }

    public String get(String id) {
        String l10n = this.lang.get(id);
        if (l10n != null) {
            return l10n;
        }
        return id;
    }
}
