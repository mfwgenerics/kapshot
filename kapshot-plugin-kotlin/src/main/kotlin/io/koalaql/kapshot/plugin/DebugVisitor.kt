package io.koalaql.kapshot.plugin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class DebugVisitor(
    private val messages: MessageCollector
): IrElementVisitor<Unit, Unit> {
    override fun visitElement(element: IrElement, data: Unit) {
        messages.report(CompilerMessageSeverity.INFO, element.dump())
    }
}