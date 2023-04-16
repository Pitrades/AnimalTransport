package org.silvius.animaltransport;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.MetadataValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.Yaml;

public class SerializeEntities {
    public static String serializeEntity(Entity entity)


    {
        AgeableEntityData entityData = new AgeableEntityData((Ageable) entity);

// serialize the PlayerData object to a JSON object using GSON
        Gson gson = new Gson();


        return gson.toJson(entityData);
    }

    public static void spawnEntity(Location location, String string){
        Gson gson = new Gson();
        AgeableEntityData entityData = gson.fromJson(string, AgeableEntityData.class);
        Ageable entity = (Ageable) location.getWorld().spawnEntity(location, entityData.getType());
        entity.setHealth(entityData.getHealth());
        entity.setAge(entityData.getAge());
        if(entityData.isAdult()){entity.setAdult();}
        if(entity instanceof Sheep){((Sheep) entity).setSheared(entityData.isSheared());}
    }}

