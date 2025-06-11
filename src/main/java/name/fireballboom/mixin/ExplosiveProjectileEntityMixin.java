package name.fireballboom.mixin;

import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExplosiveProjectileEntity.class)
public class ExplosiveProjectileEntityMixin {
    @Shadow public double powerX;
    @Shadow public double powerY;
    @Shadow public double powerZ;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    void resetSpeed(CallbackInfo ci){
        Vec3d speed = new Vec3d(this.powerX,this.powerY,this.powerZ).multiply(15);
        ((ExplosiveProjectileEntity)(Object)this).setVelocity(speed);
    }
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ExplosiveProjectileEntity;setPosition(DDD)V")
    )
    void resetPosition(ExplosiveProjectileEntity instance, double x, double y, double z){
        Vec3d pos = instance.getPos();
        Vec3d speed = new Vec3d(instance.powerX,instance.powerY,instance.powerZ).multiply(15);
        instance.setVelocity(speed);
        instance.setPosition(pos.add(speed));
    }
}
