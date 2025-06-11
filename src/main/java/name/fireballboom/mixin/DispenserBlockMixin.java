package name.fireballboom.mixin;

import name.fireballboom.FireballBoom;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Redirect(
            method = "registerProjectileBehavior",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static <K,V> V changeDispenserFireballBehavior(Map instance, K k, V v){
        ItemConvertible itemLike = (ItemConvertible) k;
        if(itemLike == Items.FIRE_CHARGE){
            instance.put(itemLike.asItem(), new ItemDispenserBehavior() {
                @Override
                public @NotNull ItemStack dispenseSilently(BlockPointer blockPointer, ItemStack itemStack) {
                    FireballBoom.summonFireballFromDispenser(blockPointer, itemStack);
                    return itemStack;
                }
                @Override
                protected void playSound(BlockPointer blockPointer) {
                    blockPointer.world().syncWorldEvent(WorldEvents.BLAZE_SHOOTS, blockPointer.pos(), 0);
                }
            });
        } else {
            instance.put(itemLike.asItem(),v);
        }
        return null;
    }
}
