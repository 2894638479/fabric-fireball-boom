package name.fireballboom.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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


@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow @Final private Map<Player, Vec3> hitPlayers;

    @Accessor("x")
    public abstract double x();
    @Accessor("y")
    public abstract double y();
    @Accessor("z")
    public abstract double z();
    @Accessor("level")
    public abstract Level level();
    @Accessor("source")
    public abstract Entity source();

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

    @Inject(
            method = "explode",
            at = @At("HEAD"),
            cancellable = true
    )
    void changeExplosionKnockBack(CallbackInfo ci){
        if(source() instanceof LargeFireball) {
            Vec3 explosionPos = new Vec3(x(), y(), z());
            level().gameEvent(this.source(), GameEvent.EXPLODE, explosionPos);
            float radius = 7f;
            int minX = Mth.floor(this.x() - radius - 1.0);
            int maxX = Mth.floor(this.x() + radius + 1.0);
            int minY = Mth.floor(this.y() - radius - 1.0);
            int maxY = Mth.floor(this.y() + radius + 1.0);
            int minZ = Mth.floor(this.z() - radius - 1.0);
            int maxZ = Mth.floor(this.z() + radius + 1.0);
            List<Entity> list = this.level().getEntities(this.source(), new AABB(minX, minY, minZ, maxX, maxY, maxZ));
            for (int v = 0; v < list.size(); v++) {
                Entity instance = list.get(v);
                if(!instance.ignoreExplosion() && !(instance instanceof LargeFireball)) {
                    Vec3 playerPos = instance.position().add(0, 1, 0);
                    Vec3 diff = playerPos.add(explosionPos.scale(-1));
                    Vec3 originSpeed = instance.getDeltaMovement();
                    horizontalDistance = diff.horizontalDistance();
                    verticalDistance = abs(diff.y);
                    distance = diff.length();
                    if(horizontalDistance > 6) {
                        continue;
                    }
                    double hKb = horizontalKb();
                    double yKb = verticalKb();
                    if (!instance.onGround()) {
                        hKb = jumpingHorizontalKbScale(hKb);
                    }
                    double xKb = 0.0;
                    double zKb = 0.0;
                    if(horizontalDistance != 0.0){
                        xKb = diff.x / horizontalDistance * hKb;
                        zKb = diff.z / horizontalDistance * hKb;
                    }
                    if (instance instanceof Player) {
                        xKb += playerAccelerationScale(abs(originSpeed.x)) * signum(originSpeed.x);
                        zKb += playerAccelerationScale(abs(originSpeed.z)) * signum(originSpeed.z);
                    }
                    Vec3 finalSpeed = new Vec3(xKb,yKb,zKb);
                    Vec3 knockBack = finalSpeed.add(originSpeed.scale(-1));
                    Vec3 result = originSpeed.add(knockBack);
                    instance.setDeltaMovement(result);
                    if (instance instanceof ServerPlayer player && !player.isSpectator()){
                        ServerPlayNetworking.send(player,
                                new ResourceLocation("fireball-boom","fireball_play_hurt_animation"),
                                new FriendlyByteBuf(Unpooled.buffer()));
                    }
                    if (instance instanceof Player player && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                        hitPlayers.put(player,knockBack);
                    }
                }
            }
            ci.cancel();
        }
    }
}
