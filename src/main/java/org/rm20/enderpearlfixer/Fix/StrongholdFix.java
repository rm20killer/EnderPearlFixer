package org.rm20.enderpearlfixer.Fix;

import com.google.gson.Gson;
import org.rm20.enderpearlfixer.Fix.StrongholdFix.OldStrongholdConfig.OldStrongholdWorld;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.rm20.enderpearlfixer.utils.IOUtils;
import java.util.Random;
import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static java.util.Comparator.comparing;

@NoArgsConstructor
public class StrongholdFix {
    private static OldStrongholdConfig config;
    public void onStart() {
        try {
            config = new Gson().fromJson(Files.readString(getFile().toPath()), OldStrongholdConfig.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static File getFile() {
        return IOUtils.getPluginFile(getFileName());
    }

    @NotNull
    private static String getFileName() {
        return "old-strongholds.json";
    }

    @Data
    public static class OldStrongholdConfig {
        private List<OldStrongholdWorld> worlds;

        @Data
        public static class OldStrongholdWorld {
            private long seed;
            private List<Vector2d> strongholds;

            @Data
            public static class Vector2d {
                private int x;
                private int z;

                public Location asLocation(World world) {
                    return new Location(world, x, 0, z);
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();

        //check if item is null here

        if (event.getItem().getType() != Material.ENDER_EYE)
            return;

//        Block block = event.getClickedBlock();
//        if (isNotNullOrAir(block) && block.getType() == Material.END_PORTAL_FRAME)
//            return;

        OldStrongholdWorld handledWorld = config.getWorlds().stream()
                .filter(strongholdWorld -> strongholdWorld.getSeed() == world.getSeed())
                .findFirst()
                .orElse(null);

        if (handledWorld == null)
            return;

        event.setCancelled(true);

        final List<Location> reachable = handledWorld.getStrongholds().stream()
                .map(vector2d -> vector2d.asLocation(world))
                .filter(location -> world.getWorldBorder().isInside(location))
                .toList();


        final Location nearest = Collections.min(reachable, comparing(stronghold -> distance(player.getLocation(), stronghold)));

        if (nearest == null) {
            player.sendMessage("&cNo stronghold could be found");
            return;
        }

        world.spawn(player.getLocation(), EnderSignal.class, enderEye -> {
            enderEye.setTargetLocation(nearest);
            enderEye.setDropItem(chanceOf(80));
        });
    }

    private double distance(Location from, Location to) {
        return from.distanceSquared(to);
    }

    public static boolean chanceOf(double chance) {
        if(chance <= 0.0)
            return false;
        //get random number between 0 and 100
        Random random = new Random();
        double randomDouble = random.nextDouble() * 100;
        return randomDouble <= chance;
    }
}