package org.udder.peutilities.modelengine.mount.controller.pemount;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.mount.controller.AbstractMountController;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class PEBoatController extends AbstractMountController{
    public PEBoatController(){

    }

    public void updateDriverMovement(MoveController controller, ModeledEntity modelEntity) {
        ActiveModel mainModel = (ActiveModel) modelEntity.getModels().values().toArray()[0];
        Vector waterY = mainModel.getBone("water_y").getPosition();
        String material = String.valueOf(new Location(modelEntity.getBase().getWorld(), waterY.getBlockX(),waterY.getBlockY(),waterY.getBlockZ()).getBlock().getType());
        // Prevent the controller from taking fall damage
        controller.nullifyFallDistance();
        if (this.input.isSneak()) {
            modelEntity.getMountManager().removeDriver();
            controller.move(0.0F, 0.0F, 0.0F);
        } else {
            //Needed to manipulate the models Y velocity, to prevent sinking
            Vector modelVelocity = controller.getVelocity();
            // Movement speed multiplier (this multiplies with entity movements speed set in the mythic mob)
            float multiplier = 1.0F;
            if(material.equals("WATER")){
                controller.setVelocity(modelVelocity.getX(), 0.0, modelVelocity.getZ());
                // If we are inside of water, we allow the user to slightly go upwards (to exit waters for example).
                if (this.input.isJump()) {
                    // Look carefully at /5, this implies swimming up is 5x as slow as normal walk speed.
                    controller.addVelocity(0.0, controller.getSpeed()/5, 0.0);
                }
            }else{
                // This implies we move 75% slower on land
                multiplier = 0.25F;
            }

            // General forward and backward movement
            if (this.input.getFront() > 0){
                controller.move(0.0F, this.input.getFront(), 1.0F * multiplier);
            }else if (this.input.getFront() < 0){
                controller.move(0.0F, this.input.getFront(), 0.33F * multiplier);
            }

            // Animation handling
            if (this.input.getSide() == 0.0F && this.input.getFront() == 0.0F && controller.isOnGround()) {
                modelEntity.setState(ModelState.IDLE);
            } else if (this.input.getFront() != 0.0F && controller.isOnGround()) {
                modelEntity.setState(ModelState.WALK);
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
