package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension

/**
 * Placed in the package 'io.kotest.provided', ref. kotest.io/docs/framework/project-config.html
 */
class ProjectConfig : AbstractProjectConfig() {
    override val extensions = listOf(SpringExtension())
    override val isolationMode = IsolationMode.InstancePerRoot
}
