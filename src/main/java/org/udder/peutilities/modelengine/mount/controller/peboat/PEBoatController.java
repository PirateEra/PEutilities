package org.udder.peutilities.modelengine.mount.controller.peboat;

import com.ticxo.modelengine.api.mount.controller.AbstractMountController;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;

public class PEBoatController extends AbstractMountController{
    public PEBoatController() {
    }

    public void updateDriverMovement(MoveController controller, ModeledEntity modelEntity) {
        if (this.input.isSneak()) {
            modelEntity.getMountManager().removeDriver();
            controller.move(0.0F, 0.0F, 0.0F);
        } else {
            controller.move(0.0F, this.input.getFront(), 1.0F);
            if (this.input.getSide() == 0.0F && this.input.getFront() == 0.0F) {
                modelEntity.setState(ModelState.IDLE);
            } else {
                modelEntity.setState(ModelState.WALK);
            }

            if (this.input.isJump() && controller.isOnGround()) {
                controller.jump();
                modelEntity.setState(ModelState.JUMP);
            }

        }
    }

    public void updatePassengerMovement(MoveController controller, ModeledEntity modelEntity) {
        if (this.input.isSneak()) {
            modelEntity.getMountManager().removePassenger(this.entity);
        }
    }
}
