package tororo1066.playersurveillanceplugin

import org.bukkit.command.CommandSender
import tororo1066.playersurveillanceplugin.data.LocationTpData
import tororo1066.playersurveillanceplugin.data.PlayerTpData
import tororo1066.playersurveillanceplugin.func.AbstractFunc
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import java.util.*

class PlayerSurveillancePlugin: SJavaPlugin(UseOption.SConfig) {

    companion object{
        lateinit var plugin: PlayerSurveillancePlugin
        var cameraPlayer: UUID? = null
        var isRunning = false
        val prefix = SStr("&f&l[&a&lP&7&lS&4&lP&f&l&c&lv2&f&l]&r")
        var runningTask: AbstractFunc? = null
        val locationData = HashMap<String, LocationTpData>()
        val playerData = HashMap<String, PlayerTpData>()
        fun CommandSender.sendPrefixMsg(msg: SStr){
            this.sendMessage(prefix.toPaperComponent().append(msg.toPaperComponent()))
        }
    }

    override fun onStart() {
        plugin = this

        UsefulUtility.sTry({
            cameraPlayer = UUID.fromString(config.getString("cameraPlayer"))
        },{})

        val locations = sConfig.loadAllFiles("locations").filter { it.extension == "yml" }
        locations.forEach {
            locationData[it.nameWithoutExtension] = LocationTpData.loadFromYml(it)
        }

        val players = sConfig.loadAllFiles("players").filter { it.extension == "yml" }
        players.forEach {
            playerData[it.nameWithoutExtension] = PlayerTpData.loadFromYml(it)
        }
    }
}