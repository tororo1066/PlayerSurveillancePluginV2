package tororo1066.playersurveillanceplugin.command

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.inventory.ClickType
import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin
import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin.Companion.sendPrefixMsg
import tororo1066.playersurveillanceplugin.data.LocationTpData
import tororo1066.playersurveillanceplugin.data.PlayerTpData
import tororo1066.playersurveillanceplugin.func.LocationTp
import tororo1066.playersurveillanceplugin.func.PlayerTp
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandArgType
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.utils.LocType
import tororo1066.tororopluginapi.utils.toLocString
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList

class PSPCommand: SCommand("psp",PlayerSurveillancePlugin.prefix.toString(),"psp.op") {

    private fun showHelp(sender: CommandSender){
        sender.sendMessage("§b====================PlayerSurveillancePlugin====================")
        sender.sendMessage("§a/psp task player <内部名> §7プレイヤーモードで開始します")
        sender.sendMessage("§a/psp task location <内部名> §7ロケーションーモードで開始します")
        sender.sendMessage("§a/psp task stop §7タスクをストップします")
        sender.sendMessage("§a/psp createTask player <内部名> §7プレイヤーのタスクを作成します")
        sender.sendMessage("§a/psp createTask location <内部名> §7ロケーションのタスクを作成します")
        sender.sendMessage("§a/psp edit help §7editのヘルプを開きます")
        sender.sendMessage("§b====================PlayerSurveillancePlugin==Author:tororo_1066")
    }

    private fun showEditHelp(sender: CommandSender){
        sender.sendMessage("§b====================PlayerSurveillancePlugin====================")
        sender.sendMessage("§a/psp edit camera §7カメラプレイヤーを表示します")
        sender.sendMessage("§a/psp edit camera <プレイヤー名> §7カメラプレイヤーを設定します")
        sender.sendMessage("§a/psp edit player <内部名> delay §7テレポートの周期を表示します")
        sender.sendMessage("§a/psp edit player <内部名> delay <ミリ秒> §7テレポートの周期を設定します")
        sender.sendMessage("§a/psp edit player <内部名> exclusion add <プレイヤー名> §7除外プレイヤーを追加します")
        sender.sendMessage("§a/psp edit player <内部名> exclusion remove <プレイヤー名> §7除外プレイヤーを削除します")
        sender.sendMessage("§a/psp edit player <内部名> exclusion list §7除外プレイヤー一覧を表示します")
        sender.sendMessage("§a/psp edit location <内部名> delay §7テレポートの周期を表示します")
        sender.sendMessage("§a/psp edit location <内部名> delay <ミリ秒> §7テレポートの周期を設定します")
        sender.sendMessage("§a/psp edit location <内部名> location add §7立っているところを位置として追加します")
        sender.sendMessage("§a/psp edit location <内部名> location remove §7ロケーションを削除するメニューを開きます")
        sender.sendMessage("§a/psp edit location <内部名> location list §7ロケーション一覧を表示します(クリックでtpできます)")
        sender.sendMessage("§b====================PlayerSurveillancePlugin==Author:tororo_1066")
    }

    init {
        setCommandNoFoundEvent { showHelp(it.sender) }
    }

    @SCommandBody
    val showHelp = command().setNormalExecutor { showHelp(it.sender) }

    @SCommandBody
    val showHelp2 = command().addArg(SCommandArg("help")).setNormalExecutor { showHelp(it.sender) }

    @SCommandBody
    val showEditHelp = command().addArg(SCommandArg("edit")).setNormalExecutor { showEditHelp(it.sender) }

    @SCommandBody
    val showEditHelp2 = command().addArg(SCommandArg("edit")).addArg(SCommandArg("help")).setNormalExecutor { showEditHelp(it.sender) }

    @SCommandBody
    val startPlayerTask = command().addArg(SCommandArg("task")).addArg(SCommandArg("player")).addArg(SCommandArg(PlayerSurveillancePlugin.playerData.keys)).setNormalExecutor {
        if (PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
            return@setNormalExecutor
        }
        if (PlayerSurveillancePlugin.cameraPlayer == null){
            it.sender.sendPrefixMsg(SStr("&cカメラプレイヤーが存在しません！"))
            return@setNormalExecutor
        }
        val data = PlayerSurveillancePlugin.playerData[it.args[2]]!!
        val task = PlayerTp(data)
        task.start()
        PlayerSurveillancePlugin.runningTask = task
        it.sender.sendPrefixMsg(SStr("&a動作を開始しました"))

    }

    @SCommandBody
    val startLocTask = command().addArg(SCommandArg("task")).addArg(SCommandArg("location")).addArg(SCommandArg(PlayerSurveillancePlugin.locationData.keys)).setNormalExecutor {
        if (PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
            return@setNormalExecutor
        }
        if (PlayerSurveillancePlugin.cameraPlayer == null){
            it.sender.sendPrefixMsg(SStr("&cカメラプレイヤーが存在しません！"))
            return@setNormalExecutor
        }
        val data = PlayerSurveillancePlugin.locationData[it.args[2]]!!
        val task = LocationTp(data)
        task.start()
        PlayerSurveillancePlugin.runningTask = task
        it.sender.sendPrefixMsg(SStr("&a動作を開始しました"))

    }

    @SCommandBody
    val endTask = command().addArg(SCommandArg("task")).addArg(SCommandArg("stop")).setNormalExecutor {
        if (!PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行していません！"))
            return@setNormalExecutor
        }
        PlayerSurveillancePlugin.runningTask?.cancel()
        PlayerSurveillancePlugin.isRunning = false
        it.sender.sendPrefixMsg(SStr("&a動作をストップしました"))
    }

    @SCommandBody
    val createPlayerTask = command().addArg(SCommandArg("createTask")).addArg(SCommandArg("player")).addArg(SCommandArg(SCommandArgType.STRING).addAlias("内部名")).setNormalExecutor {
        if (PlayerSurveillancePlugin.playerData.containsKey(it.args[2].split("/").last())){
            it.sender.sendPrefixMsg(SStr("&c既に存在しています！"))
            return@setNormalExecutor
        }
        if (PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
            return@setNormalExecutor
        }
        SJavaPlugin.sConfig.saveConfig(YamlConfiguration(),"players/${it.args[2]}")
        val data = PlayerTpData()
        data.file = "players/${it.args[2]}"
        PlayerSurveillancePlugin.playerData[it.args[2].split("/").last()] = data
        reloadSCommandBodies()
        it.sender.sendPrefixMsg(SStr("&a作成しました"))
    }

    @SCommandBody
    val createLocationTask = command().addArg(SCommandArg("createTask")).addArg(SCommandArg("location")).addArg(SCommandArg(SCommandArgType.STRING).addAlias("内部名")).setNormalExecutor {
        if (PlayerSurveillancePlugin.locationData.containsKey(it.args[2].split("/").last())){
            it.sender.sendPrefixMsg(SStr("&c既に存在しています！"))
            return@setNormalExecutor
        }
        if (PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
            return@setNormalExecutor
        }
        SJavaPlugin.sConfig.saveConfig(YamlConfiguration(),"locations/${it.args[2]}")
        val data = LocationTpData()
        data.file = "locations/${it.args[2]}"
        PlayerSurveillancePlugin.locationData[it.args[2].split("/").last()] = data
        reloadSCommandBodies()
        it.sender.sendPrefixMsg(SStr("&a作成しました"))
    }

    @SCommandBody
    val editCameraPlayerInfo = command().addArg(SCommandArg("edit")).addArg(SCommandArg("camera")).setNormalExecutor {
        if (PlayerSurveillancePlugin.cameraPlayer == null){
            it.sender.sendPrefixMsg(SStr("&cカメラプレイヤーが存在しません！"))
            return@setNormalExecutor
        }
        val p = Bukkit.getOfflinePlayer(PlayerSurveillancePlugin.cameraPlayer!!)
        it.sender.sendPrefixMsg(SStr("&aカメラプレイヤー"))
        it.sender.sendPrefixMsg(SStr("&a${p.name}"))
    }

    @SCommandBody
    val editCameraPlayer = command().addArg(SCommandArg("edit")).addArg(SCommandArg("camera")).addArg(SCommandArg().addAlias("プレイヤー名")).setNormalExecutor {
        val p = Bukkit.getOfflinePlayerIfCached(it.args[2])
        if (p == null){
            it.sender.sendMessage("&cプレイヤーが存在しません！")
            return@setNormalExecutor
        }
        PlayerSurveillancePlugin.cameraPlayer = p.uniqueId
        PlayerSurveillancePlugin.plugin.config.set("cameraPlayer",p.uniqueId.toString())
        PlayerSurveillancePlugin.plugin.saveConfig()
        it.sender.sendPrefixMsg(SStr("&aカメラプレイヤーを${it.args[2]}にしました"))
    }

    @SCommandBody
    val editPlayerDelayInfo = command().addArg(SCommandArg("edit")).addArg(SCommandArg("player")).addArg(SCommandArg(PlayerSurveillancePlugin.playerData.keys).addAlias("内部名")).addArg(SCommandArg("delay")).setNormalExecutor {
        val data = PlayerSurveillancePlugin.playerData[it.args[2]]!!
        it.sender.sendPrefixMsg(SStr("&a周期(1000=1秒)"))
        it.sender.sendPrefixMsg(SStr("&a${data.delay}"))
    }

    @SCommandBody
    val editPlayerDelay = command().addArg(SCommandArg("edit")).addArg(SCommandArg("player")).addArg(SCommandArg(PlayerSurveillancePlugin.playerData.keys).addAlias("内部名")).addArg(SCommandArg("delay")).addArg(
        SCommandArg(SCommandArgType.LONG).addAlias("周期(1000=1秒)")).setNormalExecutor {
        if (PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
            return@setNormalExecutor
        }
        val data = PlayerSurveillancePlugin.playerData[it.args[2]]!!
        data.delay = it.args[4].toLong()
        if (editYml(data.file) { yml ->
                yml.set("delay",data.delay)
            }){
            it.sender.sendPrefixMsg(SStr("&a${data.delay}ミリ秒にしました"))
        }
    }

    @SCommandBody
    val editPlayerExclusionList = command().addArg(SCommandArg("edit")).addArg(SCommandArg("player")).addArg(SCommandArg(PlayerSurveillancePlugin.playerData.keys).addAlias("内部名")).addArg(SCommandArg("exclusion")).setNormalExecutor {
        it.sender.sendPrefixMsg(SStr("&a除外設定に含まれているプレイヤー一覧"))
        for (exclusion in PlayerSurveillancePlugin.playerData[it.args[2]]!!.exclusionPlayers){
            it.sender.sendPrefixMsg(SStr("&a${Bukkit.getOfflinePlayer(exclusion).name}"))
        }
    }

    @SCommandBody
    val editPlayerExclusionAdd = command().addArg(SCommandArg("edit")).addArg(SCommandArg("player")).addArg(SCommandArg(PlayerSurveillancePlugin.playerData.keys).addAlias("内部名")).addArg(SCommandArg("exclusion"))
        .addArg(SCommandArg("add")).addArg(SCommandArg().addAlias("プレイヤー")).setNormalExecutor {
        if (PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
            return@setNormalExecutor
        }
        val player = Bukkit.getOfflinePlayerIfCached(it.args[5])
        if (player == null){
            it.sender.sendPrefixMsg(SStr("&cプレイヤーが存在しません！"))
            return@setNormalExecutor
        }
        val data = PlayerSurveillancePlugin.playerData[it.args[2]]!!
        data.exclusionPlayers.add(player.uniqueId)
        if (editYml(data.file) { yml ->
                val list = yml.getStringList("exclusionPlayers")
                list.add(player.uniqueId.toString())
                yml.set("exclusionPlayers",list)
            }){
            it.sender.sendPrefixMsg(SStr("&a${player.name}を追加しました"))
        }
    }

    @SCommandBody
    val editPlayerExclusionRemove = command().addArg(SCommandArg("edit")).addArg(SCommandArg("player")).addArg(SCommandArg(PlayerSurveillancePlugin.playerData.keys).addAlias("内部名")).addArg(SCommandArg("exclusion"))
        .addArg(SCommandArg("remove")).addArg(SCommandArg().addAlias("プレイヤー(UUIDでもok)").addChangeableAllowString {PlayerSurveillancePlugin.playerData[it[2]]?.exclusionPlayers?.map { map->map.toString() }?: listOf()}).setNormalExecutor {
            if (PlayerSurveillancePlugin.isRunning){
                it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
                return@setNormalExecutor
            }
            var player = Bukkit.getOfflinePlayerIfCached(it.args[5])
            if (player == null){
                if (UUID.fromString(it.args[5]) != null) {
                    player = Bukkit.getOfflinePlayer(UUID.fromString(it.args[5]))
                } else {
                    it.sender.sendPrefixMsg(SStr("&cプレイヤーが存在しません！"))
                    return@setNormalExecutor
                }
            }
            val data = PlayerSurveillancePlugin.playerData[it.args[2]]!!
            data.exclusionPlayers.remove(player.uniqueId)
            if (editYml(data.file) { yml ->
                    val list = yml.getStringList("exclusionPlayers")
                    list.remove(player.uniqueId.toString())
                    yml.set("exclusionPlayers",list)
                }){
                it.sender.sendPrefixMsg(SStr("&a${player.name}を削除しました"))
            }
        }

    @SCommandBody
    val editLocationDelayInfo = command().addArg(SCommandArg("edit")).addArg(SCommandArg("location")).addArg(SCommandArg(PlayerSurveillancePlugin.locationData.keys).addAlias("内部名")).addArg(SCommandArg("delay")).setNormalExecutor {
        val data = PlayerSurveillancePlugin.locationData[it.args[2]]!!
        it.sender.sendPrefixMsg(SStr("&a周期(1000=1秒)"))
        it.sender.sendPrefixMsg(SStr("&a${data.delay}"))
    }

    @SCommandBody
    val editLocationDelay = command().addArg(SCommandArg("edit")).addArg(SCommandArg("location")).addArg(SCommandArg(PlayerSurveillancePlugin.locationData.keys).addAlias("内部名")).addArg(SCommandArg("delay")).addArg(
        SCommandArg(SCommandArgType.LONG).addAlias("周期(1000=1秒)")).setNormalExecutor {
        if (PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
            return@setNormalExecutor
        }
        val data = PlayerSurveillancePlugin.locationData[it.args[2]]!!
        data.delay = it.args[4].toLong()
        if (editYml(data.file) { yml ->
                yml.set("delay",data.delay)
            }){
            it.sender.sendPrefixMsg(SStr("&a${data.delay}ミリ秒にしました"))
        }
    }

    @SCommandBody
    val editLocationList = command().addArg(SCommandArg("edit")).addArg(SCommandArg("location")).addArg(SCommandArg(PlayerSurveillancePlugin.locationData.keys).addAlias("内部名"))
        .addArg(SCommandArg("location")).addArg(SCommandArg("list")).setPlayerExecutor {
            val data = PlayerSurveillancePlugin.locationData[it.args[2]]!!
            it.sender.sendPrefixMsg(SStr("&aLoc一覧(クリックでテレポート)"))
            data.locations.forEach { loc ->
                it.sender.sendPrefixMsg(SStr(loc.toLocString(LocType.ALL_BLOCK_SPACE)).commandText("/tp ${loc.x} ${loc.y} ${loc.z} ${loc.yaw} ${loc.pitch}"))
            }
        }

    @SCommandBody
    val editLocationAdd = command().addArg(SCommandArg("edit")).addArg(SCommandArg("location")).addArg(SCommandArg(PlayerSurveillancePlugin.locationData.keys).addAlias("内部名"))
        .addArg(SCommandArg("location")).addArg(SCommandArg("add")).setPlayerExecutor {
        if (PlayerSurveillancePlugin.isRunning){
            it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
            return@setPlayerExecutor
        }
        val data = PlayerSurveillancePlugin.locationData[it.args[2]]!!
        data.locations.add(it.sender.location)
        if (editYml(data.file) { yml ->
                val locations = ArrayList((yml.getList("locations") as? List<Location>?:listOf()))
                locations.add(it.sender.location)
                yml.set("locations",locations)
            }){
            it.sender.sendPrefixMsg(SStr("&a追加しました"))
        }
    }

    @SCommandBody
    val editLocationRemove = command().addArg(SCommandArg("edit")).addArg(SCommandArg("location")).addArg(SCommandArg(PlayerSurveillancePlugin.locationData.keys).addAlias("内部名"))
        .addArg(SCommandArg("location")).addArg(SCommandArg("remove")).setPlayerExecutor {
            if (PlayerSurveillancePlugin.isRunning){
                it.sender.sendPrefixMsg(SStr("&cタスクを実行中です！ /psp task stopで終了してください"))
                return@setPlayerExecutor
            }
            val data = PlayerSurveillancePlugin.locationData[it.args[2]]!!
            object : LargeSInventory(PlayerSurveillancePlugin.plugin, "LocationRemove"){
                override fun renderMenu(): Boolean {
                    setResourceItems(ArrayList(data.locations.map { map-> SInventoryItem(Material.GRASS_BLOCK).setDisplayName(map.toLocString(LocType.ALL_BLOCK_SPACE)).addLore("§cシフト左クリックで削除").setCanClick(false).setClickEvent { e ->
                        if (e.click != ClickType.SHIFT_LEFT)return@setClickEvent
                        data.locations.remove(map)
                        if (editYml(data.file) { yml ->
                                val locations = ArrayList((yml.getList("locations") as? List<Location>?:listOf()))
                                locations.remove(map)
                                yml.set("locations",locations)
                            }){
                            it.sender.sendPrefixMsg(SStr("&a削除しました"))
                            allRenderMenu()
                        }
                    } }))
                    return true
                }
            }.open(it.sender)
        }

    private fun editYml(path: String, func: Consumer<YamlConfiguration>): Boolean {
        val config = SJavaPlugin.sConfig.getConfig(path)?:return false
        func.accept(config)
        return SJavaPlugin.sConfig.saveConfig(config, path)
    }

}