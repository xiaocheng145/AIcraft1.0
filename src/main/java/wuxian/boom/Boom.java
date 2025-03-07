package wuxian.boom;

        import org.bukkit.*;
        import org.bukkit.block.Block;
        import org.bukkit.enchantments.Enchantment;
        import org.bukkit.entity.*;
        import org.bukkit.event.EventHandler;
        import org.bukkit.event.Listener;
        import org.bukkit.event.block.*;
        import org.bukkit.event.entity.*;
        import org.bukkit.event.inventory.*;
        import org.bukkit.event.player.*;
        import org.bukkit.inventory.ItemStack;
        import org.bukkit.inventory.meta.Damageable;
        import org.bukkit.plugin.java.JavaPlugin;
        import org.bukkit.potion.PotionEffect;
        import org.bukkit.potion.PotionEffectType;
        import org.bukkit.util.Vector;

        import java.util.*;

public class Boom extends JavaPlugin implements Listener {

    private final Random random = new Random();
    private final Set<Material> forbiddenItems = new HashSet<>(Arrays.asList(
            Material.BEDROCK, Material.COMMAND_BLOCK, Material.BARRIER));
    private boolean apocalypseMode = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            // 每30秒触发一次全局随机事件
            triggerGlobalChaos();
        }, 600, 600);
    }

    // 核心混乱机制
    private void triggerGlobalChaos() {
        int event = random.nextInt(10);
        switch (event) {
            case 0 : getServer().getWorlds().forEach(w -> w.setTime(random.nextInt(24000)));
            case 1 : Bukkit.broadcastMessage(ChatColor.RED + "量子纠缠已启动！");
            case 2 : getServer().getOnlinePlayers().forEach(p ->
                    p.addPotionEffect(new PotionEffect(
                            PotionEffectType.values()[random.nextInt(PotionEffectType.values().length)],
                            200,
                            random.nextInt(5))));
            case 3 : apocalypseMode = !apocalypseMode;
            case 4 : getServer().getWorlds().forEach(w ->
                    w.getEntities().forEach(e ->
                            e.setVelocity(new Vector(
                                    random.nextDouble()*2-1,
                                    random.nextDouble()*2-1,
                                    random.nextDouble()*2-1))));
            case 5 : getServer().getWorlds().forEach(w ->
                    w.playEffect(randomLocation(w),
                            Effect.values()[random.nextInt(Effect.values().length)],
                            null));
            case 6 : getServer().getWorlds().forEach(w ->
                    w.strikeLightningEffect(randomLocation(w)));
            case 7 : getServer().getOnlinePlayers().forEach(p ->
                    p.getInventory().forEach(item ->
                            quantumEntanglement(item)));
            case 8 : getServer().getWorlds().forEach(w ->
                    w.getBlockAt(randomLocation(w)).setType(Material.TNT));
            case 9 : getServer().getOnlinePlayers().forEach(p ->
                    p.setGameMode(GameMode.values()[random.nextInt(GameMode.values().length)]));
        }
    }

    // 量子纠缠物品系统
    private void quantumEntanglement(ItemStack item) {
        if (item == null) return;
        if (random.nextDouble() < 0.3) {
            item.setType(Material.values()[random.nextInt(Material.values().length)]);
            item.setAmount(random.nextInt(64)+1);
            if (item.getItemMeta() instanceof Damageable) {
                Damageable meta = (Damageable) item.getItemMeta();
                meta.setDamage(random.nextInt(item.getType().getMaxDurability()));
                item.setItemMeta(meta);
            }
        }
    }

    // 方块破坏事件
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (apocalypseMode) {
            event.setCancelled(true);
            event.getBlock().getWorld().createExplosion(
                    event.getBlock().getLocation(),
                    random.nextFloat() * 10);
            return;
        }

        if (random.nextDouble() < 0.4) {
            Material newType = Material.values()[random.nextInt(Material.values().length)];
            event.getBlock().setType(newType);
            event.setCancelled(true);

            if (random.nextDouble() < 0.2) {
                event.getPlayer().getInventory().addItem(
                        new ItemStack(Material.DIAMOND, random.nextInt(5)));
                event.getPlayer().sendTitle("量子采矿奖励！", "", 10, 40, 10);
            }
        }
    }

    // 物品合成系统
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (random.nextDouble() < 0.7) {
            event.getInventory().setResult(new ItemStack(
                    Material.values()[random.nextInt(Material.values().length)],
                    random.nextInt(3)+1));

            if (random.nextDouble() < 0.1) {
                event.getWhoClicked().getWorld().createExplosion(
                        event.getWhoClicked().getLocation(),
                        4F,
                        true);
            }
        }
    }

    // 实体伤害事件
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (random.nextDouble() < 0.3) {
                event.setDamage(event.getDamage() * random.nextDouble() * 5);
                if (random.nextDouble() < 0.2) {
                    event.getEntity().sendMessage("时空扭曲导致伤害变异！");
                }
            }
        }
    }

    // 玩家移动事件
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (random.nextDouble() < 0.005) {
            player.teleport(randomLocation(player.getWorld()));
            player.sendMessage("你被量子传送了！");
        }

        if (apocalypseMode && random.nextDouble() < 0.1) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.LEVITATION,
                    100,
                    random.nextInt(3)));
        }
    }

    // 生物生成事件
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (random.nextDouble() < 0.4) {
            Entity entity = event.getEntity();
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(
                        PotionEffectType.SPEED,
                        Integer.MAX_VALUE,
                        5));

                entity.setCustomName("超速实体");
                entity.setGlowing(true);
            }

            if (random.nextDouble() < 0.1) {
                entity.getWorld().spawnEntity(
                        entity.getLocation(),
                        EntityType.values()[random.nextInt(EntityType.values().length)]);
            }
        }
    }

    // 随机位置生成器
    private Location randomLocation(World world) {
        return new Location(
                world,
                random.nextDouble() * 1000 - 500,
                random.nextDouble() * 256,
                random.nextDouble() * 1000 - 500);
    }

    // 玩家加入事件
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("欢迎来到量子地狱 " + event.getPlayer().getName());
        event.getPlayer().getInventory().addItem(
                new ItemStack(Material.BEDROCK, 64));
        event.getPlayer().addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                Integer.MAX_VALUE,
                1));
    }
}