package name.fireballboom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PacketReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier("fireball-boom","fireball_play_hurt_animation"),
                (MinecraftClient mc, ClientPlayNetworkHandler var2, PacketByteBuf var3, PacketSender var4)->{
                    if (mc.player != null) {
                        mc.player.maxHurtTime = 10;
                        mc.player.hurtTime = 10;
                    }
                });
    }
}
