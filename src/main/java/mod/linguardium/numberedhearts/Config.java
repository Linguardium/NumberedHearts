package mod.linguardium.numberedhearts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static mod.linguardium.numberedhearts.NumberedHearts.LOGGER;
import static mod.linguardium.numberedhearts.NumberedHearts.CONFIG_PATH;
import static mod.linguardium.numberedhearts.NumberedHearts.GSON;

public record Config(boolean hideHearts, boolean matchColorfulHeartsColor, boolean changeColorWithStatusEffects) {
    public static Config DEFAULT = new Config(false,false,true);
    public static Config INSTANCE = DEFAULT;
    public static final Codec<Config> CODEC = RecordCodecBuilder.create(config->config.group(
            Codec.BOOL.optionalFieldOf("hideHearts",false).forGetter(c->c.hideHearts),
            Codec.BOOL.optionalFieldOf("matchColorfulHeartsColor",true).forGetter(c->c.matchColorfulHeartsColor),
            Codec.BOOL.optionalFieldOf("changeColorWithStatusEffects",true).forGetter(c->c.changeColorWithStatusEffects)
    ).apply(config,Config::new));

    public static void saveConfig() {
        LOGGER.info("Saving current config");
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(Config.INSTANCE));
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Unable to save config: {}",e.getMessage());
        }
    }
    public static void loadConfig() {
        LOGGER.info("Reloading config");
        Config.INSTANCE = Config.DEFAULT;
        try {
            if (Files.exists(CONFIG_PATH)) {
                Config.INSTANCE = Util.getResult(Config.CODEC.parse(JsonOps.INSTANCE, JsonHelper.deserialize(Files.readString(CONFIG_PATH, StandardCharsets.UTF_8))),RuntimeException::new);
            }
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Failed to read config: {}",e.getMessage());
        }
        saveConfig();

    }
}
