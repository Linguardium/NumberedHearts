package mod.linguardium.numberedhearts;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import terrails.colorfulhearts.heart.HeartPiece;
import terrails.colorfulhearts.heart.HeartType;

import java.util.List;
import java.util.Set;

import static com.mojang.text2speech.Narrator.LOGGER;

@FunctionalInterface
public interface PlayerHeartColorProvider {
    int getColor(PlayerEntity player, int amount, boolean absorption);


    PlayerHeartColorProvider DEFAULT = (player, health, absorption)->{
        if (Config.INSTANCE.changeColorWithStatusEffects()) {
            Set<StatusEffect> statusEffects = player.getActiveStatusEffects().keySet();
            if (statusEffects.contains(StatusEffects.WITHER)) {
                return 0x0F0F0F;
            } else if (statusEffects.contains(StatusEffects.POISON)) {
                return 0x739B00;
            } else if (player.isFrozen()) {
                return 0x3E70E6;
            }
        }
        return absorption?0xFFFF00:-1;
    };
    PlayerHeartColorProvider COLORFUL_HEARTS_PROVIDER = (player, amount, absorption)->{
                List<HeartPiece> pieces =  HeartPiece.getHeartPiecesForType(
                        Config.INSTANCE.changeColorWithStatusEffects()?
                                HeartType.forPlayer(player):
                                HeartType.NORMAL,
                        absorption);
                Integer color=null;
                if (!pieces.isEmpty()) {
                    color = pieces.get(MathHelper.clamp((amount / 20) - 1, 0, pieces.size() - 1)).getColor();
                }
                if (color==null) return absorption?0xFFFF00:-1;
                return color;
    };
    static PlayerHeartColorProvider getColorfulHeartsProvider() {
        if (FabricLoader.getInstance().isModLoaded("colorfulhearts") && Config.INSTANCE.matchColorfulHeartsColor()) {
            LOGGER.info("Found colorful hearts. Setting color providers");
            return COLORFUL_HEARTS_PROVIDER;
        }
        return DEFAULT;
    }

    public static int rgbSmash(int r, int g, int b) {
        return r + g + b;
    }
    public static int rgbSmash(int rgb) {
        return rgbSmash((rgb & 0xFF0000) >> 16, (rgb & 0xFF00 >> 8), rgb & 0xFF);
    }
    public static int getBorderColor(int rgb) {
        return (rgbSmash(rgb) > 0xC0)?0:-1;
    }

}

