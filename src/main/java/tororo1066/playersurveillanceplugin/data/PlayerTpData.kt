package tororo1066.playersurveillanceplugin.data

import org.bukkit.configuration.file.YamlConfiguration
import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin
import java.io.File
import java.util.*

class PlayerTpData {

    var file = ""
    var delay = 10000L
    val exclusionPlayers = ArrayList<UUID>()
    var distance = 3.0

    companion object{
        fun loadFromYml(file: File): PlayerTpData {
            val yml = YamlConfiguration.loadConfiguration(file)
            val data = PlayerTpData()
            data.file = file.path.replace(PlayerSurveillancePlugin.plugin.dataFolder.path, "").replace("\\","/").dropLast(4)
            data.exclusionPlayers.addAll(yml.getStringList("exclusionPlayers").map { UUID.fromString(it) })
            data.delay = yml.getLong("delay",10000L)
            data.distance = yml.getDouble("distance")
            return data
        }
    }
}