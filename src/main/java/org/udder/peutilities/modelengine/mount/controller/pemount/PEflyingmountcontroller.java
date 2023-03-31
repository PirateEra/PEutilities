package org.udder.peutilities.modelengine.mount.controller.pemount;

import com.ticxo.modelengine.api.mount.controller.AbstractMountController;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import org.bukkit.Location;
import com.ticxo.modelengine.api.entity.BaseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class PEflyingmountcontroller extends AbstractMountController{
    public PEflyingmountcontroller(){

    }

    public void updateDriverMovement(MoveController controller, ModeledEntity modelEntity) {
        // Prevent the controller from taking damage
        controller.nullifyFallDistance();

        // We set the Y velocity of the model to 0, to prevent it from falling downwards
        // This way it can only descent using sneak
        Vector modelVelocity = controller.getVelocity();
        controller.setVelocity(modelVelocity.getX(), 0.0, modelVelocity.getZ());

        // When the player is trying to dismount, or descend
        if (this.input.isSneak()) {
            if (controller.isOnGround()) {
                modelEntity.getMountManager().removeDriver();
                controller.move(0.0F, 0.0F, 0.0F);
                return;
            }
            // Add negative velocity to the y of the model
            controller.addVelocity(0.0, (double)(-controller.getSpeed()), 0.0);
        }

        // When the player is trying to ascend
        if (this.input.isJump()) {
            controller.addVelocity(0.0, (double)(controller.getSpeed()), 0.0);
        }

        // Forward movement
        controller.move(0.0F, this.input.getFront(), 1.0F);

        // Animation state handling
        if (this.input.getSide() == 0.0F && this.input.getFront() == 0.0F) {
            modelEntity.setState(ModelState.IDLE);
        } else if (this.input.getFront() != 0.0F) {
            modelEntity.setState(ModelState.WALK);
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
        if (this.input.isSneak() && modelEntity.getBase().isOnGround()) {
            modelEntity.getMountManager().removePassengers(new Entity[]{this.entity});
        }
    }
}
