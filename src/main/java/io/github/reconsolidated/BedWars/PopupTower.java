package io.github.reconsolidated.BedWars;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.util.Vector;

public class PopupTower {


    public static void build(Location center, Vector direction, Material material) {
        craftNormalize(direction);
        buildPopupTower(center, direction, material);
    }

    private static void craftNormalize(Vector vec){
        double max_val = Math.max(vec.getX()*vec.getX(), vec.getZ()*vec.getZ());
        if (max_val == vec.getX()*vec.getX()){
            if (vec.getX() < 0)
                vec.setX(-1);
            else
                vec.setX(1);
            vec.setY(0);
            vec.setZ(0);
        }
        if (max_val == vec.getZ()*vec.getZ()){
            if (vec.getZ() < 0)
                vec.setZ(-1);
            else
                vec.setZ(1);
            vec.setY(0);
            vec.setX(0);
        }
    }

    private static void setTypeIfCanBuild(Location location, Material type) {
        if (!BedWars.guard.isProtected(location) && location.getBlock().getType().equals(Material.AIR)) {
            location.getBlock().setType(type);
        }
    }

    private static void buildPopupTower(Location center, Vector direction, Material material){
        Vector forward = direction.clone();
        Vector left = rotateY(direction.clone());
        //Vector left = direction.clone().rotateAroundY(Math.PI /2);
        left.setX((int)left.getX());
        left.setY((int)left.getY());
        left.setZ((int)left.getZ());
        Location pos = center.clone();

        pos.add(forward);
        pos.add(forward);
        for (int i = 0; i<4; i++){

            setTypeIfCanBuild(pos, material);
            pos.add(left);
            setTypeIfCanBuild(pos, material);
            pos.subtract(forward);
            pos.add(left);
            setTypeIfCanBuild(pos, material);
            pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
            pos.subtract(forward);
            pos.subtract(left);
            setTypeIfCanBuild(pos, material);
            pos.subtract(left);
            if (i>1){
                setTypeIfCanBuild(pos, material);
            }
            pos.subtract(left);
            setTypeIfCanBuild(pos, material);
            pos.subtract(left);
            pos.add(forward);
            setTypeIfCanBuild(pos, material);
            pos.add(forward);
            setTypeIfCanBuild(pos, material);
            pos.add(forward);
            pos.add(left);
            setTypeIfCanBuild(pos, material);

            pos.add(left);
            pos.add(new Vector(0, 1, 0));
        }

        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);

        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);

        pos.subtract(forward);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
        pos.add(forward);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
        pos.subtract(forward);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);

        pos.add(new Vector(0, 1, 0));

        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);

        pos.add(new Vector(0, 1, 0));

            setTypeIfCanBuild(pos, material);
        pos.add(left);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
        pos.add(left);
            setTypeIfCanBuild(pos, material);
        pos.add(forward);
        pos.add(forward);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
        pos.add(forward);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(left);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
        pos.subtract(left);
            setTypeIfCanBuild(pos, material);
        pos.subtract(forward);
        pos.subtract(forward);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);
        pos.add(left);
        pos.subtract(forward);
            setTypeIfCanBuild(pos, material);

        pos = center.clone();
        pos.add(forward);
        for (int i = 0; i<5; i++){
            if (!BedWars.guard.isProtected(pos)) {
                center.getWorld().getBlockAt(pos).setType(Material.LADDER);
                Ladder ladder = (Ladder) center.getWorld().getBlockAt(pos).getBlockData();
                if (forward.getX() == -1){
                    ladder.setFacing(BlockFace.EAST);
                }
                if (forward.getX() == 1){
                    ladder.setFacing(BlockFace.WEST);
                }
                if (forward.getZ() == -1){
                    ladder.setFacing(BlockFace.SOUTH);
                }
                if (forward.getZ() == 1){
                    ladder.setFacing(BlockFace.NORTH);
                }
                center.getWorld().getBlockAt(pos).setBlockData(ladder);
            }
            pos.add(new Vector(0, 1, 0));
        }


    }

    private static Vector rotateY(Vector vec) {
        float x1 = (float)(vec.getX() * Math.cos(Math.PI/2) + vec.getZ() * Math.sin(Math.PI/2));
        float z1 = (float)(-1 * vec.getX() * Math.sin(Math.PI/2) + vec.getZ() * Math.cos(Math.PI/2));
        return new Vector(x1, vec.getY(), z1);
    }
}
