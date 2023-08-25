package ro.davidarsene.leitmotif

import android.content.om.OverlayInfo

fun OverlayInfo.fullName(): String =
    if (isFabricated) "$packageName:$overlayName" else packageName

fun OverlayInfo.shortName(): String =
    if (isFabricated) overlayName else packageName
