package com.addavi.elify.tools

fun cleanTrackTitle(
    input: String,
    keepParentheses: Boolean = true,   // اگر true باشد چیزهای داخل () را نگه می‌داریم (مثل Live, Remix)
    keepBrackets: Boolean = false      // معمولاً چیزهای داخل [] تبلیغ/سایت است => false
): String {
    var s = input.trim()

    // Normalize Persian/Arabic variants and whitespace
    s = s
        .replace('ي', 'ی')
        .replace('ك', 'ک')
        .replace('\u200C', ' ') // ZWNJ -> space (optional)
        .replace(Regex("\\s+"), " ")

    // 1) Remove URLs (http/https/www)
    s = s.replace(Regex("(?i)\\bhttps?://\\S+\\b"), " ")
    s = s.replace(Regex("(?i)\\bwww\\.\\S+\\b"), " ")

    // 2) Remove bracketed parts
    if (!keepBrackets) {
        s = s.replace(Regex("\\[[^\\]]*\\]"), " ")
    }
    if (!keepParentheses) {
        s = s.replace(Regex("\\([^)]*\\)"), " ")
    }

    // 3) Remove naked domains anywhere (music-atc.com, upmusic.ir, foo.net, ...)
    // - allows hyphen and subdomains
    val domainRegex = Regex(
        pattern = "(?i)(?<=^|\\s|[\\-—–|])(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+(?:ir|com|net|org|info|me|io|co|tv|app|dev)\\b"
    )
    s = s.replace(domainRegex, " ")

    // 4) Remove common separators left behind around removed tokens
    // e.g. "Title -  " or "Title | "
    s = s.replace(Regex("\\s*([\\-—–|•·:])\\s*(?=\\s|$)"), " ")

    // 5) Final whitespace cleanup
    s = s.replace(Regex("\\s+"), " ").trim()

    // 6) Safety: if cleaning nuked everything, fallback to original trimmed input
    return if (s.isBlank()) input.trim() else s
}