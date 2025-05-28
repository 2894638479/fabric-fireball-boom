package name.fireballboom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class PacketReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                new ResourceLocation("fireball-boom","fireball_play_hurt_animation"),
                (Minecraft mc, ClientPacketListener var2, FriendlyByteBuf buf, PacketSender var4)->{
                    if (mc.player != null) {
                        mc.player.hurtDuration = 10;
                        mc.player.hurtTime = 10;
                    }
                });
    }
}
