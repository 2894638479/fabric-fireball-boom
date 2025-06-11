package name.fireballboom.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExplosiveProjectileEntity.class)
public class ExplosiveProjectileEntityMixin {

    @Unique
    Vec3d getConstSpeed(){
        Entity entity = (ExplosiveProjectileEntity) (Object) this;
        return entity.getVelocity().normalize().multiply(1.5);
    }

    @Unique
    private static boolean isFireball(Object instance){
        return instance instanceof FireballEntity;
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    void resetSpeed(CallbackInfo ci){
        if(!isFireball(this)) return;
        ((ExplosiveProjectileEntity) (Object) this).setVelocity(getConstSpeed());
    }
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ExplosiveProjectileEntity;setPosition(DDD)V")
    )
    void resetPosition(ExplosiveProjectileEntity instance, double x, double y, double z){
        if(!isFireball(instance)) {
            instance.setPosition(x,y,z);
            return;
        }
        Vec3d pos = instance.getPos();
        Vec3d speed = getConstSpeed();
        instance.setVelocity(speed);
        instance.setPosition(pos.add(speed));
    }
}
