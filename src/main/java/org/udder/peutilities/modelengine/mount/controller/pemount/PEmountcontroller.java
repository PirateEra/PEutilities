package org.udder.peutilities.modelengine.mount.controller.pemount;

import com.ticxo.modelengine.api.mount.controller.AbstractMountController;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;

public class PEmountcontroller extends AbstractMountController{
    public PEmountcontroller() {

    }

    public void updateDriverMovement(MoveController controller, ModeledEntity modelEntity) {
        if (this.input.isSneak()) {
            modelEntity.getMountManager().removeDriver();
            controller.move(0.0F, 0.0F, 0.0F);
        } else {
            // Used to for example check if we are in water
            Block location = modelEntity.getBase().getLocation().getBlock();
            String material = String.valueOf(location.getType());
            // Movement speed multiplier (this multiplies with entity movements speed set in the mythic mob)
            float multiplier = 1.0F;
            // This implies we move 50% slower in water
            if(material == "WATER"){
                multiplier = 0.5F;
            }

            // General forward and backward movement
            if (this.input.getFront() > 0){
                controller.move(0.0F, this.input.getFront(), 1.0F * multiplier);
            }else if (this.input.getFront() < 0){
                controller.move(0.0F, this.input.getFront(), 0.33F * multiplier);
            }

            // Animation handling, This is only when on land, water is handled using mythic mob skills
            // This is done with mythic mob skills, because the API does not allow setting non default states.
            // I will look for a way to do this within the code to exclude mythic mob skills.
            if(material != "WATER"){
                if (this.input.getSide() == 0.0F && this.input.getFront() == 0.0F && controller.isOnGround()) {
                    modelEntity.setState(ModelState.IDLE);
                } else if (this.input.getFront() != 0.0F && controller.isOnGround()) {
                    modelEntity.setState(ModelState.WALK);
                } else if (!controller.isOnGround()){
                    modelEntity.setState(ModelState.JUMP);
                }
            }

            // Jump logic, we call controller jump on space press whilst on ground
            if (this.input.isJump() && controller.isOnGround()) {
                controller.jump();
            }
            // If we are inside of water, we slowly sink due to gravity, but holding space will make us swim up.
            if (this.input.isJump() && (String.valueOf(location.getType()) == "WATER")) {
                // Look carefully at /5, this implies swimming up is 5x as slow as normal walk speed.
                controller.addVelocity(0.0, controller.getSpeed()/5, 0.0);
            }
        }
    }

    @Override
    public void updateDirection(LookController controller, ModeledEntity model) {
        Location location = this.getEntity().getLocation();
        controller.setPitch(location.getPitch() / 2.0F);
        // Rotational movement
        if (this.input.getSide() < 0.0F){
            controller.setHeadYaw(model.getBodyYaw() + 5);
        } else if (this.input.getSide() > 0.0F) {
            controller.setHeadYaw(model.getBodyYaw() - 5);
        }
    }

    public void updatePassengerMovement(MoveController controller, ModeledEntity modelEntity) {
        if (this.input.isSneak()) {
            modelEntity.getMountManager().removePassenger(this.entity);
        }
    }
}
