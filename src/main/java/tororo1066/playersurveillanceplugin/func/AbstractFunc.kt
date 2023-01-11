package tororo1066.playersurveillanceplugin.func

import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin
import tororo1066.tororopluginapi.sEvent.SEvent

abstract class AbstractFunc: Thread() {
    protected val sEvent = SEvent(PlayerSurveillancePlugin.plugin)

    fun cancel(){
        sEvent.unregisterAll()
        interrupt()
    }
}