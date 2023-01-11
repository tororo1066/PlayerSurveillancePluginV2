package tororo1066.playersurveillanceplugin.data

import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin
import java.io.File

class LocationTpData {

    var file = ""
    val locations = ArrayList<Location>()
    var delay = 10000L

    companion object{
        fun loadFromYml(file: File): LocationTpData {
            val yml = YamlConfiguration.loadConfiguration(file)
            val data = LocationTpData()
            data.file = file.path.replace(PlayerSurveillancePlugin.plugin.dataFolder.path, "").replace("\\","/").dropLast(4)
            (yml.getList("locations") as? List<Location>)?.let { data.locations.addAll(it) }
            data.delay = yml.getLong("delay",10000L)
            return data
        }
    }
}