package org.udder.peutilities;

import com.ticxo.modelengine.api.animation.state.ModelState;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import com.ticxo.modelengine.api.mount.MountControllerRegistry;
import com.ticxo.modelengine.api.ModelEngineAPI;
import org.udder.peutilities.modelengine.mount.controller.pemount.PEflyingmountcontroller;
import org.udder.peutilities.modelengine.mount.controller.pemount.PEmountcontroller;

public final class PEutilities extends JavaPlugin {
    private MountControllerRegistry controllerRegistry;
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getLogger().info("PEutilities loaded");
        addpemounts();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void addpemounts() {
        ModelEngineAPI.getControllerRegistry().register("pemount", PEmountcontroller::new);
        ModelEngineAPI.getControllerRegistry().register("peflymount", PEflyingmountcontroller::new);
    }
}
