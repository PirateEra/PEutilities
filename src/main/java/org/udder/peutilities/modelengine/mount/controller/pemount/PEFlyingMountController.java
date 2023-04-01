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
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class PEFlyingMountController extends AbstractMountController{
    private boolean sneaking = false;
    public PEFlyingMountController(){

    }

    public void updateDriverMovement(MoveController controller, ModeledEntity modelEntity) {
        // Prevent the controller from taking damage
        controller.nullifyFallDistance();

        // Used to for example check if we are in water
        Block location = modelEntity.getBase().getLocation().getBlock();
        String material = String.valueOf(location.getType());

        // We set the Y velocity of the model to 0, to prevent it from falling downwards
        // This way it can only descend using sneak
        Vector modelVelocity = controller.getVelocity();
        controller.setVelocity(modelVelocity.getX(), 0.0, modelVelocity.getZ());
        // When the player is trying to dismount, or descend
        if (this.input.isSneak()) {
            // This will be cancelled in the doubleSneakDismount() function
            // Unless it is pressed twice in a row.
            if(!sneaking){
                modelEntity.getMountManager().removeDriver();
            }
            // Add negative velocity to the y of the model, Only when not in water!
            // This is because we do not want flying mounts to swim underwater
            if(!material.equals("WATER")){
                controller.addVelocity(0.0, (double)(-controller.getSpeed()), 0.0);
            }
            sneaking = true;
        }else{
            sneaking = false;
        }

        // When the player is trying to ascend
        if (this.input.isJump()) {
            controller.addVelocity(0.0, (double)(controller.getSpeed()), 0.0);
        }

        // Forward movement
        controller.move(0.0F, this.input.getFront(), 1.0F);

        // Animation state handling
        if(controller.isOnGround()){
            if (this.input.getSide() == 0.0F && this.input.getFront() == 0.0F) {
                modelEntity.setState(ModelState.IDLE);
            } else if (this.input.getFront() != 0.0F) {
                modelEntity.setState(ModelState.WALK);
            }
        }else{
            modelEntity.setState(ModelState.IDLE);
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
            modelEntity.getMountManager().removePassenger(this.entity);
        }
    }
}
