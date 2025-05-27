package name.fireballboom.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
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


@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow @Final private @Nullable Entity source;

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
    Vec3 knockBack;
    @Unique
    double horizontalDistance;
    @Unique
    double distance;
    @Unique
    double horizontalKb(){
        if(horizontalDistance > 6) return 0;
        if(distance < 1) return 0;
        if(horizontalDistance < 3) return horizontalDistance*0.35;
        return 3*0.35;
    }

    @Unique
    double verticalKb(){
        if(horizontalDistance > 6) return 0;
        return 1.27;
    }
    @Unique
    double horizontalKbJumpingScale(){ return 2; }

    @Inject(
            method = "explode",
            at = @At(value = "HEAD"),
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
                    Vec3 playerPos = instance.position();
                    Vec3 diff = playerPos.add(explosionPos.scale(-1));
                    Vec3 origin = instance.getDeltaMovement();
                    horizontalDistance = diff.horizontalDistance();
                    distance = diff.length();
                    double hKb = horizontalKb();
                    double yKb = verticalKb();
                    if (!instance.onGround()) {
                        hKb *= horizontalKbJumpingScale();
                    }
                    double xKb = diff.x / horizontalDistance * hKb;
                    double zKb = diff.z / horizontalDistance * hKb;
                    Vec3 finalSpeed = new Vec3(xKb + origin.x * 5, yKb, zKb + origin.z * 5);
                    knockBack = finalSpeed.add(origin.scale(-1));
                    Vec3 result = origin.add(knockBack);
                    instance.setDeltaMovement(result);

                    if (instance instanceof Player player && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                        hitPlayers.put(player,knockBack);
                    }
                }
            }
            ci.cancel();
        }
    }
}
