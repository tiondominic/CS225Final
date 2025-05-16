public class Main {
    public static void main(String[] args) {
        Gamestate gamestate = new Gamestate(100000000000000L); 
        GameTick gameTick = new GameTick(gamestate);

        Gamewindow mainwindow = new Gamewindow(gamestate);
        
        // Add upgrades to the UI
        Upgrade cursor = new Upgrade("Cursor", 15, 0.1, gamestate, false);
        Upgrade grandma = new Upgrade("Grandma", 100, 1, gamestate, false);
        Upgrade farm = new Upgrade("Farm", 1100, 8, gamestate, false);
        Upgrade mine = new Upgrade("Mine", 12000, 47, gamestate, false);
        Upgrade factory = new Upgrade("Factory", 130000, 260, gamestate, false);
        Upgrade bank = new Upgrade("Bank", 1400000, 1400, gamestate, false);
        Upgrade temple = new Upgrade("Temple", 20000000, 7800, gamestate, false);
        Upgrade wizard_tower = new Upgrade("Wizard Tower", 330000000, 44000, gamestate, false);
        Upgrade shipment = new Upgrade("Shipment", 5100000000L, 260000, gamestate, false);
        Upgrade alchemy_lab = new Upgrade("Alchemy Lab", 75000000000L, 1600000, gamestate, false);
        Upgrade portal = new Upgrade("Portal", 1000000000000L, 10000000, gamestate, false);

        Upgrade time_machine = new Upgrade("Time Machine", 14000000000000L, 65000000, gamestate, false);
        Upgrade antimatter_condenser = new Upgrade("Antimatter Condenser", 170000000000000L, 430000000, gamestate, false);
        Upgrade prism = new Upgrade("Prism", 2100000000000000L, 2900000000L, gamestate, false);
        Upgrade chancemaker = new Upgrade("Chancemaker", 26000000000000000L, 21000000000L, gamestate, false);
        Upgrade fractal_engine = new Upgrade("Fractal Engine", 310000000000000000L, 150000000000L, gamestate, false);
        
        mainwindow.addUpgrade(cursor);
        mainwindow.addUpgrade(grandma);
        mainwindow.addUpgrade(farm);
        mainwindow.addUpgrade(mine);
        mainwindow.addUpgrade(factory);
        mainwindow.addUpgrade(bank);
        mainwindow.addUpgrade(temple);
        mainwindow.addUpgrade(wizard_tower);
        mainwindow.addUpgrade(shipment);
        mainwindow.addUpgrade(alchemy_lab);
        mainwindow.addUpgrade(portal);

        mainwindow.addUpgrade(time_machine);
        mainwindow.addUpgrade(antimatter_condenser);
        mainwindow.addUpgrade(prism);
        mainwindow.addUpgrade(chancemaker);
        mainwindow.addUpgrade(fractal_engine);
    }
}
