package name.fireballboom.mixin;

import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHurtingProjectile.class)
public class AbstractHurtingProjectileMixin {
    @Shadow public double xPower;

    @Shadow public double yPower;

    @Shadow public double zPower;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    void resetSpeed(CallbackInfo ci){
        Vec3 speed = new Vec3(this.xPower,this.yPower,this.zPower).scale(15);
        ((AbstractHurtingProjectile)(Object)this).setDeltaMovement(speed);
    }
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
