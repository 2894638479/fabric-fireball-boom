package name.fireballboom.mixin;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Redirect(
            method = "registerBehavior",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static <K,V> V changeDispenserFireballBehavior(Map instance, K k, V v){
        ItemLike itemLike = (ItemLike) k;
        if(itemLike == Items.FIRE_CHARGE){
            instance.put(itemLike.asItem(), new DefaultDispenseItemBehavior() {
                @Override
                public @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                    Level level = blockSource.getLevel();
                    Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                    Position spawnPos = DispenserBlock.getDispensePosition(blockSource);
                    LargeFireball fireball = new LargeFireball(EntityType.FIREBALL,level);
                    fireball.setOwner(fireball);
                    Vec3i speed = direction.getNormal();
                    fireball.xPower = speed.getX() * 0.1;
                    fireball.yPower = speed.getY() * 0.1;
                    fireball.zPower = speed.getZ() * 0.1;
                    fireball.setPos(spawnPos.x(),spawnPos.y(),spawnPos.z());
                    fireball.setYRot(direction.toYRot());
                    fireball.setXRot(direction.getStepX() * -90);
                    level.addFreshEntity(fireball);
                    itemStack.shrink(1);
                    return itemStack;
                }

                @Override
                protected void playSound(BlockSource blockSource) {
                    blockSource.getLevel().levelEvent(1018, blockSource.getPos(), 0);
                }
            });
        } else {
            instance.put(itemLike.asItem(),v);
        }

        return null;
    }
}
