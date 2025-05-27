package name.fireballboom.mixin;

import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHurtingProjectile.class)
public class AbstractHurtingProjectileMixin {
    @Unique int ticks = 0;
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractHurtingProjectile;setPos(DDD)V")
    )
    void resetPosition(AbstractHurtingProjectile instance, double x, double y, double z){
        ticks++;
        Vec3 pos = instance.position();
        Vec3 speed = instance.getDeltaMovement().scale(ticks <= 3 ? 4 : 1.5);
        instance.setPos(pos.add(speed));
    }
    @Inject(method = "getInertia", at = @At(value = "HEAD"), cancellable = true)
    void changeFireballSpeed(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(1f);
    }
}
