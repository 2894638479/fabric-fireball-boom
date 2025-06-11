package name.fireballboom.mixin;

import io.netty.buffer.Unpooled;
import name.fireballboom.HurtAnimationPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static net.minecraft.util.math.MathHelper.floor;


@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow @Final private Map<PlayerEntity, Vec3d> affectedPlayers;

    @Accessor("x")
    public abstract double x();
    @Accessor("y")
    public abstract double y();
    @Accessor("z")
    public abstract double z();
    @Accessor("world")
    public abstract World world();
    @Accessor("entity")
    public abstract Entity entity();

    @Unique
    double horizontalDistance;
    @Unique
    double verticalDistance;
    @Unique
    double distance;
    @Unique
    double horizontalKb(){
        if(horizontalDistance > 6) return 0;
        if(verticalDistance < 0.7 && horizontalDistance < 1) return 0;
        if(horizontalDistance < 3) return horizontalDistance*0.35;
        return 3*0.35;
    }

    @Unique
    double verticalKb(){
        if(horizontalDistance > 6) return 0;
        return 1.27;
    }
    @Unique
    double jumpingHorizontalKbScale(double originSpeed){
        return 2 * originSpeed;
    }

    @Unique
    double playerAccelerationScale(double originSpeed){
        if(originSpeed < 0.2) return 10 * originSpeed;
        if(originSpeed < 3.6) return  1.8 + originSpeed;
        return 1.5 * originSpeed;
    }
    @Unique
    Explosion asExplosion(){
        return (Explosion) (Object) this;
    }

    @Inject(
            method = "collectBlocksAndDamageEntities",
            at = @At("HEAD"),
            cancellable = true
    )
    void changeExplosionKnockBack(CallbackInfo ci){
        if(!(entity() instanceof FireballEntity)) return;
        Vec3d explosionPos = new Vec3d(x(), y(), z());
        world().emitGameEvent(this.entity(), GameEvent.EXPLODE, explosionPos);
        float radius = 7f;
        int minX = floor(this.x() - radius - 1.0);
        int maxX = floor(this.x() + radius + 1.0);
        int minY = floor(this.y() - radius - 1.0);
        int maxY = floor(this.y() + radius + 1.0);
        int minZ = floor(this.z() - radius - 1.0);
        int maxZ = floor(this.z() + radius + 1.0);
        List<Entity> list = this.world().getOtherEntities(this.entity(), new Box(minX, minY, minZ, maxX, maxY, maxZ));
        for (int v = 0; v < list.size(); v++) {
            Entity instance = list.get(v);
            if(!instance.isImmuneToExplosion(asExplosion()) && !(instance instanceof FireballEntity)) {
                Vec3d playerPos = instance.getPos().add(0, 1, 0);
                Vec3d diff = playerPos.add(explosionPos.multiply(-1));
                Vec3d originSpeed = instance.getVelocity();
                horizontalDistance = diff.horizontalLength();
                verticalDistance = abs(diff.y);
                distance = diff.length();
                if(horizontalDistance > 6) {
                    continue;
                }
                double hKb = horizontalKb();
                double yKb = verticalKb();
                if (!instance.isOnGround()) {
                    hKb = jumpingHorizontalKbScale(hKb);
                }
                double xKb = 0.0;
                double zKb = 0.0;
                if(horizontalDistance != 0.0){
                    xKb = diff.x / horizontalDistance * hKb;
                    zKb = diff.z / horizontalDistance * hKb;
                }
                if (instance instanceof PlayerEntity) {
                    xKb += playerAccelerationScale(abs(originSpeed.x)) * signum(originSpeed.x);
                    zKb += playerAccelerationScale(abs(originSpeed.z)) * signum(originSpeed.z);
                }
                Vec3d finalSpeed = new Vec3d(xKb,yKb,zKb);
                Vec3d knockBack = finalSpeed.add(originSpeed.multiply(-1));
                Vec3d result = originSpeed.add(knockBack);
                instance.setVelocity(result);
                if (instance instanceof ServerPlayerEntity player && !player.isSpectator()){
                    ServerPlayNetworking.send(player, new HurtAnimationPayload());
                }
                if (instance instanceof PlayerEntity player && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                    affectedPlayers.put(player,knockBack);
                }
            }
        }
        ci.cancel();
    }
}
