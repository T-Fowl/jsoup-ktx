package com.tfowl.jsoup.ktx

import org.jsoup.nodes.Document

fun Document.getJsonLinkingDataStrings(): List<String> {
    return select("script[type='application/ld+json']")
        .map { script -> script.html().trim() }
}