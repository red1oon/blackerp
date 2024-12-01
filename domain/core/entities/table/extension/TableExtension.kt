package org.blackerp.domain.table.extension

import org.blackerp.plugin.Extension
import org.blackerp.plugin.PluginId

interface TableExtension : Extension, TableExtensionPoint {
    override val pluginId: PluginId
}
