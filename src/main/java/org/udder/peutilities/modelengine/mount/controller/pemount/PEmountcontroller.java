package org.udder.peutilities.modelengine.mount.controller.pemount;

import com.ticxo.modelengine.api.mount.controller.AbstractMountController;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PEmountcontroller extends AbstractMountController{
    public PEmountcontroller() {

    }

    public void updateDriverMovement(MoveController controller, ModeledEntity modelEntity) {
        if (this.input.isSneak()) {
            modelEntity.getMountManager().removeDriver();
            controller.move(0.0F, 0.0F, 0.0F);
        } else {
            // General forward and backward movement
            controller.move(0.0F, this.input.getFront(), 1.0F);

            // Animation handling
            if (this.input.getSide() == 0.0F && this.input.getFront() == 0.0F) {
                modelEntity.setState(ModelState.IDLE);
            } else if (this.input.getFront() != 0.0F) {
                modelEntity.setState(ModelState.WALK);
            }

            // Jump logic, we call controller jump on space press whilst on ground
            if (this.input.isJump() && controller.isOnGround()) {
                controller.jump();
                modelEntity.setState(ModelState.JUMP);
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
