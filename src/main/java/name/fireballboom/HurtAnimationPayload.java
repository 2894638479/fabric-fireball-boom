package name.fireballboom;


import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class HurtAnimationPayload implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() { return id; }
    public static CustomPayload.Id<HurtAnimationPayload> id = new CustomPayload.Id<>(
            Identifier.of("fireball-boom","fireball_play_hurt_animation"));
    static void register(){
        PacketCodec<PacketByteBuf, HurtAnimationPayload> CODEC = PacketCodec.unit(new HurtAnimationPayload());
        PayloadTypeRegistry.playS2C().register(id,CODEC);
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof HurtAnimationPayload;
    }
}
