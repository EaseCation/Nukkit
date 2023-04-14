package cn.nukkit.permission;

import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class BanList {

    private Map<String, BanEntry> list = new Object2ObjectLinkedOpenHashMap<>();

    private final String file;

    private boolean enable = true;

    public BanList(String file) {
        this.file = file;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, BanEntry> getEntires() {
        removeExpired();
        return this.list;
    }

    public boolean isBanned(String name) {
        if (!this.isEnable() || name == null) {
            return false;
        } else {
            this.removeExpired();

            return this.list.containsKey(name.toLowerCase());
        }
    }

    public void add(BanEntry entry) {
        this.list.put(entry.getName(), entry);
        this.save();
    }

    public BanEntry addBan(String target) {
        return this.addBan(target, null);
    }

    public BanEntry addBan(String target, String reason) {
        return this.addBan(target, reason, null);
    }

    public BanEntry addBan(String target, String reason, Date expireDate) {
        return this.addBan(target, reason, expireDate, null);
    }

    public BanEntry addBan(String target, String reason, Date expireDate, String source) {
        BanEntry entry = new BanEntry(target);
        entry.setSource(source != null ? source : entry.getSource());
        entry.setExpirationDate(expireDate);
        entry.setReason(reason != null ? reason : entry.getReason());

        this.add(entry);

        return entry;
    }

    public void remove(String name) {
        name = name.toLowerCase();
        if (this.list.remove(name) != null) {
            this.save();
        }
    }


    public void removeExpired() {
        for (String name : new ObjectArrayList<>(this.list.keySet())) {
            BanEntry entry = this.list.get(name);
            if (entry.hasExpired()) {
                list.remove(name);
            }
        }
    }

    public void load() {
        this.list = new Object2ObjectLinkedOpenHashMap<>();
        File file = new File(this.file);
        try {
            if (!file.exists()) {
                file.createNewFile();
                this.save();
            } else {
                List<Map<String, String>> list = new Gson().fromJson(Utils.readFile(this.file), new TypeToken<LinkedList<Object2ObjectRBTreeMap<String, String>>>() {
                }.getType());
                for (Map<String, String> map : list) {
                    BanEntry entry = BanEntry.fromMap(map);
                    this.list.put(entry.getName(), entry);
                }
            }
        } catch (IOException e) {
            log.error("Could not load ban list: ", e);
        }

    }

    public void save() {
        this.removeExpired();

        try {
            File file = new File(this.file);
            if (!file.exists()) {
                file.createNewFile();
            }

            List<Map<String, String>> list = new LinkedList<>();
            for (BanEntry entry : this.list.values()) {
                list.add(entry.getMap());
            }
            Utils.writeFile(this.file, new ByteArrayInputStream(new GsonBuilder().setPrettyPrinting().create().toJson(list).getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            log.error("Could not save ban list ", e);
        }
    }
}
