/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util

class Whitelist<E>(private var isWhitelist: Boolean, private var list: Collection<E>) {

    fun isWhitelisted(`object`: E): Boolean {
        return isWhitelist == list.contains(`object`)
    }
}