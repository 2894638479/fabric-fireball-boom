package name.fireballboom.mixin;

import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractHurtingProjectile.class)
public class AbstractHurtingProjectileMixin {
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractHurtingProjectile;setPos(DDD)V")
    )
    void resetPosition(AbstractHurtingProjectile instance, double x, double y, double z){
        Vec3 pos = instance.position();
        Vec3 speed = new Vec3(instance.xPower,instance.yPower,instance.zPower).scale(15);
        instance.setDeltaMovement(speed);
        instance.setPos(pos.add(speed));
    }
}
