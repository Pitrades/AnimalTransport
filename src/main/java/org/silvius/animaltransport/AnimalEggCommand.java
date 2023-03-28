package org.silvius.animaltransport;

import com.google.gson.Gson;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

    public static ChatColor getLoreColor(){
        return loreColor;
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
                        entity2.teleport(IntersectionUtils.getIntersection(player.getEyeLocation(), event.getClickedBlock(), event.getBlockFace(), 0d).toLocation(player.getWorld()));
                        entity2.setGravity(true);
                        ((LivingEntity) entity2).setAI(true);
                        ((LivingEntity) entity2).setInvulnerable(false);
                        return;
                    }

                    //player.getWorld().spawn(IntersectionUtils.getIntersection(player.getEyeLocation(), event.getClickedBlock(), event.getBlockFace(), 0d).toLocation(player.getWorld()), type.getEntityClass());
                    //SerializeEntities.spawnEntity(IntersectionUtils.getIntersection(player.getEyeLocation(), event.getClickedBlock(), event.getBlockFace(), 0d).toLocation(player.getWorld()), storedAnimal);

                    UUID uniqueId = UUID.fromString(storedAnimal);
                    Entity entity2 = getEntityByUniqueId(uniqueId);
                    entity2.teleport(IntersectionUtils.getIntersection(player.getEyeLocation(), event.getClickedBlock(), event.getBlockFace(), 0d).toLocation(player.getWorld()));
                    entity2.setGravity(true);
                    ((LivingEntity) entity2).setAI(true);
                    ((LivingEntity) entity2).setInvulnerable(false);


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
    @EventHandler
    public void playerInteractEntityEvent(PlayerInteractEntityEvent event){
        EquipmentSlot hand = event.getHand();
        if (hand != null && !hand.equals(EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        Entity entity = player.getTargetEntity(5);
        if(player.getInventory().getItemInMainHand().getType()==Material.AIR){return;}
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(AnimalTransport.getPlugin(), "tier");
        if(entity==null){return;}
        if(entity instanceof Monster || !isValidEntity(entity)){return;}
        if(data.has(namespacedKey)) {
            String storedAnimal = data.get(namespacedKey, PersistentDataType.STRING);
            if(!Objects.equals(storedAnimal, "")){
                return;
            }

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AnimalTransport.getPlugin(), new Runnable(){
                public void run(){
                    //entity.remove();
                    entity.teleport(entity.getLocation().add(0, 100, 0));
                    entity.setGravity(false);
                    ((LivingEntity) entity).setAI(false);
                    ((LivingEntity) entity).setInvulnerable(true);
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
        if(entity instanceof Sheep || entity instanceof Cow || entity instanceof Pig || entity instanceof Chicken)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public Entity getEntityByUniqueId(UUID uniqueId) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(uniqueId))
                    return entity;
            }
        }

        return null;
    }
}
