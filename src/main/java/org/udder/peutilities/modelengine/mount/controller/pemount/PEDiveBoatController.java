package org.udder.peutilities.modelengine.mount.controller.pemount;

import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.mount.controller.AbstractMountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class PEDiveBoatController extends AbstractMountController {
    private boolean sneaking = false;
    public PEDiveBoatController(){

    }

    public void updateDriverMovement(MoveController controller, ModeledEntity modelEntity) {
        // Prevent the controller from taking damage
        controller.nullifyFallDistance();
        //Needed to manipulate the models Y velocity, to prevent sinking
        Vector modelVelocity = controller.getVelocity();
        // Used to for example check if we are in water
        Block location = modelEntity.getBase().getLocation().getBlock();
        String material = String.valueOf(location.getRelative(BlockFace.UP).getType());
        if(material.equals("WATER")){
            // Make the dive boat not sink, you can only descend using sneak
            controller.setVelocity(modelVelocity.getX(), 0.0, modelVelocity.getZ());
            // Descend
            if(this.input.isSneak()){
                // Going down is 2x slower than forward
                controller.addVelocity(0.0, -controller.getSpeed()/2, 0.0);
            }
            // If we are inside of water, we allow the user to slightly go upwards (to exit waters for example).
            else if (this.input.isJump()) {
                // Going up is 2x slower than forward
                controller.addVelocity(0.0, controller.getSpeed()/2, 0.0);
            }
        }
        // Needed for dismounting with double tap sneak
        if (this.input.isSneak()) {
            if(!sneaking){
                modelEntity.getMountManager().removeDriver();
            }
            sneaking = true;
        }else{
            sneaking = false;
        }
        // Movement speed multiplier (this multiplies with entity movements speed set in the mythic mob)
        float multiplier = 1.0F;
        if(!material.equals("WATER")){
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
        if(!material.equals("WATER")){
            if (this.input.getSide() == 0.0F && this.input.getFront() == 0.0F && controller.isOnGround()) {
                modelEntity.setState(ModelState.IDLE);
            } else if (this.input.getFront() != 0.0F && controller.isOnGround()) {
                modelEntity.setState(ModelState.WALK);
            }
        }else{
            modelEntity.setState(ModelState.IDLE);
        }
        // Needed, because the event that turns off this mounts gravity gets called once everytime we descend
        ((Entity) modelEntity.getBase().getOriginal()).setGravity(true);
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
