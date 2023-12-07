package me.mika.midomikasiegesafebaseshield.Tasks;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleTrail extends BukkitRunnable {

    private final Location startLocation;
    private final Location endLocation;
    private final int particleAmount;

    public ParticleTrail(Location startLocation, Location endLocation, int particleAmount) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.particleAmount = particleAmount;
    }

    @Override
    public void run() {
        World world = startLocation.getWorld();

        double startX = startLocation.getX();
        double startY = startLocation.getY();
        double startZ = startLocation.getZ();

        double endX = endLocation.getX();
        double endY = endLocation.getY();
        double endZ = endLocation.getZ();

        double deltaX = (endX - startX) / particleAmount;
        double deltaY = (endY - startY) / particleAmount;
        double deltaZ = (endZ - startZ) / particleAmount;

        for (int i = 0; i < particleAmount; i++) {
            double currentX = startX + i * deltaX;
            double currentY = startY + i * deltaY;
            double currentZ = startZ + i * deltaZ;

            Location particleLocation = new Location(world, currentX, currentY, currentZ);

            // 产生粒子
            world.spawnParticle(Particle.REDSTONE, particleLocation, 0, 0, 0, 0, 0);
        }
    }

    public static void main(String[] args) {
        // 示例用法
        Location startLocation = new Location(Bukkit.getWorld("world"), 10, 20, 30);
        Location endLocation = new Location(Bukkit.getWorld("world"), 15, 25, 35);

        // 创建并运行粒子轨迹
        ParticleTrail particleTrail = new ParticleTrail(startLocation, endLocation, 50);
        particleTrail.runTaskTimer(SiegeSafeBaseShield.getPlugin(), 0L, 1L); // pluginInstance 替换为你的插件实例
    }
}

