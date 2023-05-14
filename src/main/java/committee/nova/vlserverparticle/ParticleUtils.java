package committee.nova.vlserverparticle;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet63WorldParticles;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.io.*;

@SuppressWarnings("unused")
public class ParticleUtils {
    public static Packet63WorldParticles getParticlePacket(
            String particleName,
            double posX, double posY, double posZ,
            double offsetX, double offsetY, double offsetZ,
            double speed, int quantity) throws IOException {
        final Packet63WorldParticles particles = new Packet63WorldParticles();
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(os);
        Packet.writeString(particleName, dos);
        dos.writeFloat((float) posX);
        dos.writeFloat((float) posY);
        dos.writeFloat((float) posZ);
        dos.writeFloat((float) offsetX);
        dos.writeFloat((float) offsetY);
        dos.writeFloat((float) offsetZ);
        dos.writeFloat((float) speed);
        dos.writeInt(quantity);
        particles.readPacketData(new DataInputStream(new ByteArrayInputStream(os.toByteArray())));
        return particles;
    }

    public static Packet63WorldParticles getParticlePacketOrNull(
            String particleName,
            double posX, double posY, double posZ,
            double offsetX, double offsetY, double offsetZ,
            double speed, int quantity) {
        try {
            return getParticlePacket(particleName, posX, posY, posZ, offsetX, offsetY, offsetZ, speed, quantity);
        } catch (IOException ignored) {
            return null;
        }
    }

    public static void sendParticle(World world, Packet63WorldParticles particle) {
        if (particle == null) return;
        if (world instanceof WorldServer)
            PacketDispatcher.sendPacketToAllInDimension(particle, world.provider.dimensionId);
    }

    public static void sendParticle(
            World world, String particleName,
            double posX, double posY, double posZ,
            double offsetX, double offsetY, double offsetZ,
            double speed, int quantity) {
        if (world instanceof WorldServer) sendParticle(world, getParticlePacketOrNull(particleName, posX, posY, posZ,
                offsetX, offsetY, offsetZ, speed, quantity));
        else world.spawnParticle(particleName, posX, posY, posZ, offsetX, offsetY, offsetZ);
    }

    public static void sendParticleToNearby(
            World world, double range, String particleName,
            double posX, double posY, double posZ,
            double offsetX, double offsetY, double offsetZ,
            double speed, int quantity) {
        final Packet63WorldParticles particles = getParticlePacketOrNull(particleName, posX, posY, posZ,
                offsetX, offsetY, offsetZ, speed, quantity);
        if (particles == null) return;
        if (world instanceof WorldServer)
            PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, range, world.provider.dimensionId, particles);
        else world.spawnParticle(particleName, posX, posY, posZ, offsetX, offsetY, offsetZ);
    }
}
