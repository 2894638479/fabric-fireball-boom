package name.fireballboom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

public class PacketReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                HurtAnimationPayload.id,
                (HurtAnimationPayload payload, ClientPlayNetworking.Context ctx)->{
                    PlayerEntity player = ctx.client().player;
                    if (player != null) {
                        player.maxHurtTime = 10;
                        player.hurtTime = 10;
                    }
                });
    }
}
