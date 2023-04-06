package com.br.guilhermematthew.nowly.commons.bukkit.worldedit;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.actionbar.ActionBarAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.protocol.ProtocolGetter;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Constructions {

    private Player owner;
    private int blocksPerTick, maxBlocks, blockAtual, blocksPerSecond;
    private List<Location> locations;
    private List<Material> blocksToSet;
    private boolean finished, resetando, cancelTask, in18;
    private Random random;
    private Long started, startedNano;
    private HashMap<String, Material> blocksToReset;

    public Constructions(Player owner, List<Location> locations) {
        this.owner = owner;
        this.blocksPerTick = 2;
        this.blockAtual = 0;
        this.blocksPerSecond = blocksPerTick * 20;
        this.finished = false;
        this.resetando = false;
        this.cancelTask = false;
        this.locations = new ArrayList<>();
        this.blocksToReset = new HashMap<>();
        this.blocksToSet = new ArrayList<>();

        this.locations.addAll(locations);

        this.in18 = ProtocolGetter.getVersion(owner) > 5;
        this.maxBlocks = locations.size();
        this.random = new Random();
    }

    public void processBlocks() {
        for (int i = 0; i < maxBlocks; i++) {
            Location location = locations.get(this.blockAtual + i);
            String formated = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
            if (this.blocksToReset.containsKey(formated)) {
                continue;
            }
            Block block = location.getBlock();
            this.blocksToReset.put(formated, block.getType());
        }
    }

    public void start() {
        processBlocks();

        this.started = System.currentTimeMillis();
        this.startedNano = System.nanoTime();

        new BukkitRunnable() {
            public void run() {
                if (cancelTask) {
                    cancel();
                    sendMessageIfPlayerIsOnline("§e§lWORLDEDIT §fConstrução concluída em: §e" + DateUtils.getElapsed(started, startedNano), true);
                    blockAtual = 0;
                    finished = true;
                    return;
                }
                putBlock();
            }
        }.runTaskTimer(BukkitMain.getInstance(), 1L, 1L);
    }

    public void startRegress() {
        this.blockAtual = 0;
        this.resetando = true;
        new BukkitRunnable() {
            public void run() {
                if (cancelTask) {
                    cancel();
                    sendMessageIfPlayerIsOnline("§e§lWORLDEDIT §fConstrução resetada com sucesso.", true);
                    finished = true;
                    WorldEditManager.removeConstructionByUUID(getOwner().getUniqueId());
                    return;
                }
                regressBlock();
            }
        }.runTaskTimer(BukkitMain.getInstance(), 1L, 1L);
    }

    public void sendMessageIfPlayerIsOnline(String mensagem, boolean msg) {
        if (getOwner() != null && getOwner().isOnline()) {
            if (isIn18()) {
                ActionBarAPI.send(getOwner(), mensagem);
            }
            if (msg) {
                getOwner().sendMessage(mensagem);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void putBlock() {
        if (this.finished) {
            return;
        }
        if (this.blockAtual >= maxBlocks) {
            this.cancelTask = true;
            return;
        }

        int atualBlocksPertick = this.blocksPerTick;
        for (int i = 0; i < atualBlocksPertick; i++) {
            try {
                Location location = locations.get(this.blockAtual + i);
                Material escolhido = getRandomOurExactBlock();
                setAsyncBlock(location.getWorld(), location, escolhido.getId());
            } catch (IndexOutOfBoundsException e) {
            } catch (NullPointerException e) {
            }
        }

        sendMessageIfPlayerIsOnline("§e§lWORLDEDIT §fConstrução em andamento... §e(" +
                StringUtility.formatValue(blockAtual) + "/" + StringUtility.formatValue(maxBlocks) + ") " + StringUtility.formatValue(blocksPerSecond) + "b/ps", false);

        this.blockAtual += atualBlocksPertick;
    }

    @SuppressWarnings({"deprecation", "unused"})
    public void regressBlock() {
        if (this.finished) {
            return;
        }
        if (this.blockAtual >= maxBlocks) {
            this.cancelTask = true;
            return;
        }
        int atualBlocksPertick = this.blocksPerTick;
        for (int i = 0; i < atualBlocksPertick; i++) {
            try {
                Location location = locations.get(this.blockAtual + i);
                Block block = location.getBlock();

                String formated = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                Material material = blocksToReset.get(formated);
                setAsyncBlock(location.getWorld(), location, material.getId());
            } catch (IndexOutOfBoundsException e) {
            } catch (NullPointerException e) {
            }
        }
        sendMessageIfPlayerIsOnline("§e§lWORLDEDIT §fResetando construção... §e(" +
                StringUtility.formatValue(blockAtual) + "/" + StringUtility.formatValue(maxBlocks) + ") " + StringUtility.formatValue(blocksPerSecond) + "b/ps", false);
        this.blockAtual += atualBlocksPertick;
    }

    public void setAsyncBlock(World world, Location location, int blockId) {
        setAsyncBlock(world, location, blockId, (byte) 0);
    }

    public void setAsyncBlock(World world, Location location, int blockId, byte data) {
        setAsyncBlock(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockId, data);
    }

    public void setAsyncBlock(World world, int x, int y, int z, int blockId, byte data) {
        net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        int i = blockId + (data << 12);
        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(i);
        chunk.a(bp, ibd);
        w.notify(bp);
    }

    public Material getRandomOurExactBlock() {
        if (blocksToReset.size() == 1) {
            return blocksToSet.get(0);
        }
        return blocksToSet.get(random.nextInt(blocksToSet.size()));
    }

    public void setBlocksPerTick(int blocksPerTick) {
        this.blocksPerTick = blocksPerTick;
        this.blocksPerSecond = blocksPerTick * 20;
    }

    public void setBlocksToSet(List<Material> blocksToSet) {
        this.blocksToSet.addAll(blocksToSet);
    }
}