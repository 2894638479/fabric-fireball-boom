package name.fireballboom.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;


@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Accessor("x")
    public abstract double x();
    @Accessor("y")
    public abstract double y();
    @Accessor("z")
    public abstract double z();

    @Unique
    Vec3 knockBack;
    @Unique
    double horizontalDistance;
    @Unique
    double distance;
    @Unique
    double horizontalKb(){
        if(horizontalDistance > 6) return 0;
        if(distance < 1.5) return 0;
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

    @Redirect(
            method = "explode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )
    void changeExplosionKnockBack(Entity instance, Vec3 vec3){
        Vec3 playerPos = instance.position();
        Vec3 explosionPos = new Vec3(x(),y(),z());
        Vec3 diff = playerPos.add(explosionPos.scale(-1));
        Vec3 origin = instance.getDeltaMovement();
        horizontalDistance = diff.horizontalDistance();
        distance = diff.length();
        double hKb = horizontalKb();
        double yKb = verticalKb();
        if(!instance.onGround()){
            hKb *= horizontalKbJumpingScale();
        }
        double xKb = diff.x / horizontalDistance * hKb;
        double zKb = diff.z / horizontalDistance * hKb;
        Vec3 finalSpeed = new Vec3(xKb + origin.x*5,yKb,zKb + origin.z*5);
        knockBack = finalSpeed.add(origin.scale(-1));
        Vec3 result = origin.add(knockBack);
        instance.setDeltaMovement(result);
    }
    @Redirect(
            method = "explode",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
    )
    <K,V> V changePlayerHitPut(Map<K,V> instance, K k, V v){
        return instance.put(k, (V) knockBack);
    }
}
