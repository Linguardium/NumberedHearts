package mod.linguardium.numberedhearts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static mod.linguardium.numberedhearts.Config.loadConfig;
import static mod.linguardium.numberedhearts.PlayerHeartColorProvider.getBorderColor;
import static mod.linguardium.numberedhearts.PlayerHeartColorProvider.getColorfulHeartsProvider;

public class NumberedHearts implements ModInitializer {

    static final Logger LOGGER = LoggerFactory.getLogger("numberedhearts");
	static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
	private static PlayerHeartColorProvider HeartColorProvider = PlayerHeartColorProvider.DEFAULT;

	static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("numberedhearts.json");
	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			private static final Identifier id = new Identifier("numberedhearts:config");
			@Override
			public Identifier getFabricId() {
				return id;
			}
			@Override
			public void reload(ResourceManager manager) {
				loadConfig();
				HeartColorProvider = getColorfulHeartsProvider();
			}
		});
	}
	public static boolean shouldRenderHearts() {
		return !Config.INSTANCE.hideHearts();
	}


	public static void renderHealth(DrawContext context, int x, int y, PlayerEntity player, int health, int maxHealth, int absorption) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

		MutableText hp = Text.translatable("hud.numberedhearts.hp",health);
		int width = textRenderer.getWidth(hp);
		int startX = (x+40)-(width/2);
		int color = 0;

		if (absorption>0) {
			MutableText absorptionText = Text.translatable("hud.numberedhearts.absorption", absorption);
			startX -= textRenderer.getWidth(absorptionText)/2;
			color = HeartColorProvider.getColor(player, absorption, true);
			textRenderer.drawWithOutline(absorptionText.asOrderedText(), startX + width, y, color, getBorderColor(color), context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), 15728880);
		}

		color = HeartColorProvider.getColor(player,maxHealth,false);
		textRenderer.drawWithOutline(hp.asOrderedText(),startX,y,color,getBorderColor(color),context.getMatrices().peek().getPositionMatrix(),context.getVertexConsumers(),15728880);
		context.draw();
	}
}