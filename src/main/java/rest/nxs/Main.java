package rest.nxs;

import rest.nxs.config.Config;

public class Main {
    public static void main(String[] args) {
        Config config = Config.load("config.json");

        config.set("user.1.name", "Niko");
        config.set("user.1.age", "18");

        config.set("user.2.name", "Julina");
        config.set("user.2.age", "17");

        config.save();

        System.out.printf(config.getString("user.1.name", "1"));

        config.set("user.1.name", "...");
        config.save();
        System.out.printf(config.getString("user.1.name", "1"));
    }
}