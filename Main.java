public class Main {
    public static void main(String[] args) {
        Gamestate gamestate = new Gamestate(0); 
        GameTick gameTick = new GameTick(gamestate);

        CookieClickerLayoutColored mainwindow = new CookieClickerLayoutColored(gamestate);
        
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
    }
}
