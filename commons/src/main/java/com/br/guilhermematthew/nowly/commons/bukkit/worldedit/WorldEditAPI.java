package com.br.guilhermematthew.nowly.commons.bukkit.worldedit;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class WorldEditAPI {

    public static void setAsyncBlock(World world, Location location, int blockId) {
        setAsyncBlock(world, location, blockId, (byte) 0);
    }

    public static void setAsyncBlock(World world, Location location, int blockId, byte data) {
        setAsyncBlock(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockId, data);
    }

    public static void setAsyncBlock(World world, int x, int y, int z, int blockId, byte data) {
        net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        int i = blockId + (data << 12);
        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(i);
        chunk.a(bp, ibd);
        w.notify(bp);
    }
}