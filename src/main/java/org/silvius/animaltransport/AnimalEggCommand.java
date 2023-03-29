package org.silvius.animaltransport;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class AnimalEggCommand  implements CommandExecutor, Listener {
    static ChatColor loreColor = ChatColor.LIGHT_PURPLE;
    static Location teleportLocation = new Location(Bukkit.getWorlds().get(0), 1000, 1000, 1000);
    static Chunk chunk = teleportLocation.getChunk();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();

            if(!player.hasPermission("animaltransport.transportei")){
                commandSender.sendMessage(ChatColor.RED+"Keine Berechtigung");
                return true;
            }
            ItemStack stack = new ItemStack(Material.POPPED_CHORUS_FRUIT);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Transportei");
            ArrayList<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add(loreColor + "Kein Tier gefangen");
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey namespacedKey = new NamespacedKey(AnimalTransport.getPlugin(), "tier");
            data.set(namespacedKey, PersistentDataType.STRING, "");
            meta.setLore(lore);
            stack.setItemMeta(meta);
            player.getInventory().addItem(stack);

        }
        return true;

    }


    @EventHandler
    public static void onEntityDamaged(EntityDamageByEntityEvent event){
        if (event.getEntity().getLocation().toVector().distance(teleportLocation.toVector())<0.5 && event.getEntity().isInvulnerable()){event.setCancelled(true);}
    }


    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Entity entity = player.getTargetEntity(5);
        if(player.getInventory().getItemInMainHand().getType()==Material.AIR){return;}
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(AnimalTransport.getPlugin(), "tier");
        if(data.has(namespacedKey)) {
            String storedAnimal = data.get(namespacedKey, PersistentDataType.STRING);
            if(!Objects.equals(storedAnimal, "")){
                if(entity==null && event.getClickedBlock()!=null){
                    //EntityType type = EntityType.valueOf(storedAnimal);

                    if(item.getAmount()>1){
                        ItemStack newItem = item.clone();
                        newItem.setAmount(1);

                        ItemMeta newMeta = newItem.getItemMeta();
                        PersistentDataContainer newData = newMeta.getPersistentDataContainer();
                        newData.set(namespacedKey, PersistentDataType.STRING, "");
                        ArrayList< String > lore = new ArrayList < > ();
                        lore.add(" ");
                        lore.add(loreColor + "Kein Tier gefangen");
                        newMeta.setLore(lore);
                        newItem.setItemMeta(newMeta);
                        item.setAmount(item.getAmount()-1);
                        //player.getInventory().addItem(newItem);
                        UUID uniqueId = UUID.fromString(storedAnimal);
                        Entity entity2 = getEntityByUniqueId(uniqueId);
                        teleportEntity(false, entity2, IntersectionUtils.getIntersection(player.getEyeLocation(), event.getClickedBlock(), event.getBlockFace(), 0d).toLocation(player.getWorld()));
                        return;
                    }

                    UUID uniqueId = UUID.fromString(storedAnimal);
                    Entity entity2 = getEntityByUniqueId(uniqueId);
                    teleportEntity(false, entity2, IntersectionUtils.getIntersection(player.getEyeLocation(), event.getClickedBlock(), event.getBlockFace(), 0d).toLocation(player.getWorld()));
                    data.set(namespacedKey, PersistentDataType.STRING, "");
                    ArrayList< String > lore = new ArrayList < > ();
                    lore.add(" ");
                    lore.add(loreColor + "Kein Tier gefangen");
                    meta.setLore(lore);
                    item.setAmount(0);
                }
            }
        }
    }

    private static void teleportEntity(boolean isRemoved, Entity entity, Location location) {
        entity.setGravity(!isRemoved);
        ((LivingEntity) entity).setAI(!isRemoved);
        entity.setInvulnerable(isRemoved);
        if(isRemoved){entity.setFreezeTicks(Integer.MAX_VALUE);}
        else{entity.setFreezeTicks(0);}
        ((LivingEntity) entity).setInvisible(isRemoved);
        entity.setPersistent(isRemoved);
        entity.teleport(location);
    }

    @EventHandler
    public void playerInteractEntityEvent(PlayerInteractEntityEvent event){
        EquipmentSlot hand = event.getHand();
        if (!hand.equals(EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        Entity entity = player.getTargetEntity(5);
        if(player.getInventory().getItemInMainHand().getType()==Material.AIR){return;}
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(AnimalTransport.getPlugin(), "tier");

        if(entity==null){return;}
        if(entity instanceof Monster || !isValidEntity(entity)){return;}
        if (!(entity instanceof Ageable)){return;}
        if(entity instanceof Tameable && ((Tameable) entity).isTamed() && ((Tameable) entity).getOwner()!=event.getPlayer()){return;}
        if(entity.getCustomName()!=null){return;}

        if(data.has(namespacedKey)) {
            String storedAnimal = data.get(namespacedKey, PersistentDataType.STRING);
            if(!Objects.equals(storedAnimal, "")){
                return;
            }

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AnimalTransport.getPlugin(), new Runnable(){
                public void run(){
                    teleportEntity(true, entity, teleportLocation);

                }
            }, 1);
            if(item.getAmount()>1){
                ItemStack newItem = item.clone();
                newItem.setAmount(1);

                ItemMeta newMeta = newItem.getItemMeta();
                PersistentDataContainer newData = newMeta.getPersistentDataContainer();
                newData.set(namespacedKey, PersistentDataType.STRING, entity.getUniqueId().toString());
                ArrayList< String > lore = new ArrayList < > ();
                lore.add(" ");
                lore.add(loreColor + translateName(entity.getName())+" gefangen!");
                newMeta.setLore(lore);
                newItem.setItemMeta(newMeta);
                item.setAmount(item.getAmount()-1);
                player.getInventory().addItem(newItem);
                return;
            }

            data.set(namespacedKey, PersistentDataType.STRING, entity.getUniqueId().toString());
            ArrayList< String > lore = new ArrayList < > ();
            lore.add(" ");
            lore.add(loreColor + translateName(entity.getName())+" gefangen!");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    private String translateName(String name) {
        switch(name){
            case "Cow":
                return "Kuh";
            case "Chicken":
                return "Huhn";
            case "Sheep":
                return "Schaf";
            case "Horse":
                return "Pferd";
            default:
                return name;
        }}

    private boolean isValidEntity(Entity entity){
        return true;
    }


    public Entity getEntityByUniqueId(UUID uniqueId){
        chunk.load();
                for (Entity entity : chunk.getEntities()) {
                    if (entity.getUniqueId().equals(uniqueId))
                        return entity;
        }

        return null;
    }
}
