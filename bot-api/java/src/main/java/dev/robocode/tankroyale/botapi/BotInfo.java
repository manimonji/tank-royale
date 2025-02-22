package dev.robocode.tankroyale.botapi;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.neovisionaries.i18n.CountryCode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Bot info contains the properties of a bot.
 *
 * <script src="../../../../../prism.js"></script>
 */
@SuppressWarnings("unused")
public final class BotInfo {

    private final String name; // required
    private final String version; // required
    private final List<String> authors; // required
    private final String description; // optional
    private final String homepage; // optional
    private final List<String> countryCodes; // optional
    private final Set<String> gameTypes; // required
    private final String platform; // optional
    private final String programmingLang; // optional

    /**
     * Initializes a new instance of the BotInfo class.
     *
     * @param name            is the name of the bot (required).
     * @param version         is the version of the bot (required).
     * @param authors         is the author(s) of the bot (required).
     * @param description     is a short description of the bot (optional).
     * @param homepage        is the URL to a web page for the bot (optional).
     * @param countryCodes    is the country code(s) for the bot (optional).
     * @param gameTypes       is the game types that this bot can handle (required).
     * @param platform        is the platform used for running the bot (optional).
     * @param programmingLang is the programming language used for developing the bot (optional).
     */
    public BotInfo(
            final String name,
            final String version,
            final List<String> authors,
            final String description,
            final String homepage,
            final List<String> countryCodes,
            final Collection<String> gameTypes,
            final String platform,
            final String programmingLang) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null, empty or blank");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be null, empty or blank");
        }
        if (isNullOrEmptyOrContainsBlanks(authors)) {
            throw new IllegalArgumentException("Authors cannot be null or empty or contain blanks");
        }
        List<String> authors2 = new ArrayList<>();
        authors.removeIf(String::isBlank);
        authors.forEach(author -> authors2.add(author.trim()));

        if (isNullOrEmptyOrContainsBlanks(gameTypes)) {
            throw new IllegalArgumentException("Game types cannot be null or empty or contain blanks");
        }
        Set<String> gameTypes2 = new HashSet<>();
        gameTypes.removeIf(String::isBlank);
        gameTypes.forEach(gameType -> gameTypes2.add(gameType.trim()));

        List<CountryCode> countryCodes2 = new ArrayList<>();
        if (countryCodes != null) {
            countryCodes.removeIf(String::isBlank);
            countryCodes.forEach(
                    code -> {
                        var cc = CountryCode.getByCodeIgnoreCase(code.trim());
                        if (cc != null && !countryCodes2.contains(cc)) {
                            countryCodes2.add(cc);
                        }
                    });
        }
        if (countryCodes2.isEmpty()) {
            var cc = CountryCode.getByLocale(Locale.getDefault());
            if (cc != null && !countryCodes2.contains(cc)) {
                countryCodes2.add(cc);
            }
        }
        List<String> countryCodes3 = new ArrayList<>();
        countryCodes2.forEach(cc -> countryCodes3.add(cc.getAlpha2()));

        String platform2 = platform;
        if (platform2 == null || platform2.trim().length() == 0) {
            platform2 = "Java Runtime Environment (JRE) " + System.getProperty("java.version");
        }

        this.name = name;
        this.version = version;
        this.authors = authors2;
        this.description = description;
        this.homepage = homepage;
        this.countryCodes = countryCodes3;
        this.gameTypes = gameTypes2;
        this.platform = platform2;
        this.programmingLang = programmingLang;
    }

    /**
     * Reads the bot info from a resource file, e.g. when the file is located in a jar file or resource path in IDE.<br>
     * The file is assumed to be in JSON format.<br>
     * <br>
     * See the {@link #fromInputStream(InputStream)} to see the required JSON format for the file.
     *
     * @param filename is the filename of the file containing bot properties.
     * @return A BotInfo instance containing the bot properties read from the file.
     * @throws BotException if the resource file could not be read, or if some field read from the file is invalid.
     * @see #fromFile(String)
     * @see #fromInputStream(InputStream)
     */
    public static BotInfo fromResourceFile(String filename) {
        try (InputStream is = BotInfo.class.getResourceAsStream(filename)) {
            if (is == null) {
                throw new BotException("Could not read the resource file: " + filename);
            }
            return fromInputStream(is);
        } catch (IOException ioe) {
            throw new BotException("Could not read the resource file: " + filename, ioe);
        }
    }

    /**
     * Reads the bot info from a local file on a file system.<br>
     * The file is assumed to be in JSON format.<br>
     * <br>
     * See the {@link #fromInputStream(InputStream)} to see the required JSON format for the file.
     *
     * @param filename is the filename of the file containing bot properties.
     * @return A BotInfo instance containing the bot properties read from the file.
     * @throws BotException if the file could not be read, or if some field read from the file is invalid.
     * @see #fromResourceFile(String)
     * @see #fromInputStream(InputStream)
     */
    public static BotInfo fromFile(String filename) {
        try (InputStream is = new FileInputStream(filename)) {
            return fromInputStream(is);
        } catch (IOException ioe) {
            throw new BotException("Could not read the file: " + filename, ioe);
        }
    }

    /**
     * Reads the bot info from an input stream.<br>
     * The file is assumed to be in JSON format.<br>
     * <br>
     * Example file in JSON format:<br>
     *
     * <pre><code class="language-java">
     * {
     *   name: "MyBot",
     *   version: "1.0",
     *   authors: "John Doe",
     *   description: "A short description",
     *   homepage: "http://somewhere.net/MyBot",
     *   countryCodes: "us",
     *   gameTypes: "classic, melee, 1v1",
     *   platform: "Java 11",
     *   programmingLang: "Java 11"
     * }
     * </code></pre>
     * Note that these fields are required:
     * <ul>
     *     <li>name</li>
     *     <li>version</li>
     *     <li>authors</li>
     *     <li>gameTypes</li>
     * </ul>
     * And these value can take multiple values separated by a comma:
     * <ul>
     *     <li>authors, e.g. "John Doe, Jane Doe"</li>
     *     <li>countryCodes, e.g. "se, no, dk"</li>
     *     <li>gameTypes, e.g. "classic, melee, 1v1"</li>
     * </ul>
     *
     * @param inputStream is the input stream providing the bot properties.
     * @return A BotInfo instance containing the bot properties read from the stream.
     * @throws BotException if some fields read from the stream is invalid.
     * @see #fromFile(String)
     * @see #fromResourceFile(String)
     */
    public static BotInfo fromInputStream(InputStream inputStream) {
        var gson = new Gson();
        var reader = new JsonReader(new InputStreamReader(inputStream));
        JsonProperties data = gson.fromJson(reader, JsonProperties.class);

        if (data.name == null || data.name.isBlank()) {
            throw new BotException("The JSON field 'name' is missing or blank");
        }
        if (data.version == null || data.version.isBlank()) {
            throw new BotException("The JSON field 'version' is missing or blank");
        }
        if (data.authors == null || data.authors.isBlank()) {
            throw new BotException("The JSON field 'authors' is missing or blank");
        }
        if (data.gameTypes == null || data.gameTypes.isBlank()) {
            throw new BotException("The JSON field 'gameTypes' is missing or blank");
        }
        String countryCodes = data.countryCodes;
        if (countryCodes == null) {
            countryCodes = "";
        }
        return new BotInfo(
                data.name,
                data.version,
                Arrays.asList(data.authors.split("\\s*,\\s*")),
                data.description,
                data.homepage,
                Arrays.asList(countryCodes.split("\\s*,\\s*")),
                new HashSet<>(Arrays.asList(data.gameTypes.split("\\s*,\\s*"))),
                data.platform,
                data.programmingLang);
    }

    private static boolean isNullOrEmptyOrContainsBlanks(Collection<String> collection) {
        return (collection == null
                || collection.isEmpty()
                || collection.stream().allMatch(String::isBlank));
    }

    /**
     * Returns the name, e.g., "MyBot". This field must always be provided with the bot info.
     *
     * @return The name of the bot.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the version, e.g., "1.0". This field must always be provided with the bot info.
     *
     * @return The version of the bot.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the list of authors of the bot, e.g., "John Doe (johndoe@somewhere.io)". At least one
     * author must be provided.
     *
     * @return The author(s) of the bot.
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Returns a short description of the bot, preferably a one-liner. This field is optional.
     *
     * @return a short description of the bot.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the URL of a web page for the bot. This field is optional.
     *
     * @return The URL of a web page for the bot.
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * Returns a list of country code(s) defined by ISO 3166-1 alpha-2, e.g. "us":
     * https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2. This field is optional. If no country codes
     * are provided, the locale of the system is being used instead.
     *
     * @return The country code(s) for the bot.
     */
    public List<String> getCountryCodes() {
        return countryCodes;
    }

    /**
     * Returns the game type(s) accepted by the bot, e.g., "classic", "melee", "1v1". At least one
     * game type must be provided to indicate the type(s) of games that this bot can participate in.
     * The game types define which game types the bot can participate in. See {@link GameType} for
     * using predefined game type.
     *
     * @return The game type(s) that this bot can handle.
     */
    public Set<String> getGameTypes() {
        return gameTypes;
    }

    /**
     * Returns the platform used for running the bot, e.g., "Java Runtime Environment (JRE) 11" or
     * ".Net 5.0". This field is optional.
     *
     * @return The platform used for running the bot.
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Returns the programming language used for developing the bot, e.g., "Java 8" or "C# 8.0". This
     * field is optional.
     *
     * @return The programming language used for developing the bot.
     */
    public String getProgrammingLang() {
        return programmingLang;
    }

    private static class JsonProperties {
        String name;
        String version;
        String authors;
        String description;
        String homepage;
        String countryCodes;
        String gameTypes;
        String platform;
        String programmingLang;
    }
}
