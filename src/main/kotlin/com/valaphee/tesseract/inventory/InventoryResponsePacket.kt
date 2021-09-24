/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class InventoryResponsePacket(
    var responses: Array<Response>
) : Packet {
    data class Response(
        val status: ResponseStatus,
        var requestId: Int,
        var windows: Array<ResponseWindow>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Response

            if (status != other.status) return false
            if (requestId != other.requestId) return false
            if (!windows.contentEquals(other.windows)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = status.hashCode()
            result = 31 * result + requestId
            result = 31 * result + windows.contentHashCode()
            return result
        }
    }

    enum class ResponseStatus {
        Ok,
        Error,
        InvalidRequestActionType,
        ActionRequestNotAllowed,
        ScreenHandlerEndRequestFailed,
        ItemRequestActionHandlerCommitFailed,
        InvalidRequestCraftActionType,
        InvalidCraftRequest,
        InvalidCraftRequestScreen,
        InvalidCraftResult,
        InvalidCraftResultIndex,
        InvalidCraftResultItem,
        InvalidItemNetId,
        MissingCreatedOutputContainer,
        FailedToSetCreatedItemOutputSlot,
        RequestAlreadyInProgress,
        FailedToInitSparseContainer,
        ResultTransferFailed,
        ExpectedItemSlotNotFullyConsumed,
        ExpectedAnywhereItemNotFullyConsumed,
        ItemAlreadyConsumedFromSlot,
        ConsumedTooMuchFromSlot,
        MismatchSlotExpectedConsumedItem,
        MismatchSlotExpectedConsumedItemNetIdVariant,
        FailedToMatchExpectedSlotConsumedItem,
        FailedToMatchExpectedAllowedAnywhereConsumedItem,
        ConsumedItemOutOfAllowedSlotRange,
        ConsumedItemNotAllowed,
        PlayerNotInCreativeMode,
        InvalidExperimentalRecipeRequest,
        FailedToCraftCreative,
        FailedToGetLevelRecipe,
        FailedToFindReceiptByNetId,
        MismatchedCraftingSize,
        MissingInputSparseContainer,
        MismatchedRecipeForInputGridItems,
        EmptyCraftResults,
        FailedToEnchant,
        MissingInputItem,
        InsufficientPlayerLevelToEnchant,
        MissingMaterialItem,
        MissingActor,
        UnknownPrimaryEffect,
        PrimaryEffectOutOfRange,
        PrimaryEffectUnavailable,
        SecondaryEffectOutOfRange,
        SecondaryEffectUnavailable,
        DstContainerEqualToCreatedOutputContainer,
        DstContainerAndSlotEqualToSrcContainerAndSlot,
        FailedToValidateSrcSlot,
        FailedToValidateDstSlot,
        InvalidAdjustedAmount,
        InvalidItemSetType,
        InvalidTransferAmount,
        CannotSwapItem,
        CannotPlaceItem,
        UnhandledItemSetType,
        InvalidRemovedAmount,
        InvalidRegion,
        CannotDropItem,
        CannotDestroyItem,
        InvalidSourceContainer,
        ItemNotConsumed,
        InvalidNumCrafts,
        InvalidCraftResultStackSize,
        CannotRemoveItem,
        CannotConsumeItem
    }

    data class ResponseWindow(
        val windowId: Int,
        val slots: Array<ResponseWindowSlot>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ResponseWindow

            if (windowId != other.windowId) return false
            if (!slots.contentEquals(other.slots)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = windowId
            result = 31 * result + slots.contentHashCode()
            return result
        }
    }

    data class ResponseWindowSlot(
        val slotId: Int,
        val hotbarSlot: Int,
        val count: Int,
        val netId: Int,
        val name: String,
        val damage: Int
    )

    override val id get() = 0x94

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(responses.size)
        responses.forEach {
            if (version >= 419) buffer.writeByte(it.status.ordinal) else buffer.writeBoolean(
                when (it.status) {
                    ResponseStatus.Ok -> true
                    else -> false
                }
            )
            buffer.writeVarInt(it.requestId)
            if (it.status == ResponseStatus.Ok) {
                buffer.writeVarUInt(it.windows.size)
                it.windows.forEach {
                    buffer.writeByte(it.windowId)
                    buffer.writeVarUInt(it.slots.size)
                    it.slots.forEach {
                        buffer.writeByte(it.slotId)
                        buffer.writeByte(it.hotbarSlot)
                        buffer.writeByte(it.count)
                        buffer.writeVarInt(it.netId)
                        if (version >= 422) {
                            buffer.writeString(it.name)
                            if (version >= 428) buffer.writeVarInt(it.damage)
                        }
                    }
                }
            }
        }
    }

    override fun handle(handler: PacketHandler) = handler.inventoryResponse(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InventoryResponsePacket

        if (!responses.contentEquals(other.responses)) return false

        return true
    }

    override fun hashCode() = responses.contentHashCode()
}
