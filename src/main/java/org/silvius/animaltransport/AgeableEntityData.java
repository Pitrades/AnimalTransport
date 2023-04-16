package org.silvius.animaltransport;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

public class AgeableEntityData {
    private final double health;
    private final int age;
    private final boolean isAdult;
    private final EntityType type;
    private boolean sheared;

    public AgeableEntityData(Ageable entity) {
        this.health = entity.getHealth();
        this.age = entity.getAge();
        this.isAdult = entity.isAdult();
        this.type = entity.getType();
        if(entity instanceof Sheep){
            this.sheared = ((Sheep) entity).isSheared();
        }
    }

    public double getHealth() {
        return this.health;
    }

    public int getAge() {
        return this.age;
    }

    public boolean isAdult() {
        return this.isAdult;
    }
    public EntityType getType() {
        return this.type;
    }

    public boolean isSheared() {
        return this.sheared;
    }



}
