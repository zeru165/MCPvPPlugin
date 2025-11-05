@echo off
title MCPvP Plugin Builder
echo ===============================
echo   Minecraft PvP Plugin Builder
echo ===============================
echo.

REM 1. JDK 설치 확인
javac -version >nul 2>&1
if errorlevel 1 (
    echo [!] Java JDK가 설치되어 있지 않습니다.
    echo 설치 후 다시 실행해주세요.
    pause
    exit /b
)

REM 2. 소스코드 작성
echo 소스코드 생성 중...
(
echo package mc.pvpplugin;
echo.
echo import org.bukkit.Bukkit;
echo import org.bukkit.ChatColor;
echo import org.bukkit.Material;
echo import org.bukkit.World;
echo import org.bukkit.WorldBorder;
echo import org.bukkit.command.Command;
echo import org.bukkit.command.CommandSender;
echo import org.bukkit.enchantments.Enchantment;
echo import org.bukkit.entity.Player;
echo import org.bukkit.event.EventHandler;
echo import org.bukkit.event.Listener;
echo import org.bukkit.event.entity.EntityDamageEvent;
echo import org.bukkit.event.entity.PlayerDeathEvent;
echo import org.bukkit.event.player.PlayerMoveEvent;
echo import org.bukkit.event.player.PlayerJoinEvent;
echo import org.bukkit.event.inventory.PrepareItemCraftEvent;
echo import org.bukkit.inventory.ItemStack;
echo import org.bukkit.inventory.ShapedRecipe;
echo import org.bukkit.inventory.meta.ItemMeta;
echo import org.bukkit.plugin.java.JavaPlugin;
echo import org.bukkit.scheduler.BukkitRunnable;
echo import org.bukkit.attribute.Attribute;
echo import java.util.*;
echo.
echo public class MCPvPPlugin extends JavaPlugin implements Listener {
echo.
echo     private boolean gameStarted = false;
echo     private boolean deathmatch = false;
echo     private Set\<UUID\> usedFreeItem = new HashSet<>();
echo     private int day = 1;
echo.
echo     @Override
echo     public void onEnable() {
echo         Bukkit.getPluginManager().registerEvents(this, this);
echo         getLogger().info("MCPvPPlugin enabled!");
echo         registerGoldenAppleRecipe();
echo     }
echo.
echo     private void registerGoldenAppleRecipe() {
echo         ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
echo         ShapedRecipe recipe = new ShapedRecipe(item);
echo         recipe.shape("GGG","GAG","GGG");
echo         recipe.setIngredient('G', Material.GOLD_NUGGET);
echo         recipe.setIngredient('A', Material.APPLE);
echo         Bukkit.addRecipe(recipe);
echo     }
echo.
echo     @Override
echo     public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
echo         if (!(sender instanceof Player)) return true;
echo         Player p = (Player)sender;
echo.
echo         if (cmd.getName().equalsIgnoreCase("start")) {
echo             if (gameStarted) { p.sendMessage(ChatColor.RED + "이미 게임이 시작되었습니다!"); return true; }
echo             gameStarted = true;
echo             startGame(p);
echo             return true;
echo         }
echo.
echo         if (cmd.getName().equalsIgnoreCase("freeitem")) {
echo             if (usedFreeItem.contains(p.getUniqueId())) {
echo                 p.sendMessage(ChatColor.RED + "이미 사용했습니다!");
echo                 return true;
echo             }
echo             usedFreeItem.add(p.getUniqueId());
echo             p.setLevel(50);
echo             p.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 30));
echo             p.getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE, 1));
echo             p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 32));
echo             p.sendMessage(ChatColor.GREEN + "아이템이 지급되었습니다!");
echo             return true;
echo         }
echo         return false;
echo     }
echo.
echo     private void startGame(Player p) {
echo         World w = p.getWorld();
echo         WorldBorder border = w.getWorldBorder();
echo         border.setCenter(p.getLocation());
echo         border.setSize(1000);
echo.
echo         Bukkit.broadcastMessage(ChatColor.GOLD + "게임이 시작되었습니다!");
echo.
echo         new BukkitRunnable() {
echo             public void run() {
echo                 if (day >= 10) {
echo                     startDeathmatch();
echo                     cancel();
echo                     return;
echo                 }
echo                 day++;
echo                 Bukkit.broadcastMessage(ChatColor.YELLOW + "현재 일수: " + day + "일차");
echo             }
echo         }.runTaskTimer(this, 20 * 60 * 20, 20 * 60 * 20);
echo     }
echo.
echo     private void startDeathmatch() {
echo         deathmatch = true;
echo         Bukkit.broadcastMessage(ChatColor.RED + "데스매치 시작!");
echo.
echo         new BukkitRunnable() {
echo             double size = 1000;
echo             public void run() {
echo                 if (size <= 50) { cancel(); return; }
echo                 size -= 200;
echo                 for (World w : Bukkit.getWorlds()) {
echo                     w.getWorldBorder().setSize(size);
echo                 }
echo                 Bukkit.broadcastMessage(ChatColor.DARK_RED + "자기장이 줄어듭니다! 현재 크기: " + size);
echo             }
echo         }.runTaskTimer(this, 0L, 20 * 60 * 5);
echo     }
echo.
echo     @EventHandler
echo     public void onDeath(PlayerDeathEvent e) {
echo         Player killer = e.getEntity().getKiller();
echo         if (killer != null && !deathmatch) {
echo             double maxHealth = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
echo             killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth + 5);
echo             killer.sendMessage(ChatColor.GREEN + "최대 체력이 +5 증가했습니다!");
echo         }
echo     }
echo.
echo     @EventHandler
echo     public void onFall(EntityDamageEvent e) {
echo         if (e.getCause() == EntityDamageEvent.DamageCause.FALL && e.getEntity() instanceof Player) {
echo             e.setCancelled(true);
echo             Player p = (Player)e.getEntity();
echo             p.setFoodLevel(Math.max(0, p.getFoodLevel() - 3));
echo         }
echo     }
echo }
) > MCPvPPlugin.java

REM 3. plugin.yml 작성
(
echo name: MCPvPPlugin
echo main: mc.pvpplugin.MCPvPPlugin
echo version: 1.0
echo api-version: 1.20
echo commands:
echo ^  start:
echo ^    description: 게임 시작
echo ^  freeitem:
echo ^    description: 무료 아이템 지급
) > plugin.yml

REM 4. 컴파일
echo 컴파일 중...
mkdir build
javac -cp paper-1.20.4-499.jar -d build MCPvPPlugin.java
if errorlevel 1 (
    echo [!] 컴파일 오류!
    pause
    exit /b
)

REM 5. JAR 생성
cd build
jar cf ../MCPvPPlugin-1.20.4.jar .
cd ..
jar uf MCPvPPlugin-1.20.4.jar plugin.yml

echo ===============================
echo ✅ 플러그인 생성 완료!
echo plugins 폴더에 넣고 사용하세요.
echo ===============================
pause
